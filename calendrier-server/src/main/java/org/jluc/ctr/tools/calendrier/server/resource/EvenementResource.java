package org.jluc.ctr.tools.calendrier.server.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jluc.ctr.tools.calendrier.server.dto.EvenementDTO;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.service.EvenementService;

@Path("/evenements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvenementResource {
    @Inject
    EvenementService service;

    @GET
    @Path("/{id}")
    public Response getEventById(@RestQuery String id) {
        Log.info(id);
        UUID uuid = UUID.fromString(id);
        Evenement event = Evenement.findById(uuid);
        if (event != null)
            return Response.ok(EvenementDTO.fromEntity(event)).build();
        else
            return Response.noContent().build();
    }

    @GET
    public Response getAll() {
        List<Evenement> events = Evenement.listAll();
        List<EvenementDTO> eventsDTO = new ArrayList<EvenementDTO>();
        for (Evenement evenement : events) {
            eventsDTO.add(EvenementDTO.fromEntity(evenement));
        }
       return Response.ok(eventsDTO).build();
    }

    @POST
    public Response addEvent(EvenementDTO input) {
        System.out.println("Reçu un événement : " + input);

        // Ici tu peux transformer en entité Evenement, valider, etc.
        // Exemple simple : retourner ce qu’on a reçu
        return Response.status(Response.Status.CREATED).entity(input).build();
    }

    @PUT
    public Response modifyEvent() {
        return Response.serverError().status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEventById(@RestQuery String id) {
        UUID uuid = UUID.fromString(id);
        if (Evenement.deleteById(uuid))
            return Response.ok().build();
        else
            return Response.noContent().build();
    }
}
