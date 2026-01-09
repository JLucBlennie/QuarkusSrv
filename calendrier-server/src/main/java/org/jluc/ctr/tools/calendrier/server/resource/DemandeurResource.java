package org.jluc.ctr.tools.calendrier.server.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.dto.DemandeurDTO;
import org.jluc.ctr.tools.calendrier.server.model.club.Demandeur;
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

@Path("/demandeurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DemandeurResource {
    @Inject
    WebSocketResource wsResource;

    @GET
    @Path("/{id}")
    public Response getCDemandeurById(@PathParam("id") String id) {
        Log.info("UUID demandé : " + id);
        wsResource.broadcast(new ProgressMessage(true, "loaddemandeur", "Chargement du demandeur...", 0));
        UUID uuid = UUID.fromString(id);
        Demandeur demandeur = Demandeur.findById(uuid);
        if (demandeur != null) {
            wsResource.broadcast(new ProgressMessage(true,
                    "loaddemandeur", "Chargement du demandeur terminé...", 100));
            return Response.ok(DemandeurDTO.fromEntity(demandeur)).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur de chargement du demandeur..."));
            return Response.noContent().build();
        }
    }

    @GET
    public Response getAll() {
        List<Demandeur> demandeurs = Demandeur.listAll();
        wsResource.broadcast(new ProgressMessage(true, "loaddemandeurs", "Chargement des demandeurs...", 0));
        List<DemandeurDTO> demandeursDTO = new ArrayList<DemandeurDTO>();
        int nb = 0;
        for (Demandeur demandeur : demandeurs) {
                demandeursDTO.add(DemandeurDTO.fromEntity(demandeur));
            wsResource.broadcast(
                    new ProgressMessage(true, "loaddemandeurs", "Chargement des demandeurs...",
                            (nb / demandeurs.size()) * 100));
            nb++;
        }
        wsResource.broadcast(
                new ProgressMessage(true, "loaddemandeurs", "Chargement des demandeurs terminé...", 100));
        return Response.ok(demandeursDTO).build();
    }

    @POST
    public Response addDemandeur(DemandeurDTO input) {
        Log.info("Ajout d'un demandeur : " + input);

        Demandeur newDemandeur = Demandeur.findById(input.uuid);
        if (newDemandeur != null) {
            Log.warn("Le demandeur existe déjà en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("Le demandeur existe déjà en base.").build();
        }
        newDemandeur = input.toEntity();
        newDemandeur.persist();
        wsResource.broadcast(new InfoMessage("Demandeur ajouté " + input.name));
        return Response.status(Response.Status.CREATED).entity(newDemandeur).build();
    }

    @PUT
    public Response modifyDemandeur(DemandeurDTO input) {
        Log.info("Modification du demandeur " + input);
        Demandeur newDemandeur = Demandeur.findById(input.uuid);
        if (newDemandeur == null) {
            Log.warn("Le demandeur n'existe pas en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("Le demandeur n'existe pas en base.").build();
        }
        newDemandeur = input.toEntity();
        newDemandeur.persist();
        wsResource.broadcast(new InfoMessage("Demandeur modifié " + input.name));
        return Response.status(Response.Status.CREATED).entity(newDemandeur).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteDemandeurById(@PathParam("id") String id) {
        UUID uuid = UUID.fromString(id);
        if (Demandeur.deleteById(uuid))
            return Response.ok().build();
        else
            return Response.noContent().build();
    }

}
