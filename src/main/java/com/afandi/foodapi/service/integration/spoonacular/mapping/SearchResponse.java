package com.afandi.foodapi.service.integration.spoonacular.mapping;

import com.afandi.foodapi.dto.recipe.Recipe;

import java.util.List;

/**
 * <p>Java class for Spoonacular complexSearch API
 */
public class SearchResponse {

    private List<Recipe> results;

    public List<Recipe> getResults() {
        return results;
    }
}
