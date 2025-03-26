package org.jluc.appli.bibliothequena.server.model;

import java.util.ArrayList;
import java.util.List;

import org.jluc.appli.bibliothequena.server.model.json.LivreJSON;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/bibna")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LivreResource {
    @PersistenceContext
    EntityManager em;

    @POST
    @Transactional
    public Response addLivre(LivreJSON livrejson) {
        System.out.println("Reçu --> " + livrejson.toString());
        Livre livre = Livre.findById(livrejson.getId());
        if (livre != null) {
            livre.updateFromJSON(livrejson);
            if (livre.isPersistent()) {
                System.out.println("Entite persitente --> " + livrejson.toString());
            } else {
                System.out.println("Entite NON persitente --> " + livrejson.toString());
                try {
                    if (Livre.findById(livrejson.getId()) != null) {
                        System.out.println("Entite trouvee --> " + Livre.findById(livrejson.getId()).toString());
                    } else {
                        System.out.println("Entite non trouvee --> On sauvegarde");
                        if (livre.getStatut() == null) {
                            return Response.status(Response.Status.BAD_REQUEST).entity("Statut is required").build();
                        }
                        livrejson = em.merge(livrejson);
                        em.persist(livrejson);
                    }
                    return Response.ok(livrejson).status(Response.Status.CREATED).build();
                } catch (EntityExistsException e) {
                    System.out.println(
                            "Probleme dans la sauvegarde en BDD de Entite --> " + livrejson.toString() + " ==> "
                                    + e.getMessage());
                    return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        } else {
            livre = new Livre(livrejson);
            livre.persist();
            return Response.ok(livrejson).status(Response.Status.CREATED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Probleme d'ajout d'un livre...").build();
    }

    @GET
    public List<LivreJSON> getLivres() {
        List<Livre> livres = Livre.listAll();
        List<LivreJSON> livresjson = new ArrayList<LivreJSON>();
        for (Livre livre : livres) {
            livresjson.add(livre.toLivreJSON());
        }
        return livresjson;
    }
}
