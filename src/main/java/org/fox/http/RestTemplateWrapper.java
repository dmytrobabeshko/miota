package org.fox.http;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.net.URI;

import org.fox.domain.SearchInfoHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Component
public class RestTemplateWrapper {

    @NotNull
    private final RestTemplate restTemplate;

    @NotNull
    private final UriBuilder uriBuilder;


    @Autowired
    public RestTemplateWrapper(@NotNull RestTemplate restTemplate, @NotNull UriBuilder uriBuilder) {
        this.restTemplate = restTemplate;
        this.uriBuilder = uriBuilder;
    }


    @NotNull
    public HttpStatus tryGet(@NotNull SearchInfoHolder searchInfoHolder, @NotNull Integer currentBuild, @NotNull Integer otaBuild) throws Throwable {

        HttpStatus httpStatus = null;
        Throwable error = null;

        int connectionErrorRetry = 0;
        int httpNotFoundRetry = 0;

        Integer connectionErrorMaxRetry = searchInfoHolder.getConnectionErrorRetry();
        Integer httpNotFounMaxRetry = searchInfoHolder.getHttpNotFoundRetry();
        Integer connectionErrorRetryDelay = searchInfoHolder.getConnectionErrorRetryDelay();
        Integer httpNotFoundRetryDelay = searchInfoHolder.getHttpNotFoundRetryDelay();

        outer:
        while (connectionErrorRetry < connectionErrorMaxRetry) {
            try {
                while (httpNotFoundRetry < httpNotFounMaxRetry) {
                    httpStatus = tryGetImpl(searchInfoHolder, currentBuild, otaBuild);

                    if (httpStatus != NOT_FOUND) {
                        break outer;
                    }

                    httpNotFoundRetry++;

                    if (httpNotFoundRetryDelay != 0 && httpNotFounMaxRetry < httpNotFoundRetry) {
                        Thread.sleep(httpNotFoundRetryDelay);
                    }
                }
                break;

            } catch (Throwable e) {
                error = e;
            }

            connectionErrorRetry++;
            if (connectionErrorRetryDelay != 0 && connectionErrorRetry < connectionErrorMaxRetry) {
                Thread.sleep(connectionErrorRetryDelay);
            }
        }

        if (error != null) {
            throw error;
        }

        return httpStatus;
    }

    @NotNull
    private HttpStatus tryGetImpl(@NotNull SearchInfoHolder searchInfoHolder, @NotNull Integer currentBuild, @NotNull Integer otaBuild) {
        HttpStatus httpStatus;
        URI updateUri = uriBuilder.buildURI(searchInfoHolder, currentBuild, otaBuild);

        try {
            ResponseEntity<String> exchange = restTemplate.exchange(updateUri, HttpMethod.HEAD, null, String.class);
            httpStatus = exchange.getStatusCode();
        } catch (HttpClientErrorException e) {
            httpStatus = e.getStatusCode();
        }

        return httpStatus;
    }
}
