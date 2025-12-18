package org.jluc.ctr.tools.calendrier.server.resource;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.RestQuery;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jluc.ctr.tools.calendrier.server.dto.MoniteurDTO;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;
import org.jluc.ctr.tools.calendrier.server.service.MoniteurService;

@Path("/moniteur")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MoniteurRessource {

    @Inject
    MoniteurService service;

    @GET
    public List<MoniteurDTO> list(){
        List<Moniteur> moniteurs = Moniteur.listAll();
        List<MoniteurDTO> moniteursDTO = new ArrayList<MoniteurDTO>();
        for (Moniteur moniteur : moniteurs) {
            moniteursDTO.add(MoniteurDTO.fromEntity(moniteur));
        }
        return moniteursDTO;
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{uuid}")
    public MoniteurDTO moniteuruuid(@RestQuery String uuid) {
        return MoniteurDTO.fromEntity(service.getMoniteurByUUID(uuid));
    }
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/moniteur")
    public MoniteurDTO moniteur(@RestQuery String name) {
        return MoniteurDTO.fromEntity(service.getMoniteurByName(name));
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
        String names = moniteurs.stream().map(g -> g.toString())
                .collect(Collectors.joining(", "));
        return "Les moniteurs " + names;
    }
}
