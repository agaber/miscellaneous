package dev.agaber.movies;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public final class MovieController {
  @Autowired
  private MovieService movieService;

  @GetMapping
  public ResponseEntity<List<Movie>> getAllMovies() {
    return new ResponseEntity<List<Movie>>(movieService.allMovies(), HttpStatus.OK);
  }

  @GetMapping("/{imdbId}")
  public ResponseEntity<Movie> getMovie(@PathVariable String imdbId) {
    var movie = movieService.movie(imdbId);
    return movie
        .map(m -> new ResponseEntity<Movie>(m, HttpStatus.OK))
        .orElse(new ResponseEntity<Movie>(HttpStatus.NOT_FOUND));
  }
}
