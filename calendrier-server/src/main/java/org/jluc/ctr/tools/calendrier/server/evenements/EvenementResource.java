package org.jluc.ctr.tools.calendrier.server.evenements;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.RestQuery;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/ctr/evenements")
public class EvenementResource {
    @Inject
    EvenementService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/eventuuid")
    public String eventuuid(@RestQuery String uuid) {
        return service.getEvenementByUUID(uuid);
    }
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/event")
    public String event(@RestQuery String name) {
        return service.getEvenementByName(name);
    }

    @GET
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public String add(@RestQuery String name) {
        Evenement event = new Evenement(name);
        event.persist();
        return "Adding Event ==> " + name;
    }

    @GET
    @Path("names")
    @Produces(MediaType.TEXT_PLAIN)
    public String names() {
        List<Evenement> evenements = Evenement.listAll();
        String names = evenements.stream().map(g -> g.getName())
                .collect(Collectors.joining(", "));
        return "I've said hello to " + names;
    }
}
