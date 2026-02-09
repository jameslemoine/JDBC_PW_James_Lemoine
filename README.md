# JDBC Movie Management - PW ISEN

Ce projet est une application Java utilisant **JDBC** pour gérer une base de données de films et de genres. Il implémente le pattern **DAO** (Data Access Object) pour séparer la logique d'accès aux données de la logique métier.

##  Fonctionnalités

* **Gestion des Genres** : Lister, récupérer par nom et ajouter des genres.
* **Gestion des Films** : 
    * Récupération de la liste complète des films (avec jointures SQL).
    * Filtrage des films par genre.
    * Ajout de nouveaux films avec récupération automatique de l'ID généré.
* **Tests Unitaires** : Couverture des fonctionnalités principales avec JUnit 5 et AssertJ.

##  Technologies utilisées

* **Java 17+**
* **JDBC** (Java Database Connectivity)
* **SQLite** (Base de données)
* **JUnit 5 & AssertJ** (Tests unitaires)
* **Maven** (Gestionnaire de dépendances)

##  Structure du projet

* `src/main/java` : Contient les entités (`Movie`, `Genre`) et les DAOs.
* `src/test/java` : Contient les classes de test (`MovieDaoTestCase`, etc.).
* `DataSourceFactory.java` : Gère la connexion à la base de données.

##  Installation et Tests

1. Cloner le dépôt :
   ```bash
   git clone [https://github.com/jameslemoine/JDBC_PW_James_Lemoine.git](https://github.com/jameslemoine/JDBC_PW_James_Lemoine.git)
