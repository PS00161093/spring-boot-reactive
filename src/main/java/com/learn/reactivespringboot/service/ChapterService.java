package com.learn.reactivespringboot.service;

import com.learn.reactivespringboot.domain.Chapter;
import reactor.core.publisher.Flux;

public interface ChapterService {

    Flux<Chapter> getAllChapters();
}
