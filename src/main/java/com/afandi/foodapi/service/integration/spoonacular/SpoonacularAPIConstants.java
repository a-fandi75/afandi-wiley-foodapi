package com.afandi.foodapi.service.integration.spoonacular;

public class SpoonacularAPIConstants {

    public static final String PARAM_API_KEY = "apiKey",
        PARAM_ID = "id",
        PARAM_QUERY = "query",
        PARAM_TITLE_MATCH = "titleMatch",
        PARAM_ADD_RECIPE_INFO = "addRecipeInformation",
        PARAM_ADD_RECIPE_NUTRITION = "addRecipeNutrition",
        PARAM_NUMBER = "number";

    public static final String END_POINT_SEARCH = "/recipes/complexSearch",
        END_POINT_NUTRITION_BY_ID = "/recipes/%d/nutritionWidget.json";
}
