package com.example.jpa;

import com.example.jpa.entities.*;
import com.example.jpa.service.ServiceGestion;

public class App {
    public static void main(String[] args) {
        try (ServiceGestion service = new ServiceGestion()) {
            Salle a = new Salle("Salle A", 10);
            Salle b = new Salle("Salle B", 30);
            service.ajouterSalle(a);
            service.ajouterSalle(b);
            Utilisateur alice = new Utilisateur("Alice", "alice@example.com", a);
            Utilisateur jean = new Utilisateur("Jean", "jean@example.com", b);
            service.ajouterUtilisateur(alice);
            service.ajouterUtilisateur(jean);
            System.out.println("Toutes les salles : " + service.listerSalles());
            System.out.println("Tous les utilisateurs : " + service.listerUtilisateurs());
            System.out.println("Salle B : " + service.utilisateursParSalle("Salle B"));
            System.out.println("Capacite >= 30 : " + service.sallesParCapaciteMin(30));
            System.out.println("Capacite > 20 : " + service.sallesParCapaciteSuperieureA(20));
            System.out.println("Nom contenant Jean : " + service.utilisateursParNom("Jean"));
            a.setCapacite(15);
            service.modifierSalle(a);
            System.out.println("Salle modifiee : " + service.trouverSalle(a.getId()));
            service.supprimerUtilisateur(alice);
            service.supprimerSalle(a);
            System.out.println("Apres suppression : " + service.listerSalles());
        }
    }
}
