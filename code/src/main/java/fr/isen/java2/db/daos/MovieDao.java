package fr.isen.java2.db.daos;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {
	
	/**
     * Récupère la liste de tous les films avec leur genre associé.
     */
	public List<Movie> listMovies() {
		
		
		
		List<Movie> ListOfMovie = new ArrayList<>();
		// Connexion à la base de données
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			// Création de la requête SQL simple
			try (Statement statement = connection.createStatement()) {
				// Exécution de la requête avec une jointure pour avoir les infos du genre
				try (ResultSet results = statement.executeQuery("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre")) {
					while (results.next()) {
						// Création de l'objet Genre à partir des colonnes de la table 'genre'
						Genre genre = new Genre(results.getInt("idgenre"), results.getString("name"));
						// Création de l'objet Movie en convertissant la date en LocalDate
                        Movie movie = new Movie(results.getInt("idmovie"), results.getString("title"),
                                results.getDate("release_date").toLocalDate(), genre, results.getInt("duration"),
                                results.getString("director"), results.getString("summary"));
                        
                        ListOfMovie.add(movie);

					}
				}
			}
			return ListOfMovie;
		}
		catch(SQLException e) {
			// Gestion de l'erreur si la DB est inaccessible	
			throw new RuntimeException("Problem", e);
		}
		
	}

	/**
     * Récupère les films filtrés par le nom d'un genre.
     */
	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> ListOfMovie = new ArrayList<>();
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			// Utilisation d'un prepare Statement pour utiliser un paramètre dans la requète 
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?")){
					// On remplace le '?' par notre paramètre 
					statement.setString(1, genreName);
					
					try (ResultSet results = statement.executeQuery()){
						while(results.next()) {
							Genre genre = new Genre(results.getInt("idgenre"), results.getString("name"));
							Movie movie = new Movie(results.getInt("idmovie"), results.getString("title"),
                                results.getDate("release_date").toLocalDate(), genre, results.getInt("duration"),
                                results.getString("director"), results.getString("summary"));
						ListOfMovie.add(movie);
					}
				}
			}
		return ListOfMovie;
	}   catch (SQLException e ) {
			throw new RuntimeException("ListOfMovie by genre, error", e);
		}
	}

		
	/**
     * Ajoute un nouveau film en base de données et récupère son ID auto-généré.
     */
	public Movie addMovie(Movie movie) {
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()){
			String sqlQuery = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary) " + "VALUES(?,?,?,?,?,?)";
			// RETURN_GENERATED_KEYS permet de récupérer l'ID créé par la base 
			try ( PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)){
				// Remplissage des paramètres de la requête
				statement.setString(1, movie.getTitle());
				statement.setDate(2, Date.valueOf(movie.getReleaseDate()));
				statement.setInt(3, movie.getGenre().getId());
				statement.setInt(4, movie.getDuration());
				statement.setString(5, movie.getDirector());
				statement.setString(6, movie.getSummary());
				// Exécution de l'insertion
				statement.executeUpdate();
				
				// Récupération de l'ID généré pour mettre à jour l'objet Java
				try (ResultSet ids = statement.getGeneratedKeys()) {
		            if (ids.next()) {
		                movie.setId(ids.getInt(1));
		                return movie;
		            }
		        }
				
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error adding movie", e);
		}
		return null;
	}
}
