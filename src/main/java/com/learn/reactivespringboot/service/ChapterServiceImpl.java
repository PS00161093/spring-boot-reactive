package com.learn.reactivespringboot.service;

import com.learn.reactivespringboot.dao.ChapterRepository;
import com.learn.reactivespringboot.domain.Chapter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;

    public ChapterServiceImpl(ChapterRepository chapterRepository) {

        this.chapterRepository = chapterRepository;
    }

    @Override
    public Flux<Chapter> getAllChapters() {

        return Flux.just(new Chapter("Java"), new Chapter("Jenkins"), new Chapter("Cloud"));
    }
}
