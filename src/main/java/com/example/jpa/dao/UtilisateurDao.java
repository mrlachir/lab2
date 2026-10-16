package com.example.jpa.dao;
import com.example.jpa.entities.Utilisateur;
import javax.persistence.EntityManager;
import java.util.List;

public class UtilisateurDao extends GenericDao<Utilisateur> {
    public UtilisateurDao(EntityManager em) { super(Utilisateur.class, em); }
    public List<Utilisateur> findBySalleNom(String nomSalle) {
        return em.createQuery("SELECT u FROM Utilisateur u WHERE u.salle.nom = :nomSalle", Utilisateur.class)
                .setParameter("nomSalle", nomSalle).getResultList();
    }
    // LIKE supports SQL wildcards: the supplied fragment is surrounded by %.
    public List<Utilisateur> findByNomContenant(String fragment) {
        return em.createQuery("SELECT u FROM Utilisateur u WHERE u.nom LIKE :pattern", Utilisateur.class)
                .setParameter("pattern", "%" + fragment + "%").getResultList();
    }
}
