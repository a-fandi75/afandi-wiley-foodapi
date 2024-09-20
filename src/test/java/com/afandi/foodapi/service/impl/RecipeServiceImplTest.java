package com.afandi.foodapi.service.impl;

import com.afandi.foodapi.controller.ResourceNotFoundException;
import com.afandi.foodapi.dto.CaloriesResponse;
import com.afandi.foodapi.dto.recipe.Ingredient;
import com.afandi.foodapi.dto.recipe.Nutrient;
import com.afandi.foodapi.dto.recipe.Nutrition;
import com.afandi.foodapi.dto.recipe.Recipe;
import com.afandi.foodapi.service.integration.spoonacular.SpoonacularAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RecipeServiceImplTest {

    @Mock
    private SpoonacularAPI spoonacularAPI;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void search_recipes_returns_valid_results() {
        List<Recipe> mockRecipes = new ArrayList<>();

        for (long i = 1; i < 10; i++) {
            Recipe recipe = new Recipe();
            recipe.setId(i);
            mockRecipes.add(recipe);
        }

        when(spoonacularAPI.search(any(Map.class))).thenReturn(Mono.just(mockRecipes));

        Mono<List<Recipe>> result = recipeService.search(new HashMap<>());

        StepVerifier.create(result)
            .expectNext(mockRecipes)
            .verifyComplete();
    }

    @Test
    void search_recipe_by_name_and_calculate_calories_should_return_accurate_output() {
        List<Recipe> mockRecipes = new ArrayList<>();
        String[] calNames = new String[] {"bell pepper", "rice", "basil"};
        double[] calValues = new double[] {7.74, 102.7, 0.82};
        Nutrition recipeNutrition = buildMockNutrition(calNames, calValues);
        Recipe recipe = new Recipe();

        recipe.setNutrition(recipeNutrition);
        mockRecipes.add(recipe);

        when(spoonacularAPI.search(any(Map.class))).thenReturn(Mono.just(mockRecipes));

        Mono<CaloriesResponse> result = recipeService.getRecipeCalories("recipeName", new ArrayList<>());

        StepVerifier.create(result)
            .expectNextMatches(
                response -> response.getTotalCalories().equals(
                    recipeNutrition.getNutrients().stream()
                        .filter(n -> n.getName().equalsIgnoreCase("calories"))
                        .findFirst().get().getAmount()
                )
            )
            .verifyComplete();
    }

    @Test
    void search_recipe_by_name_with_0_results_and_calculate_calories_should_return_CaloriesResponse_with_error() {
        when(spoonacularAPI.search(any(Map.class))).thenReturn(Mono.just(new ArrayList<>()));

        Mono<CaloriesResponse> result = recipeService.getRecipeCalories("recipeName", new ArrayList<>());

        StepVerifier.create(result)
            .expectNextMatches(caloriesResponse -> caloriesResponse.getError() != null && caloriesResponse.getError().contains("No result was found"))
            .verifyComplete();
    }

    @Test
    void nutrition_by_id_successfully_calculate_calories_for_a_nutrition() {
        String[] calNames = new String[] {"bell pepper", "rice", "basil"};
        double[] calValues = new double[] {7.74, 102.7, 0.82};
        Nutrition mockNutrition = buildMockNutrition(calNames, calValues);

        when(spoonacularAPI.nutritionById(1L)).thenReturn(Mono.just(mockNutrition));

        Mono<CaloriesResponse> result = recipeService.getRecipeCalories(1L, new ArrayList<>());

        StepVerifier.create(result)
            .expectNextMatches(response -> response.getTotalCalories() == 111.25999999999999)
            .verifyComplete();
    }

    @Test
    void nutrition_by_id_throws_NotFoundException_in_case_of_invalid_id() {
        when(spoonacularAPI.nutritionById(1L))
            .thenReturn(Mono.error(WebClientResponseException.NotFound.create(404, "Not Found", null, null, null)));

        Mono<CaloriesResponse> result = recipeService.getRecipeCalories(1L, new ArrayList<>());

        StepVerifier.create(result)
            .expectErrorMatches(throwable -> throwable instanceof ResourceNotFoundException &&
                throwable.getMessage().contains("1")) // contains invalid recipe id
            .verify();
    }

    private Nutrition buildMockNutrition(String[] calNames, double[] calValues) {
        Nutrition result = new Nutrition();

        // add generic nutrients
        List<Nutrient> genericNutrients = new ArrayList<>();
        Nutrient genericCalories = new Nutrient();
        genericCalories.setName("Calories");
        genericCalories.setAmount(DoubleStream.of(calValues).sum()); // equals ingredients calories nutrient summed up
        genericNutrients.add(genericCalories);
        result.setNutrients(genericNutrients);

        // add separate ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        result.setIngredients(ingredients);

        for (int i = 0; i < calNames.length; i++) {
            Ingredient ingredient = new Ingredient();
            ingredient.setName(calNames[i]);
            ingredients.add(ingredient);

            List<Nutrient> ingNutrients = new ArrayList<>();
            Nutrient ingCalNutrient = new Nutrient();
            ingCalNutrient.setAmount(calValues[i]);
            ingCalNutrient.setName("Calories");
            ingNutrients.add(ingCalNutrient);

            ingredient.setNutrients(ingNutrients);
        }

        return result;
    }
}