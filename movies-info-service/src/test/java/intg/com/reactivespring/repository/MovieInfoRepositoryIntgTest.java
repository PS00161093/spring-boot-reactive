package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

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
    void testFindAll() {
        var allMoviesFlux =
                movieInfoRepository
                        .findAll()
                        .log();

        StepVerifier
                .create(allMoviesFlux)
                .expectNextCount(3);
    }

    @Test
    void testFindById() {
        var allMoviesFlux =
                movieInfoRepository
                        .findById("abc")
                        .log();

        StepVerifier
                .create(allMoviesFlux)
                .assertNext(movieInfo -> {
                    assertNotNull(movieInfo);
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                    assertEquals(2012, movieInfo.getYear());
                })
                .expectNextCount(3);
    }

}