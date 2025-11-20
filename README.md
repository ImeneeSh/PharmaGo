# **PharmaGo**

PharmaGo est une application desktop destinée à moderniser et automatiser la gestion interne des livraisons pharmaceutiques dans une pharmacie centrale.
Elle offre une solution complète de suivi, d’organisation et d’analyse des opérations liées aux clients, médicaments, utilisateurs et livraisons.

---

## **Introduction au Projet**

Dans de nombreuses pharmacies, la gestion des livraisons est encore réalisée manuellement, ce qui peut entraîner :

* des erreurs de saisie,
* des retards de traitement,
* une mauvaise visibilité sur les opérations en cours,
* une difficulté à assurer la traçabilité.

**PharmaGo** répond à ces défis en proposant une application :

* intuitive,
* rapide,
* sécurisée,
* adaptée aux besoins du personnel pharmaceutique.

L’application permet notamment :

* la gestion des clients, médicaments et utilisateurs,
* la création et le suivi des livraisons,
* la génération de tickets avec QR code,
* des statistiques visuelles pour mieux piloter l’activité.

---

## **Fonctionnalités Principales**

### **Authentification**

* Connexion par email + mot de passe
* Vérification du rôle : **admin** ou **personnel**
* Messages d’erreur détaillés en cas d’identifiants incorrects
* Interface adaptée selon le rôle

---

### **Tableau de Bord**

* Menu latéral ergonomique
* Indicateurs clés (livraisons, clients, médicaments)
* Navigation rapide vers toutes les sections

---

### **Gestion des Clients**

* Ajouter / modifier / supprimer un client
* Recherche par nom, prénom ou code
* Liste complète disponible
* Validation stricte des données

---

### **Gestion des Médicaments**

* Ajouter / modifier / supprimer un médicament
* Contrôle des quantités et des prix
* Recherche par nom ou code
* Mise à jour en temps réel

---

### **Gestion des Livraisons**

* Création d’une livraison
* Génération automatique d’un ticket avec QR Code
* Ajout de médicaments + calcul automatique du coût
* Modification ou annulation (avec historique conservé)
* Recherche / filtrage par type et statut
* Traçabilité complète

---

### **Gestion des Utilisateurs** *(Admin uniquement)*

* Ajout, modification et suppression d’utilisateurs
* Contrôles avancés sur les données saisies
* Accès sécurisé réservé aux administrateurs

---

## **Technologies Utilisées**

* **JavaFX** — Interface graphique moderne
* **MySQL (WAMP + PhpMyAdmin)** — Base de données
* **Architecture MVC** — Organisation claire du code
* **QRCode Generator** — Génération de tickets QR
* **GitHub** — Versioning et collaboration

---

## **Méthodologie de Développement**



### 1. **Prototype Figma**

* Conception des interfaces
* Définition des parcours utilisateurs
* Validation de l’ergonomie

>  [Lien du prototype](https://www.figma.com/proto/lJ0tlBARWACYMKvOFykVHH/PharmaGO?node-id=1-2&p=f&t=mR2xd0jKd5rrINlO-1&scaling=scale-down&content-scaling=fixed&page-id=0%3A1&starting-point-node-id=1%3A2)
---


### 2. **Développement progressif**

* Approche par **prototypage**
* Implémentation incrémentale
* Ajustements continus selon les besoins

### 3. **Travail collaboratif**

* Dépôt GitHub partagé
* Suivi rigoureux des versions
* Travail en binôme facilité

---

## **Règles Métier et Contraintes**

### **Unicité**

* Tous les identifiants (IdUser, CodeClt, IdMed, NumLiv) sont auto-incrémentés
* Aucun doublon autorisé

### **Utilisateurs**

* Email valide (format standard)
* Mot de passe ≥ 10 caractères
* Nom/prénom alphabétiques
* Téléphone = 10 chiffres

### **Médicaments**

* Quantité ≥ 0
* Prix > 0
* Nom non vide

### **Livraisons**

* Date ≥ date actuelle
* Au moins un médicament requis
* Coût calculé automatiquement
* Historisation en cas d’annulation
* QR code unique

### **Sécurité**

* Mots de passe hachés
* Accès administrateur restreint
* Contrôles stricts des saisies

---

## **Collaborateurs**

Développé en binôme :

* **Nour El Imene Sahi** – Développement & conception
* **[Lina Maouche](https://github.com/linaMCH)** – Collaboratrice principale 

---

## **Licence**

Ce projet est distribué sous la licence **MIT**.
Vous êtes libre de l’utiliser, le modifier et le redistribuer, en citant la source originale.
