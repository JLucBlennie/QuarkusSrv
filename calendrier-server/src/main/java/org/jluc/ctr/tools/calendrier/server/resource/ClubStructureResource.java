package org.jluc.ctr.tools.calendrier.server.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.dto.ClubStructureDTO;
import org.jluc.ctr.tools.calendrier.server.model.club.ClubStructure;
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

@Path("/clubstructures")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClubStructureResource {
    @Inject
    WebSocketResource wsResource;

    @GET
    @Path("/{id}")
    public Response getClubStructureById(@PathParam("id") String id) {
        Log.info("UUID demandé : " + id);
        wsResource.broadcast(new ProgressMessage(true, "loadclub", "Chargement du club...", 0));
        UUID uuid = UUID.fromString(id);
        ClubStructure clubStructure = ClubStructure.findById(uuid);
        if (clubStructure != null) {
            wsResource.broadcast(new ProgressMessage(true,
                    "loadclub", "Chargement du club terminé...", 100));
            return Response.ok(ClubStructureDTO.fromEntity(clubStructure)).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur de chargement du club..."));
            return Response.noContent().build();
        }
    }

    @GET
    public Response getAll() {
        List<ClubStructure> clubStructures = ClubStructure.listAll();
        wsResource.broadcast(new ProgressMessage(true, "loadclubs", "Chargement des clubs...", 0));
        List<ClubStructureDTO> clubStructuresDTO = new ArrayList<ClubStructureDTO>();
        int nb = 0;
        for (ClubStructure clubStructure : clubStructures) {
                clubStructuresDTO.add(ClubStructureDTO.fromEntity(clubStructure));
            wsResource.broadcast(
                    new ProgressMessage(true, "loadclubs", "Chargement des clubs...",
                            (nb / clubStructures.size()) * 100));
            nb++;
        }
        wsResource.broadcast(
                new ProgressMessage(true, "loadclubs", "Chargement des clubs terminé...", 100));
        return Response.ok(clubStructuresDTO).build();
    }

    @POST
    public Response addClubStructure(ClubStructureDTO input) {
        Log.info("Ajout d'une structure ou club : " + input);

        ClubStructure newClubStructure = ClubStructure.findById(input.uuid);
        if (newClubStructure != null) {
            Log.warn("La structure ou club existe déjà en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("La structure ou club existe déjà en base.").build();
        }
        newClubStructure = input.toEntity();
        newClubStructure.persist();
        wsResource.broadcast(new InfoMessage("Structure ou club ajoutée " + input.name));
        return Response.status(Response.Status.CREATED).entity(newClubStructure).build();
    }

    @PUT
    public Response modifyClubStructure(ClubStructureDTO input) {
        Log.info("Modification de la structure ou club " + input);
        ClubStructure newClubStructure = ClubStructure.findById(input.uuid);
        if (newClubStructure == null) {
            Log.warn("La structure ou club n'existe pas en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("La structure ou club n'existe pas en base.").build();
        }
        newClubStructure = input.toEntity();
        newClubStructure.persist();
        wsResource.broadcast(new InfoMessage("Structure ou club modifiée " + input.name));
        return Response.status(Response.Status.CREATED).entity(newClubStructure).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteClubStructureById(@PathParam("id") String id) {
        UUID uuid = UUID.fromString(id);
        if (ClubStructure.deleteById(uuid))
            return Response.ok().build();
        else
            return Response.noContent().build();
    }
}
