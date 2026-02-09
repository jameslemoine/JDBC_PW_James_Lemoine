package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDaoTestCase {
	
	private final MovieDao movieDao = new MovieDao();
	
	@BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
	/**
	 * Teste si la méthode listMovies() renvoie bien tous les films.
	 */
	 @Test
	 public void shouldListMovies() {
		 List<Movie> movies = movieDao.listMovies();
		 
		// Vérification de la taille de la liste
		 assertThat(movies).hasSize(3);
		// Vérification précise du contenu de chaque objet Movie
		 assertThat(movies).extracting("id", "title", "releaseDate", "genre.id", "duration", "director", "summary").containsOnly(
				 tuple(1, "Title 1", LocalDate.of(2015, 11, 26), 1, 120, "director 1", "summary of the first movie"), 
				 tuple(2, "My Title 2", LocalDate.of(2015, 11, 14), 2, 114, "director 2", "summary of the second movie"), 
				 tuple(3, "Third title", LocalDate.of(2015, 12, 12), 2, 176, "director 3", "summary of the third movie"));

	 }
	
	 /**
	  * Teste le filtrage des films par nom de genre.
	  */
	 @Test
	 public void shouldListMoviesByGenre() {
		// Exécution pour le genre "Drama"
		 List<Movie> movies = movieDao.listMoviesByGenre("Drama");
		 
		// On attend un seul film pour ce genre
		 assertThat(movies).hasSize(1);
		 assertThat(movies).extracting("id", "title", "releaseDate", "genre.name", "duration", "director", "summary").containsOnly(
				 tuple(1, "Title 1", LocalDate.of(2015, 11, 26), "Drama", 120, "director 1", "summary of the first movie")); 		 
	 }
	
	 /**
	  * Teste l'insertion d'un nouveau film et la récupération de son ID généré.
	  */
	 @Test
	 public void shouldAddMovie() throws Exception {
		// Préparation de l'objet à ajouter
		 Genre drama = new Genre(1, "Drama");
		 Movie addMovie = new Movie(null, "Fourth title", LocalDate.of(2026, 2, 6), drama, 160, "director 4", "summary of the fourth movie");
		// Exécution de l'ajout
		 Movie createdMovie = movieDao.addMovie(addMovie);
		 
		// Vérification que l'ID a bien été auto-incrémenté à 4, bon titre et à le genre Drama
		 assertThat(createdMovie.getId()).isEqualTo(4);
		 assertThat(createdMovie.getTitle()).isEqualTo("Fourth title");
		 assertThat(createdMovie.getGenre().getName()).isEqualTo("Drama");
		    
		// Vérification finale : la base de données doit maintenant contenir 4 films
		 List<Movie> allMovies = movieDao.listMovies();
		 assertThat(allMovies).hasSize(4);
		 assertThat(allMovies).extracting("title").contains("Fourth title");
	 }
}
