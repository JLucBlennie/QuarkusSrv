package org.jluc.ctr.tools.calendrier.server.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.dto.TypeEvenementDTO;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeEvenement;
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

@Path("/typeevenements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TypeEvenementResource {
    @Inject
    WebSocketResource wsResource;

    @GET
    @Path("/{id}")
    public Response getEventTypeById(@PathParam("id") String id) {
        Log.info("UUID demandé : " + id);
        wsResource.broadcast(new ProgressMessage(true, "loadeventtypes", "Chargement du type d'évènement...", 0));
        UUID uuid = UUID.fromString(id);
        TypeEvenement eventType = TypeEvenement.findById(uuid);
        if (eventType != null) {
            wsResource.broadcast(new ProgressMessage(true,
                    "loadeventtypes", "Chargement du type d'évènement terminé...", 100));
            return Response.ok(TypeEvenementDTO.fromEntity(eventType)).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur de chargement du type d'évènement..."));
            return Response.noContent().build();
        }
    }

    @GET
    public Response getAll() {
        List<TypeEvenement> eventTypes = TypeEvenement.listAll();
        wsResource.broadcast(new ProgressMessage(true, "loadeventtypes", "Chargement des nouveaux types d'évènements...", 0));
        List<TypeEvenementDTO> eventTypesDTO = new ArrayList<TypeEvenementDTO>();
        int nb = 0;
        for (TypeEvenement eventType : eventTypes) {
                eventTypesDTO.add(TypeEvenementDTO.fromEntity(eventType));
            wsResource.broadcast(
                    new ProgressMessage(true, "loadeventtypes", "Chargement des types d'évènements...",
                            (nb / eventTypes.size()) * 100));
            nb++;
        }
        wsResource.broadcast(
                new ProgressMessage(true, "loadeventtypes", "Chargement des types d'évènements terminé...", 100));
        return Response.ok(eventTypesDTO).build();
    }

    @POST
    public Response addEventType(TypeEvenementDTO input) {
        Log.info("Ajout d'un type d'événement : " + input);

        TypeEvenement newEventType = TypeEvenement.findById(input.uuid);
        if (newEventType != null) {
            Log.warn("Le type d'évènement existe déjà en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("Le type d'évènement existe déjà en base.").build();
        }
        newEventType = input.toEntity();
        newEventType.persist();
        wsResource.broadcast(new InfoMessage("Type d'Evenement ajouté " + input.activite));
        return Response.status(Response.Status.CREATED).entity(newEventType).build();
    }

    @PUT
    public Response modifyEventType(TypeEvenementDTO input) {
        Log.info("Modification du type d'évènement" + input);
        TypeEvenement newEventType = TypeEvenement.findById(input.uuid);
        if (newEventType == null) {
            Log.warn("Le type d'évènement n'existe pas en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("Le type d'évènement n'existe pas en base.").build();
        }
        newEventType = input.toEntity();
        newEventType.persist();
        wsResource.broadcast(new InfoMessage("Type d'Evenement modifié " + input.activite));
        return Response.status(Response.Status.CREATED).entity(newEventType).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEventTypeById(@PathParam("id") String id) {
        UUID uuid = UUID.fromString(id);
        if (TypeEvenement.deleteById(uuid))
            return Response.ok().build();
        else
            return Response.noContent().build();
    }
}
