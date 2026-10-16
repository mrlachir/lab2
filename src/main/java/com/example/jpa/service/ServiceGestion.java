package com.example.jpa.service;

import com.example.jpa.dao.*;
import com.example.jpa.entities.*;
import javax.persistence.*;
import java.util.List;
import java.util.function.Function;

public class ServiceGestion implements AutoCloseable {
    private final EntityManagerFactory emf;
    private final boolean ownsFactory;

    public ServiceGestion() {
        this(Persistence.createEntityManagerFactory("gestionPU"), true);
    }
    // Inject a shared factory (useful for tests); its owner closes it.
    public ServiceGestion(EntityManagerFactory emf) { this(emf, false); }
    private ServiceGestion(EntityManagerFactory emf, boolean ownsFactory) {
        this.emf = emf; this.ownsFactory = ownsFactory;
    }
    // One short-lived EntityManager per operation, as recommended in section 14.
    private <R> R execute(Function<EntityManager, R> operation) {
        EntityManager em = emf.createEntityManager();
        try { return operation.apply(em); } finally { em.close(); }
    }
    public void ajouterSalle(Salle salle) {
        execute(em -> { new SalleDao(em).create(salle); return null; });
    }
    public void ajouterUtilisateur(Utilisateur utilisateur) {
        execute(em -> { new UtilisateurDao(em).create(utilisateur); return null; });
    }
    public Salle trouverSalle(Long id) { return execute(em -> new SalleDao(em).find(id)); }
    public Utilisateur trouverUtilisateur(Long id) { return execute(em -> new UtilisateurDao(em).find(id)); }
    public Salle modifierSalle(Salle salle) { return execute(em -> new SalleDao(em).update(salle)); }
    public Utilisateur modifierUtilisateur(Utilisateur utilisateur) {
        return execute(em -> new UtilisateurDao(em).update(utilisateur));
    }
    public void supprimerSalle(Salle salle) {
        execute(em -> { new SalleDao(em).delete(salle); return null; });
    }
    public void supprimerUtilisateur(Utilisateur utilisateur) {
        execute(em -> { new UtilisateurDao(em).delete(utilisateur); return null; });
    }
    public List<Salle> listerSalles() { return execute(em -> new SalleDao(em).findAll()); }
    public List<Utilisateur> listerUtilisateurs() { return execute(em -> new UtilisateurDao(em).findAll()); }
    public List<Utilisateur> utilisateursParSalle(String nomSalle) {
        return execute(em -> new UtilisateurDao(em).findBySalleNom(nomSalle));
    }
    public List<Salle> sallesParCapaciteMin(int min) {
        return execute(em -> new SalleDao(em).findByCapaciteMin(min));
    }
    public List<Salle> sallesParCapaciteSuperieureA(int min) {
        return execute(em -> new SalleDao(em).findByCapaciteSuperieureA(min));
    }
    public List<Utilisateur> utilisateursParNom(String fragment) {
        return execute(em -> new UtilisateurDao(em).findByNomContenant(fragment));
    }
    public void fermer() { close(); }
    @Override public void close() {
        if (ownsFactory && emf.isOpen()) { emf.close(); }
    }
}
