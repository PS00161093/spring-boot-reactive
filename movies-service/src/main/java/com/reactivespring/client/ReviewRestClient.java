package com.reactivespring.client;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReviewRestClient {

    private WebClient webClient;

    public ReviewRestClient(WebClient webClient) { this.webClient = webClient; }

    @Value("${restClient.reviewsUrl}")
    private String moviesInfoUrl;

    public Flux<Review> retrieveReviews(String movieId) {
        String url = constructUrlForGetReviewsById(movieId);
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, reviewsResponse -> handle4xxError(movieId, reviewsResponse))
                .onStatus(HttpStatus::is5xxServerError, reviewsResponse -> handle5xxError(movieId, reviewsResponse))
                .bodyToFlux(Review.class)
                .retry(3)
                .log();
    }

    private String constructUrlForGetReviewsById(String movieId) {
        return UriComponentsBuilder
                .fromHttpUrl(moviesInfoUrl)
                .queryParam("movieInfoId", movieId)
                .buildAndExpand()
                .toUriString();
    }

    private Mono<Throwable> handle4xxError(String movieId, ClientResponse reviewsResponse) {
        if (reviewsResponse.statusCode().equals(HttpStatus.NOT_FOUND)) return Mono.empty();
        return reviewsResponse.bodyToMono(String.class)
                .flatMap(responseMsg -> Mono.error(
                        new ReviewsClientException(responseMsg)
                ));
    }

    private Mono<Throwable> handle5xxError(String movieId, ClientResponse reviewsResponse) {
        return reviewsResponse.bodyToMono(String.class)
                .flatMap(responseMsg -> Mono.error(
                        new ReviewsServerException("Server exception in ReviewService : " + responseMsg)
                ));
    }

}
