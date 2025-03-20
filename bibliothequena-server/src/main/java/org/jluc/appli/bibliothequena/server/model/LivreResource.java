package org.jluc.appli.bibliothequena.server.model;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.RestQuery;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/bibna/livres")
public class LivreResource {
    @Inject
    LivreService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/livreisbn")
    public String livreisbn(@RestQuery String isbn) {
        return service.getLivreByISBN(isbn);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/livre")
    public String livre(@RestQuery String name) {
        return service.getLivreByName(name);
    }

    @GET
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public String add(@RestQuery String name) {
        Livre livre = new Livre("", name, "", "", 0, Status.LU, "");
        livre.persist();
        return "Adding Livre ==> " + name;
    }

    @GET
    @Path("names")
    @Produces(MediaType.TEXT_PLAIN)
    public String names() {
        List<Livre> livres = Livre.listAll();
        String names = livres.stream().map(g -> g.getName())
                .collect(Collectors.joining(", "));
        return "Les Livres " + names;
    }
}
