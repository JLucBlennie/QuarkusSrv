package org.jluc.ctr.tools.calendrier.server.moniteurs;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.RestQuery;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/ctr/moniteurs")
public class MoniteurRessource {

    @Inject
    MoniteurService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/moniteuruuid")
    public String moniteuruuid(@RestQuery String uuid) {
        return service.getMoniteurByUUID(uuid);
    }
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/moniteur")
    public String moniteur(@RestQuery String name) {
        return service.getMoniteurByName(name);
    }

    @GET
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public String add(@RestQuery String name) {
        Moniteur moniteur = new Moniteur(name);
        moniteur.persist();
        return "Adding Mono ==> " + name;
    }

    @GET
    @Path("names")
    @Produces(MediaType.TEXT_PLAIN)
    public String names() {
        List<Moniteur> moniteurs = Moniteur.listAll();
        String names = moniteurs.stream().map(g -> g.getLastname())
                .collect(Collectors.joining(", "));
        return "Les moniteurs " + names;
    }
}
