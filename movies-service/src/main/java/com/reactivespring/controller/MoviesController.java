package com.reactivespring.controller;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewRestClient;
import com.reactivespring.domain.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private MoviesInfoRestClient moviesInfoRestClient;
    private ReviewRestClient reviewRestClient;

    public MoviesController(MoviesInfoRestClient moviesInfoRestClient, ReviewRestClient reviewRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewRestClient = reviewRestClient;
    }

    @GetMapping("/{movieId}")
    public Mono<Movie> retrieveMovieById(
            @PathVariable String movieId) {

        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    var reviewsMono =
                            reviewRestClient
                                    .retrieveReviews(movieId)
                                    .collectList();

                    return reviewsMono
                            .map(reviews -> new Movie(movieInfo, reviews));
                });
    }

}