package com.afandi.foodapi.service.integration.spoonacular;

import com.afandi.foodapi.dto.recipe.Nutrition;
import com.afandi.foodapi.dto.recipe.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class SpoonacularAPITest {

    @Autowired
    private SpoonacularAPI spoonacularAPI;

    @Test
    void search_recipes_with_parameters_returns_result() {
        Map<String, String> params = Map.of("query", "burger", "number", "1");
        List<Recipe> result = spoonacularAPI.search(params).block();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getId() > 0);
    }

    @Test
    void search_recipes_including_recipe_nutrition_returns_result() {
        Map<String, String> params = Map.of("query", "burger", "addRecipeNutrition", "true");
        List<Recipe> result = spoonacularAPI.search(params).block();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertNotNull(result.get(0).getNutrition());
    }

    @Test
    void nutrition_by_id_with_a_valid_recipe_id_does_not_throw_exception() {
        assertDoesNotThrow(() -> spoonacularAPI.nutritionById(716408).block());
    }

    @Test
    void nutrition_by_id_with_an_invalid_recipe_id_throws_not_found_exception() {
        assertThrows(WebClientResponseException.NotFound.class, () -> spoonacularAPI.nutritionById(-1).block());
    }

    @Test
    void nutrition_by_id_returns_nutrition_information_with_ingredients() {
        Nutrition nutrition = spoonacularAPI.nutritionById(716408).block();

        assertNotNull(nutrition);
        assertNotNull(nutrition.getIngredients());
        assertFalse(nutrition.getIngredients().isEmpty());
    }
}