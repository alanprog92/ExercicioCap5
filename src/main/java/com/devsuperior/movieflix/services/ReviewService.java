package com.devsuperior.movieflix.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.ReviewDTO;
import com.devsuperior.movieflix.dto.UserDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.entities.User;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.repositories.ReviewRepository;
import com.devsuperior.movieflix.repositories.UserRepository;
import com.devsuperior.movieflix.services.exceptions.DatabaseException;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository repository;
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Transactional(readOnly = true)
	public Page<ReviewDTO> findAllPaged(Pageable pageable) {
		Page<Review> list = repository.findAll(pageable);
		return list.map(x -> new ReviewDTO(x));
	}

	@Transactional(readOnly = true)
	public ReviewDTO findById(Long id) {
		Optional<Review> obj = repository.findById(id);
		Review entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new ReviewDTO(entity);
	}

	@PreAuthorize("hasAnyRole('MEMBER')")
	@Transactional
	public ReviewDTO insert(ReviewDTO dto) {
		Review entity = new Review();

		Optional<Movie> movie = movieRepository.findById(dto.getMovieId());
		UserDTO userDTO = userService.profileCurrentUser();
		
		Optional<User> user = userRepository.findById(userDTO.getId());
		
		entity.setText(dto.getText());
		entity.setMovie(movie.get());
		entity.setUser(user.get());

		entity = repository.save(entity);
		return new ReviewDTO(entity);
	}
	
	@PreAuthorize("hasAnyRole('MEMBER')")
	@Transactional
	public ReviewDTO update(Long id, ReviewDTO dto) {
		try {
			Review entity = repository.getOne(id);
			
			Optional<Movie> movie = movieRepository.findById(dto.getMovieId());
			Optional<User> user = userRepository.findById(dto.getUser().getId());
			
			entity.setText(dto.getText());
			entity.setMovie(movie.get());
			entity.setUser(user.get());
			
			return new ReviewDTO(entity);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}		
	}

	@PreAuthorize("hasAnyRole('MEMBER')")
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
}
