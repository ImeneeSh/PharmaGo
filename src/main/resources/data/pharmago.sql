-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : jeu. 13 nov. 2025 à 12:03
-- Version du serveur : 9.1.0
-- Version de PHP : 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `pharmago`
--

-- --------------------------------------------------------

--
-- Structure de la table `client`
--

DROP TABLE IF EXISTS `client`;
CREATE TABLE IF NOT EXISTS `client` (
  `codeClt` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(20) DEFAULT NULL,
  `prenom` varchar(20) DEFAULT NULL,
  `adresse` varchar(30) DEFAULT NULL,
  `numTel` char(10) DEFAULT NULL,
  PRIMARY KEY (`codeClt`)
) ;

--
-- Déchargement des données de la table `client`
--

INSERT INTO `client` (`codeClt`, `nom`, `prenom`, `adresse`, `numTel`) VALUES
(1, 'sahi', 'imene', 'sidi ahmed', '0793714172');

-- --------------------------------------------------------

--
-- Structure de la table `inclure`
--

DROP TABLE IF EXISTS `inclure`;
CREATE TABLE IF NOT EXISTS `inclure` (
  `idMed` int NOT NULL,
  `numLiv` int NOT NULL,
  PRIMARY KEY (`idMed`,`numLiv`),
  KEY `numLiv` (`numLiv`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Structure de la table `livraison`
--

DROP TABLE IF EXISTS `livraison`;
CREATE TABLE IF NOT EXISTS `livraison` (
  `numLiv` int NOT NULL AUTO_INCREMENT,
  `codeClt` int DEFAULT NULL,
  `dateLiv` date DEFAULT NULL,
  `priorite` enum('urgent','normal') DEFAULT NULL,
  `statut` enum('en_attente','livrée','annulée','en_cours') DEFAULT NULL,
  `type_liv` enum('sous_chaine_du_froid','sous_congélation','dangereuse','normale') DEFAULT NULL,
  `taxe` float DEFAULT NULL,
  `cout` float DEFAULT NULL,
  PRIMARY KEY (`numLiv`),
  KEY `codeClt` (`codeClt`)
) ;

-- --------------------------------------------------------

--
-- Structure de la table `medicament`
--

DROP TABLE IF EXISTS `medicament`;
CREATE TABLE IF NOT EXISTS `medicament` (
  `idMed` int NOT NULL AUTO_INCREMENT,
  `nomMed` varchar(20) DEFAULT NULL,
  `datePer` date DEFAULT NULL,
  `nbrBoite` int DEFAULT NULL,
  `prixMed` int DEFAULT NULL,
  PRIMARY KEY (`idMed`)
) ;

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `idUser` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(20) DEFAULT NULL,
  `prenom` varchar(20) DEFAULT NULL,
  `role` enum('personnel','admin') DEFAULT NULL,
  `mdp` varchar(255) DEFAULT NULL,
  `mail` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`idUser`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
