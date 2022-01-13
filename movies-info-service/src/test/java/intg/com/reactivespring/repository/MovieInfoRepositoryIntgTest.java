package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                    assertEquals(2012, movieInfo.getYear());
                })
                .verifyComplete();
    }

    @Test
    void testSaveMovieInfo() {
        var newMovie = new MovieInfo(null, "3 Idiots",
                2014, List.of("Aamir Khan", "Maddy"), LocalDate.parse("2014-06-15"));

        var movieMono =
                movieInfoRepository
                        .save(newMovie)
                        .log();

        StepVerifier
                .create(movieMono)
                .assertNext(savedMovie -> {
                    assertNotNull(savedMovie);
                    assertEquals("3 Idiots", savedMovie.getName());
                    assertEquals(2014, savedMovie.getYear());
                })
                .verifyComplete();
    }

    @Test
    void testUpdateMovieInfo() {
        var existingMovie =
                movieInfoRepository
                        .findById("abc")
                        .block();

        assert existingMovie != null;
        existingMovie.setName("Dark Knight Rises - 1");

        var updatedMovieMono = movieInfoRepository
                .save(existingMovie)
                .log();

        StepVerifier
                .create(updatedMovieMono)
                .assertNext(updatedMovie -> {
                    assertNotNull(updatedMovie);
                    assertEquals("Dark Knight Rises - 1", updatedMovie.getName());
                })
                .verifyComplete();
    }

}