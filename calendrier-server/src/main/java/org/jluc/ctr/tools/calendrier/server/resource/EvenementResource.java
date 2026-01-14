package org.jluc.ctr.tools.calendrier.server.resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.dto.EvenementDTO;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.EvenementRepository;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;
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
import jakarta.ws.rs.QueryParam;
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
        Log.debug("UUID demandé : " + id);
        wsResource.broadcast(new ProgressMessage(true, "loadevents", "Chargement de l'évènement...", 0));
        UUID uuid = UUID.fromString(id);
        Evenement event = evenementRepository.findOneWithAllLoaded(uuid);
        if (event != null) {
            Log.debug("Évènement trouvé et nb de sessions : " + event.getSessions().size());
            wsResource.broadcast(new ProgressMessage(true,
                    "loadevents", "Chargement de l'évènement terminé...", 100));
            return Response.ok(EvenementDTO.fromEntity(event)).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur de chargement de l'évènement..."));
            return Response.noContent().build();
        }
    }

    @GET
    @Path("/validate")
    public Response validateEvent(@QueryParam("id") String id) {
        Log.debug("Validation de l'évènement UUID : " + id);
        wsResource.broadcast(new ProgressMessage(true, "validateevent", "Validation de l'évènement...", 0));
        UUID uuid = UUID.fromString(id);
        Evenement event = Evenement.findById(uuid);
        if (event != null) {
            try {
                service.validateEvenement(event, wsResource);
                Log.debug("Évènement validé : " + event.getUUID());
                wsResource.broadcast(new ProgressMessage(true,
                        "validateevent", "Évènement validé avec succès.", 100));
                wsResource.broadcast(new InfoMessage("Évènement validé : " + event.getUUID()));
            } catch (IOException | URISyntaxException e) {
                Log.error("Erreur lors de la validation de l'évènement : " + uuid, e);
                wsResource.broadcast(
                        new InfoMessage("[Erreur]", "Erreur de validation de l'évènement..."));
                return Response.noContent().build();
            }
            return Response.ok(EvenementDTO.fromEntity(event)).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur de validation de l'évènement..."));
            return Response.noContent().build();
        }
    }

    @GET
    @Path("/refuse")
    public Response refuseEvent(@QueryParam("id") String id) {
        Log.debug("Refus de l'évènement UUID : " + id);
        wsResource.broadcast(new ProgressMessage(true, "refuseevent", "Refus de l'évènement...", 0));
        UUID uuid = UUID.fromString(id);
        Evenement event = Evenement.findById(uuid);
        if (event != null) {
            try {
                service.refuseEvenement(event, wsResource);
                Log.debug("Évènement refusé : " + event.getUUID());
                wsResource.broadcast(new ProgressMessage(true,
                        "refuseevent", "Évènement refusé avec succès.", 100));
                wsResource.broadcast(new InfoMessage("Évènement refusé : " + event.getUUID()));
            } catch (IOException | URISyntaxException e) {
                Log.error("Erreur lors du refus de l'évènement : " + uuid, e);
                wsResource.broadcast(
                        new InfoMessage("[Erreur]", "Erreur du refus de l'évènement..."));
                return Response.noContent().build();
            }
            return Response.ok(EvenementDTO.fromEntity(event)).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur du refus de l'évènement..."));
            return Response.noContent().build();
        }
    }

    @GET
    @Path("/updatebdd")
    public Response updateBDD() {
        wsResource.broadcast(
                new ProgressMessage(true, "loadevents", "Chargement des nouveaux évènements...", 0));
        int nbNewEvents = service.updateEvenementsFromGoogleForms(wsResource);
        wsResource.broadcast(new ProgressMessage(true, "loadevents",
                "Ajout de " + nbNewEvents + " nouveaux évènements...", 100));
        return Response.ok().build();
    }

    @GET
    public Response getAll() {
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
        Log.debug("Ajout d'un événement : " + input);

        // Ici tu peux transformer en entité Evenement, valider, etc.
        // Exemple simple : retourner ce qu’on a reçu
        Evenement newEvent = Evenement.findById(input.uuid);
        if (newEvent != null) {
            Log.warn("L'évènement existe déjà en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("L'évènement existe déjà en base.").build();
        }
        newEvent = input.toEntity();
        newEvent.persist();
        wsResource.broadcast(new InfoMessage("Evenement ajouté " + input.typeEvenement.activite));
        return Response.status(Response.Status.CREATED).entity(newEvent).build();
    }

    @PUT
    public Response modifyEvent(EvenementDTO input) {
        Log.debug("Modification de l'évènement" + input);
        Evenement newEvent = Evenement.findById(input.uuid);
        if (newEvent == null) {
            Log.warn("L'évènement n'existe pas en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("L'évènement n'existe pas en base.").build();
        }
        newEvent = input.toEntity();
        newEvent.persist();
        wsResource.broadcast(new InfoMessage("Evenement modifié " + input.typeEvenement.activite));
        return Response.status(Response.Status.CREATED).entity(newEvent).build();
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

    @GET
    @Path("/moniteur/{moniteurid}")
    public Response getEventsByMoniteurById(@PathParam("moniteurid") String moniteurid) {
        Log.debug("UUID demandé : " + moniteurid);
        wsResource.broadcast(new ProgressMessage(true, "loadmoniteur", "Chargement du moniteur...", 0));
        UUID uuid = UUID.fromString(moniteurid);
        Moniteur moniteur = Moniteur.findById(uuid);
        if (moniteur != null) {
            wsResource.broadcast(new ProgressMessage(true,
                    "loadmoniteur", "Chargement du moniteur terminé...", 100));
            List<EvenementDTO> eventsDTO = new ArrayList<EvenementDTO>();
            int nb = 0;
            Date Today = new Date();
            List<Evenement> events = service.getExamensFor(moniteur);
            for (Evenement evenement : events) {
                if (service.getAnnee(evenement.getDatedebut()) == service.getAnnee(Today) ||
                        service.getAnnee(evenement.getDatedebut()) == service.getAnnee(Today) - 1) {
                    eventsDTO.add(EvenementDTO.fromEntity(evenement));
                    wsResource.broadcast(
                            new ProgressMessage(true, "loadevents", "Chargement des évènements...",
                                    (nb / events.size()) * 100));
                    nb++;
                }
            }
            wsResource.broadcast(
                    new ProgressMessage(true, "loadevents", "Chargement des évènements terminé...", 100));
            return Response.ok(eventsDTO).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur de chargement du moniteur..."));
            return Response.noContent().build();
        }
    }

}
