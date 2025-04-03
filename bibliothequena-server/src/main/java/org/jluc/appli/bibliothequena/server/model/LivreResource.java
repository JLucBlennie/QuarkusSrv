package org.jluc.appli.bibliothequena.server.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.reactive.RestPath;
import org.jluc.appli.bibliothequena.server.model.json.LivreJSON;

import io.quarkus.logging.Log;
import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/bibna")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LivreResource {
    private LivreService livreService = new LivreService();

    @POST
    public Response addLivre(LivreJSON livrejson) {
        Livre livre = livreService.getLivreByISBN(livrejson.getISBN());
        if (livre != null) {
            livre = livreService.updateFromJSON(livrejson);
            try {
                livre.persist();
                return Response.ok(livreService.toLivreJSON(livre)).status(Response.Status.CREATED).build();
            } catch (EntityExistsException e) {
                Log.error(
                        "Probleme dans la sauvegarde en BDD de Entite --> " + livrejson.toString() + " ==> "
                                + e.getMessage());
                return Response.serverError().status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            livre = new Livre(livrejson);
            livre.persist();
            return Response.ok(livreService.toLivreJSON(livre)).status(Response.Status.CREATED).build();
        }
    }

    @GET
    public Response getLivres() {
        List<Livre> livres = Livre.listAll();
        List<LivreJSON> livresjson = new ArrayList<LivreJSON>();
        for (Livre livre : livres) {
            livresjson.add(livreService.toLivreJSON(livre));
        }
        return Response.ok(livresjson).status(Response.Status.FOUND).build();
    }

    @PUT
    public Response updateLivre(LivreJSON livre) {
        return addLivre(livre);
    }

    @DELETE
    public Response deleteLivre(LivreJSON livrejson) {
        List<Livre> livres = Livre.listAll();
        Livre foundLivre = null;
        for (Livre livre : livres) {
            if (livrejson.getISBN().equalsIgnoreCase(livre.getISBN())) {
                foundLivre = livre;
            }
        }

        if (foundLivre != null) {
            if (Livre.deleteById(foundLivre.id))
                return Response.ok(livreService.toLivreJSON(foundLivre)).status(Response.Status.FOUND).build();
        }
        return Response.ok(livrejson).status(Response.Status.NOT_FOUND).build();
    }

    @HEAD
    public Response getHeadInfo() {
        return Response.serverError().status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @OPTIONS
    @Path("/livre/{isbn}")
    public Response getInfoByISBN(@RestPath String isbn) {
        List<Livre> livres = Livre.listAll();
        for (Livre livre : livres) {
            if (livre.getISBN().equalsIgnoreCase(isbn)) {
                return Response.ok(livreService.toLivreJSON(livre)).status(Response.Status.FOUND).build();
            }
        }

        return Response.ok(isbn).status(Response.Status.NOT_FOUND).build();
    }

    @PATCH
    public Response updateOrAddLivre(LivreJSON livre) {
        return addLivre(livre);
    }
}
