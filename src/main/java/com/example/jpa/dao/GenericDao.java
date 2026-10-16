package com.example.jpa.dao;

import javax.persistence.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericDao<T> {
    private static final Logger LOG = Logger.getLogger(GenericDao.class.getName());
    private final Class<T> entityClass;
    protected final EntityManager em;

    public GenericDao(Class<T> entityClass, EntityManager em) {
        this.entityClass = entityClass; this.em = em;
    }
    // Each write owns a resource-local transaction; failures are rolled back.
    private <R> R transaction(Supplier<R> operation) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = operation.get();
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                try { tx.rollback(); } catch (RuntimeException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }
            LOG.log(Level.WARNING, "Persistence operation failed", e);
            throw e;
        }
    }
    public void create(T entity) {
        transaction(() -> { em.persist(entity); return null; });
    }
    public T find(Object id) { return em.find(entityClass, id); }
    public T update(T entity) { return transaction(() -> em.merge(entity)); }
    public void delete(T entity) {
        transaction(() -> {
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            return null;
        });
    }
    public List<T> findAll() {
        String name = em.getMetamodel().entity(entityClass).getName();
        return em.createQuery("SELECT e FROM " + name + " e", entityClass).getResultList();
    }
}
