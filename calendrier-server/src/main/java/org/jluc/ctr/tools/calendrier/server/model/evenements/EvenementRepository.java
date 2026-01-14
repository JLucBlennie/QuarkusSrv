package org.jluc.ctr.tools.calendrier.server.model.evenements;

import java.util.List;
import java.util.UUID;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class EvenementRepository {
    @Inject
    EntityManager entityManager;

    public List<Evenement> findAllWithType() {
        EntityGraph<Evenement> graph = (EntityGraph<Evenement>) entityManager.getEntityGraph("evenement-with-type");
        TypedQuery<Evenement> query = entityManager
                .createQuery("SELECT e FROM Evenement e", Evenement.class)
                .setHint("jakarta.persistence.loadgraph", graph);
        return query.getResultList();
    }
    
    public List<Evenement> findAllWithPresidentDeleguer() {
        EntityGraph<Evenement> graph = (EntityGraph<Evenement>) entityManager
                .getEntityGraph("evenement-with-presdelegue");
        TypedQuery<Evenement> query = entityManager
                .createQuery("SELECT e FROM Evenement e", Evenement.class)
                .setHint("jakarta.persistence.loadgraph", graph);
        return query.getResultList();
    }

    public List<Evenement> findAllWithAllLoaded() {
        EntityGraph<Evenement> graph = (EntityGraph<Evenement>) entityManager.getEntityGraph("evenement-with-all");
        TypedQuery<Evenement> query = entityManager
                .createQuery("SELECT e FROM Evenement e", Evenement.class)
                .setHint("jakarta.persistence.loadgraph", graph);
        return query.getResultList();
    }

    public Evenement findOneWithAllLoaded(UUID evenementId) {
        EntityGraph<Evenement> graph = (EntityGraph<Evenement>) entityManager.getEntityGraph("evenement-with-all");
        TypedQuery<Evenement> query = entityManager
                .createQuery("SELECT e FROM Evenement e WHERE e.uuid = :evenementId",
                        Evenement.class)
                .setParameter("evenementId", evenementId)
                        .setHint("jakarta.persistence.loadgraph", graph);
        Log.debug("Query creee : " + evenementId + " -> " + query.getResultList().size() + " resultats.");
        return query.getResultList().isEmpty() ? null : query.getResultList().get(0);
    }
}