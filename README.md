# Lab 2 — Gestion des salles et utilisateurs

Standalone Java 11+ / Maven project implementing all 15 sections of the supplied lab without EJB or JTA.

## Run

From PowerShell:

```powershell
cd C:\Users\PC\Documents\study_5\JEE\labs\lab2
mvn clean test
mvn compile exec:java
```

The demo creates rooms and users, lists and filters them, updates a room, then deletes a user before deleting their room.

## Implementation

- JPA 2.2, Hibernate 5.6.15.Final, H2 2.1.214, JUnit 4.13.2.
- Hibernate Core provides JPA support; it replaces the obsolete hibernate-entitymanager artifact in the handout.
- gestionPU uses RESOURCE_LOCAL, schema update, and jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1.
- Salle and Utilisateur preserve the handout's fields, constraints and optional ManyToOne relationship.
- GenericDao provides create/find/update/delete/findAll and transaction rollback with logging.
- SalleDao provides both >= minimum capacity and > capacity searches; UtilisateurDao provides room-name and LIKE name searches.
- ServiceGestion keeps a reusable factory and closes an EntityManager after each operation (section 14). Returned entities are detached; use modifierSalle/modifierUtilisateur to persist edits.
- The service is intended for this standalone lab; DAO writes own their transactions and are not nested.
- Delete users or clear their room references before deleting a referenced room.
- Each test uses a unique in-memory database so schema update and DB_CLOSE_DELAY do not leak data between tests.
- Memory data disappears when the JVM exits. This lab does not require a web H2 console.

## Files

Entities: src/main/java/com/example/jpa/entities  
DAOs: src/main/java/com/example/jpa/dao  
Service: src/main/java/com/example/jpa/service/ServiceGestion.java  
Demo: src/main/java/com/example/jpa/App.java  
Configuration: src/main/resources/META-INF/persistence.xml  
Tests: src/test/java/com/example/jpa/test/ServiceGestionTest.java

