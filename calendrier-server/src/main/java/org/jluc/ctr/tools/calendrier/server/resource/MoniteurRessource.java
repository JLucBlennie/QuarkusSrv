package org.jluc.ctr.tools.calendrier.server.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.dto.MoniteurDTO;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;
import org.jluc.ctr.tools.calendrier.server.websockets.WebSocketResource;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.InfoMessage;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.ProgressMessage;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/moniteurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MoniteurRessource {

    @Inject
    WebSocketResource wsResource;

    @GET
    public Response getAll() {
        List<Moniteur> moniteurs = Moniteur.listAll();
        wsResource.broadcast(new ProgressMessage(true, "loadmoniteur", "Chargement des moniteurs...", 0));
        List<MoniteurDTO> moniteursDTO = new ArrayList<MoniteurDTO>();
        int nb = 0;
        for (Moniteur moniteur : moniteurs) {
            moniteursDTO.add(MoniteurDTO.fromEntity(moniteur));
            wsResource.broadcast(
                    new ProgressMessage(true, "loadmoniteur", "Chargement des moniteurs...",
                            (nb / moniteurs.size()) * 100));
            nb++;
        }
        wsResource.broadcast(
                new ProgressMessage(true, "loadmoniteur", "Chargement des moniteurs terminé...", 100));
        return Response.ok(moniteursDTO).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getMoniteurById(@PathParam("id") String id) {
        Log.info("UUID demandé : " + id);
        wsResource.broadcast(new ProgressMessage(true, "loadmoniteur", "Chargement du moniteur...", 0));
        UUID uuid = UUID.fromString(id);
        Moniteur moniteur = Moniteur.findById(uuid);
        if (moniteur != null) {
            wsResource.broadcast(new ProgressMessage(true,
                    "loadmoniteur", "Chargement du moniteur terminé...", 100));
            return Response.ok(MoniteurDTO.fromEntity(moniteur)).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur de chargement du moniteur..."));
            return Response.noContent().build();
        }
    }

    @POST
    public Response addMoniteur(MoniteurDTO input) {
        Log.info("Ajout d'un moniteur : " + input);

        Moniteur newMoniteur = Moniteur.findById(input.uuid);
        if (newMoniteur != null) {
            Log.warn("Le moniteur existe déjà en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("Le moniteur existe déjà en base.").build();
        }
        newMoniteur = input.toEntity();
        newMoniteur.persist();
        wsResource.broadcast(new InfoMessage("Moniteur ajouté " + input.lastname + " " + input.firstname));
        return Response.status(Response.Status.CREATED).entity(newMoniteur).build();
    }

    @PUT
    public Response modifyMoniteur(MoniteurDTO input) {
        Log.info("Modification du moniteur " + input);
        Moniteur newMoniteur = Moniteur.findById(input.uuid);
        if (newMoniteur == null) {
            Log.warn("Le moniteur n'existe pas en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("Le moniteur n'existe pas en base.").build();
        }
        newMoniteur = input.toEntity();
        newMoniteur.persist();
        wsResource.broadcast(new InfoMessage("Moniteur modifié " + input.lastname + " " + input.firstname));
        return Response.status(Response.Status.CREATED).entity(newMoniteur).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteMoniteurById(@PathParam("id") String id) {
        UUID uuid = UUID.fromString(id);
        if (Moniteur.deleteById(uuid))
            return Response.ok().build();
        else
            return Response.noContent().build();
    }
}
