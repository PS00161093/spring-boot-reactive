package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesInfoService {

    private MovieInfoRepository movieInfoRepository;

    public MoviesInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getAllMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }


    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo movieInfo) {
        return movieInfoRepository.findById(id)
                .flatMap(existingMovieInfo -> {
                    existingMovieInfo.setCast(movieInfo.getCast());
                    existingMovieInfo.setName(movieInfo.getName());
                    existingMovieInfo.setYear(movieInfo.getYear());
                    existingMovieInfo.setReleaseDate(movieInfo.getReleaseDate());
                    return movieInfoRepository.save(existingMovieInfo);
                });
    }
}
