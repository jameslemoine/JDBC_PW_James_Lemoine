package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import fr.isen.java2.db.entities.Genre;

public class GenreDao {
	/**
     * Récupère la liste complète des genres présents dans la base.
     */
	public List<Genre> listGenres() {
		
		List<Genre> ListGenres = new ArrayList<>();
		// Connexion à la base de données
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			// Création de la requête SQL simple
			try (Statement statement = connection.createStatement()){
				// Exécution de la requête 
				try (ResultSet results = statement.executeQuery("SELECT * FROM genre")){
					// Boucle tant qu'il y a des lignes dans la table
					while(results.next()) {
						Genre genre = new Genre(
									results.getInt("idgenre"),
									results.getString("name")
								);
						ListGenres.add(genre);
					}
				}
			}
			return ListGenres;
		} catch (SQLException e) {
			// Gestion de l'erreur si la DB est inaccessible	
			throw new RuntimeException("Genre not found", e);
		}
	}

	/**
     * Recherche un genre spécifique par son nom.
     */
	public Genre getGenre(String name) {
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()){
			// Utilisation d'un prepare Statement pour utiliser un paramètre dans la requète 
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")){
				// On remplace le '?' par notre paramètre 
				statement.setString(1, name);
				try (ResultSet results = statement.executeQuery()){
					// 'if' au lieu de 'while' car on ne cherche qu'un seul résultat unique
					if ( results.next()) {
						return new Genre(
									results.getInt("idgenre"),
									results.getString("name")
									);
					}
				}
			}
			
		}
		catch (SQLException e) {
				throw new RuntimeException("Genre not found", e);
		}
		
		return null; // Retourne null si aucun genre ne correspond au nom
	}

	/**
     * Ajoute un nouveau genre dans la table.
     */
	public void addGenre(String name) {
		
		try( Connection connection = DataSourceFactory.getDataSource().getConnection()){
			String sqlQuery = "INSERT INTO genre(name) VALUES(?)";
			try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)){
				statement.setString(1, name);
				statement.executeUpdate();
				ResultSet ids = statement.getGeneratedKeys();
			}
		}
		catch	(SQLException e) {
			throw new RuntimeException("Not added Genre", e);
		}
		
	}
}
