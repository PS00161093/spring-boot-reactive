package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;


@Component
@Slf4j
public class ReviewHandler {

    @Autowired
    private Validator validator;

    private ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(review -> reviewReactiveRepository.save(review))
                .flatMap(savedReview -> ServerResponse.status(HttpStatus.CREATED).bodyValue(savedReview))
                .log();
    }

    private void validate(Review review) {
        var violations = validator.validate(review);
        if (!violations.isEmpty()) {
            log.info("Violations : {}", violations);
            var errorMsg = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new ReviewDataException(errorMsg);
        }
    }

    public Mono<ServerResponse> getAllReviews(ServerRequest serverRequest) {
        var movieInfoId = serverRequest.queryParam("movieInfoId");
        var reviewsFlux = movieInfoId.isPresent() ?
                reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get())) :
                reviewReactiveRepository.findAll();

        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {
        var id = serverRequest.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(id)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for reviewId : " + id)));
        return existingReview
                .flatMap(review -> serverRequest.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            review.setMovieInfoId(reqReview.getMovieInfoId());
                            return review;
                        })
                        .flatMap(updatedReview -> reviewReactiveRepository.save(updatedReview))
                        .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        var id = serverRequest.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(id);
        return existingReview
                .flatMap(review -> reviewReactiveRepository.deleteById(id))
                .then(ServerResponse.noContent().build());
    }
}
