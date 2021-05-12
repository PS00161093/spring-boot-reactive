package com.learn.reactivespringboot.dao;

import com.learn.reactivespringboot.domain.Chapter;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ChapterRepository extends ReactiveCrudRepository<Chapter, String> {


}
