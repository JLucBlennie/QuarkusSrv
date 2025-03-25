package org.jluc.appli.bibliothequena.server.model;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.RestQuery;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType; 
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/bibna")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LivreResource {
    @POST
    @Transactional
    public Response addLivre(Livre livre) {
        System.out.println("Reçu: " + livre.getName() + " - " + livre.getAuthor());
        livre.persist();
        return Response.ok(livre).status(Response.Status.CREATED).build();
    }

    @GET
    public List<Livre> getLivres() {
        List<Livre> livres = Livre.listAll();
        return livres;
    }
}
