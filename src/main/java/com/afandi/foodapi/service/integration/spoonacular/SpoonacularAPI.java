package com.afandi.foodapi.service.integration.spoonacular;

import com.afandi.foodapi.dto.recipe.Nutrition;
import com.afandi.foodapi.dto.recipe.Recipe;
import com.afandi.foodapi.service.integration.spoonacular.mapping.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SpoonacularAPI {

    @Value("${spoonacular.api.base.url}")
    private String baseUrl;

    @Value("${spoonacular.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public SpoonacularAPI(WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Makes a get request to Spoonacular's complex search API
     * @param params list of <code>String</code> filters to filter results by.
     * @return Mono object contains the list of recipes resulted from the search operation.
     */
    public Mono<List<Recipe>> search(Map<String, String> params) {
        URI uri = buildURI(SpoonacularAPIConstants.END_POINT_SEARCH, params);

        return webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(SearchResponse.class)
            .map(response -> response != null ? response.getResults() : Collections.emptyList());
    }

    /**
     * Makes a get request to Spoonacular's nutrition by id API
     * @param id <code>long</code>> the id of the targeted recipe
     * @return Mono object contains the <code>Nutrition</code> object resulted.
     */
    public Mono<Nutrition> nutritionById(long id) {
        URI uri = buildURI(String.format(SpoonacularAPIConstants.END_POINT_NUTRITION_BY_ID, id), null);

        return webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(Nutrition.class);
    }

    /**
     * Constructs and returns the target URI of the specified API.
     * @param url API's url
     * @param queryParams list of parameters to be appended to the final url.
     * @return API's URI.
     */
    private URI buildURI(String url, Map<String, String> queryParams) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .fromHttpUrl(baseUrl + url)
            .queryParam(SpoonacularAPIConstants.PARAM_API_KEY, apiKey);

        if (queryParams != null) {
            for (String param : queryParams.keySet()) {
                uriBuilder.queryParam(param, queryParams.get(param));
            }
        }

        return uriBuilder.build().toUri();
    }
}
