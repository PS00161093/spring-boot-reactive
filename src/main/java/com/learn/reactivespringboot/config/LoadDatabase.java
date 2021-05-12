package com.learn.reactivespringboot.config;

import com.learn.reactivespringboot.dao.ChapterRepository;
import com.learn.reactivespringboot.domain.Chapter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner inti(ChapterRepository chapterRepository) {

        return args -> {
            Flux.just(new Chapter("Java"), new Chapter("Jenkins"), new Chapter("Cloud"))
                    .flatMap(chapterRepository::save)
                    .subscribe(System.out::println);
        };
    }
}
