package com.learn.reactivespringboot.controller;

import com.learn.reactivespringboot.domain.Chapter;
import com.learn.reactivespringboot.service.ChapterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChapterController {

    final
    ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {

        this.chapterService = chapterService;
    }

    @GetMapping("/chapters")
    public Flux<Chapter> listing() {

        return chapterService.getAllChapters();
    }


}
