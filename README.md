# Supermarket Management System (SMS)

Ce projet est une application de gestion de supermarché développée en Java (Swing) et utilisant une base de données MySQL. Il implémente une architecture à trois tiers (Présentation, Logique Métier, Accès aux Données) pour garantir la modularité et la maintenabilité.

## Fonctionnalités Principales

Le système supporte les rôles utilisateurs suivants : **Gérant, Caissier, et Responsable de Stocks**.

* **Gestion des Ventes :** Enregistrement des produits, calcul du total, gestion de la monnaie à rendre, impression de ticket.
* **Gestion du Stock :** Ajout de nouveaux produits, mise à jour des quantités, définition des seuils minimaux.
* **Gestion des Employés :** Ajout, modification, et promotion des employés.

## Architecture

Le projet est structuré selon l'architecture **3-Tiers** :

1.  **View :** Interface utilisateur (Java Swing).
2.  **Service :** Logique métier (règles de gestion).
3.  **DAO :** Persistance des données (JDBC vers MySQL).

## Configuration de l'Environnement

### 1. Base de Données (MySQL)

Assurez-vous d'avoir un serveur MySQL fonctionnel. Le schéma de base de données est défini par l'**ERD** (voir documentation annexe).

### 2. Dépendances

Le projet nécessite le connecteur JDBC pour MySQL. Le fichier `.jar` doit être placé dans le dossier `/lib`.

* `lib/mysql-connector-java.jar`

## Démarrage du Projet

1.  Cloner ce répertoire.
2.  Configurer les informations de connexion MySQL dans `com.supermarket.db.DBConnectionManager.java`.
3.  Exécuter la classe `com.supermarket.main.Main.java`.