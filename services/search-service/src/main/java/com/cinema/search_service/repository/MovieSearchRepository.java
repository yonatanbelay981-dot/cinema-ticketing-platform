package com.cinema.search_service.repository;

import com.cinema.search_service.document.MovieDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


import java.util.UUID;

public interface MovieSearchRepository extends ElasticsearchRepository<MovieDocument , UUID> {

}
