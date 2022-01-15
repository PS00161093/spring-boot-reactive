package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    private final static String REVIEWS_CONTEXT_PATH = "/v1/reviews";

    @BeforeEach
    void setUp() {
        var reviewsList = List.of(
                new Review("abc", 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        reviewReactiveRepository
                .saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    @Order(1)
    void testAddReview() {
        var newReview = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient
                .post()
                .uri(REVIEWS_CONTEXT_PATH)
                .bodyValue(newReview)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assertNotNull(savedReview);
                    assertEquals(newReview.getMovieInfoId(), savedReview.getMovieInfoId());
                    assertEquals(newReview.getComment(), savedReview.getComment());
                });
    }

    @Test
    @Order(2)
    void testGetAllReviews() {
        webTestClient
                .get()
                .uri(REVIEWS_CONTEXT_PATH)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    @Order(3)
    void testUpdateReview() {
        var updatedMovie = new Review("abc", 1L, "Awesome Movie", 9.0);
        updatedMovie.setComment("Awesome Movie - INTG update");
        webTestClient
                .put()
                .uri(REVIEWS_CONTEXT_PATH + "/{id}", updatedMovie.getReviewId())
                .bodyValue(updatedMovie)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var review = reviewEntityExchangeResult.getResponseBody();
                    assertNotNull(review);
                    assertEquals(updatedMovie.getComment(), review.getComment());
                });
    }

    @Test
    @Order(4)
    void testDeleteReview() {
        webTestClient
                .delete()
                .uri(REVIEWS_CONTEXT_PATH + "/{id}", "abc")
                .exchange()
                .expectStatus()
                .isNoContent();

        webTestClient
                .get()
                .uri(REVIEWS_CONTEXT_PATH)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(2);
    }
}

