package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient()
class MoviesInfoControllerIntgTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    MovieInfoRepository movieInfoRepository;

    private final static String MOVIES_INFO_CONTEXT_PATH = "/v1/movieinfos";

    @BeforeEach
    void setUp() {
        var moviesInfo =
                List.of(
                        new MovieInfo(null, "Batman Begins",
                                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                        new MovieInfo(null, "The Dark Knight",
                                2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                        new MovieInfo("abc", "Dark Knight Rises",
                                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository
                .saveAll(moviesInfo)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository
                .deleteAll()
                .block();
    }

    @Test
    void testAddMovieInfo() {
        var movieInfo = new MovieInfo(null, "DON", 2010, List.of("SRK", "PC"), LocalDate.parse("2010-06-15"));
        webTestClient
                .post()
                .uri(MOVIES_INFO_CONTEXT_PATH)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfo1 -> {
                    var mvInfo = movieInfo1.getResponseBody();
                    assertNotNull(mvInfo);
                    assertNotNull(mvInfo.getMovieInfoId());
                    assertEquals("DON", mvInfo.getName());
                    assertEquals(2010, mvInfo.getYear());
                });
    }
}