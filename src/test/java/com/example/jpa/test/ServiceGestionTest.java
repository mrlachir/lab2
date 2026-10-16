package com.example.jpa.test;

import com.example.jpa.dao.SalleDao;
import com.example.jpa.entities.*;
import com.example.jpa.service.ServiceGestion;
import org.junit.*;
import javax.persistence.*;
import java.util.*;
import static org.junit.Assert.*;

public class ServiceGestionTest {
    private EntityManagerFactory emf;
    private ServiceGestion service;

    @Before public void setUp() {
        Map<String, Object> config = new HashMap<>();
        // update + DB_CLOSE_DELAY=-1 retain data: isolate each test explicitly.
        config.put("javax.persistence.jdbc.url",
                "jdbc:h2:mem:lab2_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        config.put("hibernate.show_sql", "false");
        emf = Persistence.createEntityManagerFactory("gestionPU", config);
        service = new ServiceGestion(emf);
    }
    @After public void tearDown() {
        try {
            if (service != null) service.fermer();
        } finally {
            if (emf != null && emf.isOpen()) emf.close();
        }
    }
    @Test public void testCreationEtRechercheSalle() {
        Salle salle = new Salle("Salle Test", 20);
        service.ajouterSalle(salle);
        assertNotNull(salle.getId());
        assertEquals(1, service.listerSalles().size());
        Salle loaded = service.trouverSalle(salle.getId());
        assertEquals("Salle Test", loaded.getNom());
        assertEquals(20, loaded.getCapacite());
    }
    @Test public void testCreationUtilisateurAvecSalle() {
        Salle salle = new Salle("Salle B", 15);
        service.ajouterSalle(salle);
        Utilisateur utilisateur = new Utilisateur("Alice", "alice@example.com", salle);
        service.ajouterUtilisateur(utilisateur);
        Utilisateur loaded = service.trouverUtilisateur(utilisateur.getId());
        assertEquals("Alice", loaded.getNom());
        assertEquals("alice@example.com", loaded.getEmail());
        assertEquals(salle.getId(), loaded.getSalle().getId());
        assertEquals("Salle B", loaded.getSalle().getNom());
        assertEquals(1, service.listerUtilisateurs().size());
    }
    @Test public void testRechercheUtilisateursParSalle() {
        Salle c = new Salle("Salle C", 30);
        Salle d = new Salle("Salle D", 10);
        service.ajouterSalle(c);
        service.ajouterSalle(d);
        service.ajouterUtilisateur(new Utilisateur("Bob", "bob@example.com", c));
        service.ajouterUtilisateur(new Utilisateur("Carol", "carol@example.com", c));
        service.ajouterUtilisateur(new Utilisateur("Dan", "dan@example.com", d));
        assertEquals(2, service.utilisateursParSalle("Salle C").size());
        assertTrue(service.utilisateursParSalle("Inconnue").isEmpty());
    }
    @Test public void testRequetesAvanceesEtBornes() {
        service.ajouterSalle(new Salle("Petite", 10));
        service.ajouterSalle(new Salle("Moyenne", 20));
        service.ajouterSalle(new Salle("Grande", 30));
        assertEquals(2, service.sallesParCapaciteMin(20).size());
        assertEquals(1, service.sallesParCapaciteSuperieureA(20).size());
        service.ajouterUtilisateur(new Utilisateur("Jean Dupont", "jean@example.com", null));
        service.ajouterUtilisateur(new Utilisateur("Alice", "alice@example.com", null));
        assertEquals(1, service.utilisateursParNom("Jean").size());
        assertEquals("Jean Dupont", service.utilisateursParNom("Jean").get(0).getNom());
    }
    @Test public void testMiseAJourEtSuppressionEntitesDetachees() {
        Salle salle = new Salle("Originale", 10);
        service.ajouterSalle(salle);
        salle.setNom("Modifiee");
        salle.setCapacite(40);
        service.modifierSalle(salle);
        assertEquals(40, service.trouverSalle(salle.getId()).getCapacite());
        assertEquals("Modifiee", service.trouverSalle(salle.getId()).getNom());
        Utilisateur u = new Utilisateur("Alice", "alice@example.com", salle);
        service.ajouterUtilisateur(u);
        u.setNom("Alicia");
        u.setEmail("alicia@example.com");
        service.modifierUtilisateur(u);
        assertEquals("Alicia", service.trouverUtilisateur(u.getId()).getNom());
        assertEquals("alicia@example.com", service.trouverUtilisateur(u.getId()).getEmail());
        service.supprimerUtilisateur(u);
        assertNull(service.trouverUtilisateur(u.getId()));
        assertNotNull(service.trouverSalle(salle.getId()));
        service.supprimerSalle(salle);
        assertNull(service.trouverSalle(salle.getId()));
    }
    @Test public void testRollbackSurNomSalleDuplique() {
        service.ajouterSalle(new Salle("Unique", 10));
        EntityManager em = emf.createEntityManager();
        try {
            try {
                new SalleDao(em).create(new Salle("Unique", 20));
                fail("A duplicate room name must fail");
            } catch (PersistenceException expected) {
                assertFalse(em.getTransaction().isActive());
            }
        } finally { em.close(); }
        assertEquals(1, service.listerSalles().size());
        assertEquals(10, service.listerSalles().get(0).getCapacite());
        service.ajouterSalle(new Salle("Apres erreur", 30));
        assertEquals(2, service.listerSalles().size());
    }
    @Test public void testEmailUniqueEtSalleOptionnelle() {
        service.ajouterUtilisateur(new Utilisateur("Alice", "same@example.com", null));
        try {
            service.ajouterUtilisateur(new Utilisateur("Bob", "same@example.com", null));
            fail("A duplicate email must fail");
        } catch (PersistenceException expected) {
            assertEquals(1, service.listerUtilisateurs().size());
            assertNull(service.listerUtilisateurs().get(0).getSalle());
        }
    }
}
