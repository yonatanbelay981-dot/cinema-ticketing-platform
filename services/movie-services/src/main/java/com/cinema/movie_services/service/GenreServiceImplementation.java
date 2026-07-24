package com.cinema.movie_services.service;

import com.cinema.movie_services.dto.response.GenreResponse;
import com.cinema.movie_services.entity.Genre;
import com.cinema.movie_services.exception.ResourceNotFoundException;
import com.cinema.movie_services.repository.GenreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class GenreServiceImplementation implements GenreService{
    public GenreServiceImplementation(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    private final GenreRepository genreRepository;

    @Override
    public Page<GenreResponse> findAllGenres(Pageable pageable) {
        log.info("Fetching all genres");
        Page<Genre> genres = genreRepository.findAll(pageable);
        log.info("Found {} genres", genres.getTotalElements());
        return genres.map(this::mapToGenreResponse);

    }

    @Override
    public GenreResponse createGenre(String name) {
        log.info("Creating genre with name {}", name);
        Genre genre = new Genre();
        genre.setName(name);
        Genre savedGenre = genreRepository.save(genre);
        log.info("Genre successfully created with id {}", savedGenre.getId());
        return mapToGenreResponse(savedGenre);
    }

    @Override
    public GenreResponse getGenreById(UUID id) {
        log.info("Fetching genre with id {}", id);
        Genre genre =  genreRepository.findById(id).orElseThrow(()->{
            log.warn("Genre with id {} not found", id);
            return new ResourceNotFoundException("Genre not found with id " + id);
        });
        return mapToGenreResponse(genre);
    }

    @Override
    public GenreResponse updateGenre(UUID id, String name) {
        log.info("Updating genre with id {}", id);
        Genre genre = genreRepository.findById(id).orElseThrow(()->{
            log.warn("While updating Genre with id {} not found", id);
            return new ResourceNotFoundException("Genre not found with id " + id);
        });
        genre.setName(name);
        Genre updatedGenre = genreRepository.save(genre);
        log.info("Genre successfully updated with id {}", updatedGenre.getId());
        return mapToGenreResponse(updatedGenre);
    }

    @Override
    public void deleteGenre(UUID id) {
        log.info("Deleting genre with id {}", id);
        Genre genre = genreRepository.findById(id).orElseThrow(()->{
            log.warn("While deleting Genre with id {} not found", id);
            return new ResourceNotFoundException("Genre not found with id " + id);
        });
        genreRepository.delete(genre);
        log.info("Genre successfully deleted with id {}", id);

    }
    private GenreResponse mapToGenreResponse(Genre genre) {
        GenreResponse genreResponse = new GenreResponse();
        genreResponse.setId(genre.getId());
        genreResponse.setName(genre.getName());
        return genreResponse;
    }
}
