package org.jluc.ctr.tools.calendrier.server.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.dto.EvenementDTO;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.EvenementRepository;
import org.jluc.ctr.tools.calendrier.server.service.EvenementService;
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

@Path("/evenements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvenementResource {
    @Inject
    EvenementService service;

    @Inject
    EvenementRepository evenementRepository;

    @Inject
    WebSocketResource wsResource;

    @GET
    @Path("/{id}")
    public Response getEventById(@PathParam("id") String id) {
        Log.info("UUID demandé : " + id);
        wsResource.broadcast(new ProgressMessage(true, "loadevents", "Chargement de l'évènement...", 0));
        UUID uuid = UUID.fromString(id);
        Evenement event = Evenement.findById(uuid);
        if (event != null) {
            wsResource.broadcast(new ProgressMessage(true,
                    "loadevents", "Chargement de l'évènement terminé...", 100));
            return Response.ok(EvenementDTO.fromEntity(event)).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("Erreur de loadevents", "Chargement de l'évènement terminé..."));
            return Response.noContent().build();
        }
    }

    @GET
    public Response getAll() {
        wsResource.broadcast(
                new ProgressMessage(true, "loadevents", "Chargement des nouveaux évènements...", 0));
        int nbNewEvents = service.updateEvenementsFromGoogleForms(wsResource);
        wsResource.broadcast(new ProgressMessage(true, "loadevents",
                "Ajout de " + nbNewEvents + " nouveaux évènements...", 100));
        List<Evenement> events = evenementRepository.findAllWithAllLoaded();
        wsResource.broadcast(new ProgressMessage(true, "loadevents", "Chargement des évènements...", 0));
        List<EvenementDTO> eventsDTO = new ArrayList<EvenementDTO>();
        int nb = 0;
        Date Today = new Date();
        for (Evenement evenement : events) {
            if (evenement.getDatedebut().after(Today)) {
                eventsDTO.add(EvenementDTO.fromEntity(evenement));
            }
            wsResource.broadcast(
                    new ProgressMessage(true, "loadevents", "Chargement des évènements...",
                            (nb / events.size()) * 100));
            nb++;
        }
        wsResource.broadcast(
                new ProgressMessage(true, "loadevents", "Chargement des évènements terminé...", 100));
        return Response.ok(eventsDTO).build();
    }

    @POST
    public Response addEvent(EvenementDTO input) {
        System.out.println("Reçu un événement : " + input);

        // Ici tu peux transformer en entité Evenement, valider, etc.
        // Exemple simple : retourner ce qu’on a reçu
        wsResource.broadcast(new InfoMessage("Evenement ajouté " + input.typeEvenement.activite));
        return Response.status(Response.Status.CREATED).entity(input).build();
    }

    @PUT
    public Response modifyEvent() {
        return Response.serverError().status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEventById(@PathParam("id") String id) {
        UUID uuid = UUID.fromString(id);
        if (Evenement.deleteById(uuid))
            return Response.ok().build();
        else
            return Response.noContent().build();
    }
}
