package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@WebFluxTest(MoviesInfoController.class)
@AutoConfigureWebTestClient
class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoServiceMock;

    private final static String MOVIES_INFO_CONTEXT_PATH = "/v1/movieinfos";

    private final MovieInfo movieInfo = new MovieInfo("abc", "Dark Knight Rises",
            2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

    private final MovieInfo movieInfo1 = new MovieInfo(null, "The Dark Knight",
            2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18"));

    private final MovieInfo movieInfoWithNoName = new MovieInfo("abc", "",
            2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

    private final static String MOVIE_ID = "abc";

    @Test
    void addMovieInfo() {
        when(moviesInfoServiceMock.addMovieInfo(any())).thenReturn(Mono.just(movieInfo));
        webTestClient
                .post()
                .uri(MOVIES_INFO_CONTEXT_PATH)
                .bodyValue(movieInfo)
                .exchange()
                .expectBody(MovieInfo.class)
                .isEqualTo(movieInfo);
    }

    @Test
    void getAllMovieInfos() {
        when(moviesInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.just(movieInfo, movieInfo1));
        webTestClient
                .get()
                .uri(MOVIES_INFO_CONTEXT_PATH)
                .exchange()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }

    @Test
    void getMovieInfoById() {
        when(moviesInfoServiceMock.getMovieInfoById(any())).thenReturn(Mono.just(movieInfo));
        webTestClient
                .get()
                .uri(MOVIES_INFO_CONTEXT_PATH + "/{id}", MOVIE_ID)
                .exchange()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var mInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(mInfo);
                    assertEquals(MOVIE_ID, mInfo.getMovieInfoId());
                });
    }

    @Test
    void updateMovieInfo() {
        when(moviesInfoServiceMock.updateMovieInfo(any(), any())).thenReturn(Mono.just(movieInfo1));
        webTestClient
                .put()
                .uri(MOVIES_INFO_CONTEXT_PATH + "/{id}", MOVIE_ID)
                .bodyValue(movieInfo)
                .exchange()
                .expectBody(MovieInfo.class)
                .isEqualTo(movieInfo1);
    }

    @Test
    void deleteMovieInfo() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(moviesInfoServiceMock.deleteMovieInfo(captor.capture())).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(MOVIES_INFO_CONTEXT_PATH + "/{id}", MOVIE_ID)
                .exchange()
                .expectBody(Void.class);

        assertEquals(MOVIE_ID, captor.getValue());
    }

    @Test
    void addMovieInfoWithValidation() {
//        when(moviesInfoServiceMock.addMovieInfo(any())).thenReturn(Mono.just(movieInfoWithNoName));
        webTestClient
                .post()
                .uri(MOVIES_INFO_CONTEXT_PATH)
                .bodyValue(movieInfoWithNoName)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    System.out.println(responseBody);
                    assertNotNull(responseBody);
                });
    }
}