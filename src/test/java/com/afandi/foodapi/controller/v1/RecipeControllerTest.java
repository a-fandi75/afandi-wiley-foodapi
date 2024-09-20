package com.afandi.foodapi.controller.v1;

import com.afandi.foodapi.controller.ResourceNotFoundException;
import com.afandi.foodapi.dto.CaloriesResponse;
import com.afandi.foodapi.dto.recipe.Recipe;
import com.afandi.foodapi.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = RecipeController.class)
class RecipeControllerTest {

    @MockBean
    private RecipeService recipeService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(new RecipeController(recipeService)).build();
    }

    @Test
    void test_search_recipes_end_point_returns_results() {
        List<Recipe> mockRecipes = new ArrayList<>();
        mockRecipes.add(new Recipe());

        when(recipeService.search(any(Map.class))).thenReturn(Mono.just(mockRecipes));

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/api/v1/recipes/search")
                .queryParam("name", "mock")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Recipe.class)
            .hasSize(1)
            .consumeWith(response -> {
                List<Recipe> recipes = response.getResponseBody();
                assert recipes != null;
            });
    }

    @Test
    void search_recipes_by_name_and_calculate_calories_should_return_valid_calories_response() {
        CaloriesResponse mockCaloriesResponse = new CaloriesResponse();
        mockCaloriesResponse.setTotalCalories(100d);

        when(recipeService.getRecipeCalories(any(String.class), any())).thenReturn(Mono.just(mockCaloriesResponse));

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/api/v1/recipes/search/calories")
                .queryParam("name", "mock")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(CaloriesResponse.class)
            .consumeWith(response -> {
                CaloriesResponse caloriesResponse = response.getResponseBody();
                assert caloriesResponse != null;
                assert caloriesResponse.getTotalCalories() == 100;
            });
    }

    @Test
    void get_recipes_by_id_and_calculate_calories_should_return_valid_calories_response() {
        CaloriesResponse mockCaloriesResponse = new CaloriesResponse();
        mockCaloriesResponse.setTotalCalories(100d);

        when(recipeService.getRecipeCalories(anyLong(), any())).thenReturn(Mono.just(mockCaloriesResponse));

        webTestClient.get()
            .uri("/api/v1/recipes/search/1/calories")
            .exchange()
            .expectStatus().isOk()
            .expectBody(CaloriesResponse.class)
            .consumeWith(response -> {
                CaloriesResponse caloriesResponse = response.getResponseBody();
                assert caloriesResponse != null;
                assert caloriesResponse.getTotalCalories() == 100;
            });
    }

    @Test
    void get_recipes_by_invalid_id_and_calculate_calories_should_return_NotFound_response() {
        when(recipeService.getRecipeCalories(anyLong(), any()))
            .thenReturn(Mono.error(new ResourceNotFoundException(1L)));

        webTestClient.get()
            .uri("/api/v1/recipes/search/1/calories")
            .exchange()
            .expectStatus().isNotFound();
    }
}
