-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : sam. 06 déc. 2025 à 19:27
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
(8, 'Mr', 'Bean', 'Tala Merkha', '0793714172'),
(4, 'Salmi', 'Amina', 'Cartier Sghir', '0786545445'),
(5, 'Tabet', 'Bilal', 'Tala Hamza', '0786545444'),
(6, 'Maouche', 'Lina', 'Cité Aouchiche', '0784453246'),
(7, 'Saidi', 'Fateh', 'Edimco', '0567345340'),
(9, 'mansouri', 'larbi', 'dar nacer', '0553802815'),
(10, 'Saidani', 'Souhila', 'Sidi Ahmed', '0799696865');

-- --------------------------------------------------------

--
-- Structure de la table `inclure`
--

DROP TABLE IF EXISTS `inclure`;
CREATE TABLE IF NOT EXISTS `inclure` (
  `idMed` int NOT NULL,
  `numLiv` int NOT NULL,
  `quantite` int DEFAULT NULL,
  PRIMARY KEY (`idMed`,`numLiv`),
  KEY `numLiv` (`numLiv`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

--
-- Déchargement des données de la table `inclure`
--

INSERT INTO `inclure` (`idMed`, `numLiv`, `quantite`) VALUES
(3, 19, 3),
(3, 8, 6),
(5, 10, 1),
(8, 12, 1),
(11, 20, 4),
(12, 21, 4),
(11, 22, 3);

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

--
-- Déchargement des données de la table `livraison`
--

INSERT INTO `livraison` (`numLiv`, `codeClt`, `dateLiv`, `priorite`, `statut`, `type_liv`, `taxe`, `cout`) VALUES
(22, 10, '2026-02-05', 'normal', 'en_cours', 'normale', 300, 600),
(19, 7, '2025-12-27', 'normal', 'en_cours', 'normale', 250, 2500),
(21, 5, '2025-12-31', 'urgent', 'en_cours', 'dangereuse', 300, 1100),
(8, 4, '2025-12-07', 'normal', 'annulée', 'sous_chaine_du_froid', 450, 650),
(10, 5, '2026-01-04', 'urgent', 'en_attente', 'dangereuse', 200, 1750),
(12, 6, '2026-01-12', 'urgent', 'en_cours', 'normale', 450, 3330),
(20, 8, '2025-12-30', 'normal', 'en_attente', 'sous_congélation', 200, 600);

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

--
-- Déchargement des données de la table `medicament`
--

INSERT INTO `medicament` (`idMed`, `nomMed`, `datePer`, `nbrBoite`, `prixMed`) VALUES
(1, 'Paracétamol 500g', '2028-07-06', 15, 150),
(3, 'ibuprofène', '2023-02-06', 24, 750),
(5, 'Colchimax 5mg', '2029-09-04', 9, 850),
(11, 'Doliprane 1000g', '2029-06-05', 14, 100),
(8, 'Clamoxyl 500mg', '2026-07-22', 0, 1150),
(9, 'Levothyrox 150', '2028-06-17', 22, 950),
(12, 'Vitamine C', '2026-12-26', 1, 250);

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `idUser` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(20) DEFAULT NULL,
  `prenom` varchar(20) DEFAULT NULL,
  `role` enum('personnel','admin') DEFAULT 'personnel',
  `mdp` varchar(255) DEFAULT NULL,
  `mail` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idUser`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`idUser`, `nom`, `prenom`, `role`, `mdp`, `mail`) VALUES
(13, 'Sahi', 'Nour El Imene', 'admin', '$2a$12$68vh51va6PpXwmd629XgAu1PUbdAavKrq.SLV82U9HCiWGU6hob5K', 'nourelimenesahi@gmail.com'),
(10, 'Monkey D', 'Luffy', 'personnel', '$2a$12$bC3ooCVWxr1TcagTJuUI.eQfsj88OiYE2h5D7uwJZYy.EUnT3F9ri', 'onepiece@gmail.com'),
(14, 'test', 'test', 'personnel', '$2a$12$pLP9F5qKiTvkfmRjG4xDGei9.U43DfXUDxHZTu5fstOrB3H3ZxoDy', 'test@gmail.com'),
(9, 'Uzomaki', 'Naruto', 'personnel', '$2a$12$zNC12zylzqNUBvoswTV24eznlJhk/42AVUkaRg6Swy5vz8wgYVMpK', 'naruto@gmail.com'),
(12, 'Maouche', 'Lina', 'admin', '$2a$12$rRZxT3PNscJMdILA.MJR7uSJPZHLsaIKe9oBAgC4ii7jdTHg9l2my', 'linamaouche@gmail.com'),
(15, 'test', 'test', 'personnel', '$2a$12$FEtfIxbaMcPSAdHUf9vjOOrjD5.3HVqGnzTCrG.W7UydYI00t1g0y', 'testtest@gmail.com');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
