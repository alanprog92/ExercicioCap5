package com.devsuperior.movieflix.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import com.devsuperior.movieflix.entities.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {

	@Query(value = "SELECT * FROM tb_movie WHERE genre_id LIKE :genreId", nativeQuery = true)
	Page<Movie> findByGenre(String genreId, Pageable pageable);

}
