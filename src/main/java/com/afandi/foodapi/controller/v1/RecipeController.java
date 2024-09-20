package com.afandi.foodapi.controller.v1;

import com.afandi.foodapi.dto.CaloriesResponse;
import com.afandi.foodapi.dto.recipe.Recipe;
import com.afandi.foodapi.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/recipes/", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecipeController {

    private static final String PARAM_QUERY = "query",
        PARAM_INCLUDE_INGREDIENTS = "includeIngredients",
        PARAM_ADD_RECIPE_INFORMATION = "addRecipeInformation",
        PARAM_ADD_RECIPE_NUTRITION = "addRecipeNutrition",
        PARAM_MIN_SERVINGS = "minServings",
        PARAM_INTOLERANCES = "intolerances";

    private final RecipeService recipesService;

    public RecipeController(RecipeService recipesService) {
        this.recipesService = recipesService;
    }

    @Operation(summary = "Search for recipes based on provided parameters")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recipes successfully queried and returned",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Recipe.class))
            }
        )
    })
    @GetMapping(value = "search")
    public Mono<ResponseEntity<List<Recipe>>> search(
        @Parameter(description = "The (natural language) recipe search query.")
        @RequestParam String name,
        @Parameter(description = "A comma-separated list of intolerances. All recipes returned must not contain ingredients that are not suitable for people with the intolerances entered.")
        @RequestParam (required = false) String intolerances,
        @Parameter(description = "A comma-separated list of ingredients that should/must be used in the recipes.")
        @RequestParam (required = false) String includeIngredients,
        @Parameter(description = "If set to true, you get more information about the recipes returned.")
        @RequestParam (defaultValue = "false") boolean addRecipeInformation,
        @Parameter(description = "If set to true, you get nutritional information about each recipes returned.")
        @RequestParam (defaultValue = "false") boolean addRecipeNutrition,
        @Parameter(description = "The minimum amount of servings the recipe is for.")
        @RequestParam (required = false) Integer minServings
    ) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_QUERY, name);
        if (intolerances != null && !intolerances.isBlank())
            params.put(PARAM_INTOLERANCES, intolerances);
        if (includeIngredients != null && !includeIngredients.isBlank())
            params.put(PARAM_INCLUDE_INGREDIENTS, includeIngredients);
        params.put(PARAM_ADD_RECIPE_INFORMATION, String.valueOf(addRecipeInformation));
        params.put(PARAM_ADD_RECIPE_NUTRITION, String.valueOf(addRecipeNutrition));
        if (minServings != null && minServings > 0)
            params.put(PARAM_MIN_SERVINGS, String.valueOf(minServings.intValue()));

        return recipesService.search(params).map(ResponseEntity::ok);
    }

    @Operation(summary = "Search for, calculate, and provide information about the specified recipe's calories. Search for recipe by name.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Calories successfully calculated and returned.",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CaloriesResponse.class))
            }
        )
    })
    @GetMapping(value = "search/calories")
    public Mono<ResponseEntity<CaloriesResponse>> recipeCalories(@RequestParam String name) {
        return recipesService.getRecipeCalories(name, null).map(ResponseEntity::ok);
    }

    @Operation(summary = "Retrieve recipe with the specified id, calculate, and provide information about the calories.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Calories successfully calculated and returned.",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CaloriesResponse.class))
            }
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Recipe with the provided id was not found.",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CaloriesResponse.class))
            }
        )
    })
    @GetMapping(value = "search/{id}/calories")
    public Mono<ResponseEntity<CaloriesResponse>> recipeCustomizedCalories(
        @Parameter(description = "The id value of the targeted recipe.")
        @PathVariable long id,
        @Parameter(description = "List of the ingredients that need to be excluded from calories calculation.")
        @RequestParam(required = false) List<String> ingredients
    ) {
        return recipesService.getRecipeCalories(id, ingredients).map(ResponseEntity::ok);
    }
}
