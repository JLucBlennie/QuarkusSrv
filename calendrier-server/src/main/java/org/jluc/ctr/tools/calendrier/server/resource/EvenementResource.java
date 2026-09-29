package org.jluc.ctr.tools.calendrier.server.resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jluc.ctr.tools.calendrier.server.dto.DemandeurDTO;
import org.jluc.ctr.tools.calendrier.server.dto.EvenementDTO;
import org.jluc.ctr.tools.calendrier.server.dto.MoniteurDTO;
import org.jluc.ctr.tools.calendrier.server.dto.TypeEvenementDTO;
import org.jluc.ctr.tools.calendrier.server.model.club.Demandeur;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.EvenementRepository;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Status;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeEvenement;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;
import org.jluc.ctr.tools.calendrier.server.service.EvenementService;
import org.jluc.ctr.tools.calendrier.server.websockets.WebSocketResource;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.InfoMessage;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.ProgressMessage;

import io.quarkus.logging.Log;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/{id}")
    @RolesAllowed("user")
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

    @PUT
    @Transactional
    @Path("/validate")
    @RolesAllowed("admin")
    public Response validateEvent(@QueryParam("id") String id) {
        Log.debug("Validation de l'évènement UUID : " + id);
        wsResource.broadcast(new ProgressMessage(true, "validateevent", "Validation de l'évènement...", 0));
        UUID uuid = UUID.fromString(id);
        Evenement event = Evenement.findById(uuid);
        if (event != null) {
            try {
                service.validateEvenement(event, wsResource);
                Log.debug("Évènement validé : " + event.getUUID());
                event.persistAndFlush();
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

    @PUT
    @Transactional
    @Path("/refuse")
    @RolesAllowed("admin")
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
                event.persist();
            } catch (IOException e) {
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
    @RolesAllowed("admin")
    public Response updateBDD() {
        wsResource.broadcast(
                new ProgressMessage(true, "loadevents", "Chargement des nouveaux évènements...", 0));
        int nbNewEvents = service.updateEvenementsFromGoogleForms(wsResource).size();
        wsResource.broadcast(new ProgressMessage(true, "loadevents",
                "Ajout de " + nbNewEvents + " nouveaux évènements...", 100));
        return Response.ok().build();
    }

    @GET
    @RolesAllowed("user")
    public Response getAll(@QueryParam("statut") String statutParam, @QueryParam("saison") String saison) {
        EnumSet<Status> statutsFiltre;
        try {
            statutsFiltre = parseStatutFilter(statutParam);
        } catch (IllegalArgumentException e) {
            Log.warn("Statut invalide dans le filtre : " + statutParam, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Statut invalide : " + statutParam).build();
        }

        List<Evenement> events = evenementRepository.findAllWithAllLoaded();
        wsResource.broadcast(new ProgressMessage(true, "loadevents", "Chargement des évènements...", 0));
        Log.debug("Saison demandée : " + saison + ", statuts demandés : " + statutsFiltre);
        List<EvenementDTO> eventsDTO = new ArrayList<EvenementDTO>();
        int nb = 0;
        Date today = new Date();
        for (Evenement evenement : events) {
            boolean statutOk = statutsFiltre == null
                    ? evenement.getStatut() != Status.SUPPRIME
                    : statutsFiltre.contains(evenement.getStatut());
            boolean saisonOk = saison == null || saison.isEmpty() || saison.equals(evenement.getSaison());
            // Sans saison précisée : comportement historique (à venir uniquement).
            // Avec saison précisée : on veut tout voir, passé compris.
            boolean dateOk = saison != null || evenement.getDatedebut().after(today);
            if (dateOk && statutOk && saisonOk) {
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
    @Transactional
    @RolesAllowed("user")
    public Response addEvent(EvenementDTO input) {
        Log.debug("Ajout d'un événement : " + input.uuid);
        Evenement newEvent = null;
        // Ici tu peux transformer en entité Evenement, valider, etc.
        // Exemple simple : retourner ce qu’on a reçu
        if (input.uuid != null) {
            Log.warn("L'évènement a déjà un UUID : " + input.uuid);
            newEvent = Evenement.findById(input.uuid);
            if (newEvent != null) {
                Log.warn("L'évènement existe déjà en base : " + input.uuid);
                return Response.status(Response.Status.CONFLICT)
                        .entity("L'évènement existe déjà en base.").build();
            }
        }
        newEvent = input.toEntity();
        newEvent.setCreatedBy(jwt.getSubject());
        newEvent.persist();
        wsResource.broadcast(new InfoMessage("Evenement ajouté " + input.typeEvenement.activite));
        return Response.status(Response.Status.CREATED).entity(newEvent).build();
    }

    @PUT
    @Transactional
    @RolesAllowed("user")
    public Response modifyEvent(EvenementDTO input) {
        Log.debug("Modification de l'évènement " + input.uuid);
        if (input.uuid == null) {
            Log.warn("L'évènement n'a pas d'UUID pour la modification.");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("L'évènement doit avoir un UUID pour être modifié.").build();
        }

        Evenement existingEvent = Evenement.findById(input.uuid);
        if (existingEvent == null) {
            Log.debug("L'évènement n'existe pas en base : " + input.uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("L'évènement n'existe pas en base.").build();
        }

        // 👇 Vérification : seul le créateur (ou un admin) peut modifier
        String currentUser = jwt.getSubject();
        boolean isAdmin = jwt.getGroups().contains("admin");
        if (!isAdmin && !currentUser.equals(existingEvent.getCreatedBy())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Vous n'êtes pas autorisé à modifier cet évènement.").build();
        }

        if (existingEvent.isPasse()) {
            // Évènement passé : seuls nb participants, président jury et délégué CTR
            // restent modifiables
            existingEvent.setNbparticipants(input.nbparticipants);
            existingEvent.setPresidentjury(input.presidentjury != null ? input.presidentjury.toEntity() : null);
            existingEvent.setDeleguectr(input.deleguectr != null ? input.deleguectr.toEntity() : null);
            existingEvent.persist();
            wsResource.broadcast(
                    new InfoMessage("Évènement passé mis à jour (candidats/jury) : " + existingEvent.getUUID()));
            return Response.status(Response.Status.CREATED).entity(existingEvent).build();
        }

        Evenement updatedEvent = input.toEntity();
        updatedEvent.setCreatedBy(existingEvent.getCreatedBy());
        updatedEvent.persist();
        wsResource.broadcast(new InfoMessage("Evenement modifié " + input.typeEvenement.activite));
        return Response.status(Response.Status.CREATED).entity(updatedEvent).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteEventById(@PathParam("id") String id) {
        UUID uuid = UUID.fromString(id);
        Log.debug("Suppression de l'évènement " + uuid);

        Evenement existingEvent = Evenement.findById(uuid);
        if (existingEvent == null) {
            Log.debug("L'évènement n'existe pas en base : " + uuid);
            return Response.status(Response.Status.CONFLICT)
                    .entity("L'évènement n'existe pas en base.").build();
        }
        existingEvent.setStatut(Status.SUPPRIME);
        existingEvent.persist();
        return Response.ok().build();
    }

    @GET
    @Path("/moniteur/{moniteurid}")
    @RolesAllowed("user")
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
                    if (evenement.getStatut() != Status.SUPPRIME) {
                        eventsDTO.add(EvenementDTO.fromEntity(evenement));
                        wsResource.broadcast(
                                new ProgressMessage(true, "loadevents", "Chargement des évènements...",
                                        (nb / events.size()) * 100));
                        nb++;
                    }
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

    @GET
    @Path("/demandeur/{demandeurid}")
    @RolesAllowed("user")
    public Response getEventsByDemandeurById(@PathParam("demandeurid") String demandeurid) {
        Log.debug("UUID demandé : " + demandeurid);
        wsResource.broadcast(new ProgressMessage(true, "loaddemandeur", "Chargement du demandeur...", 0));
        UUID uuid = UUID.fromString(demandeurid);
        Demandeur demandeur = Demandeur.findById(uuid);
        if (demandeur != null) {
            wsResource.broadcast(new ProgressMessage(true,
                    "loaddemandeur", "Chargement du demandeur terminé...", 100));
            List<EvenementDTO> eventsDTO = new ArrayList<EvenementDTO>();
            int nb = 0;
            Date Today = new Date();
            List<Evenement> events = service.getExamensFor(demandeur);
            for (Evenement evenement : events) {
                if (service.getAnnee(evenement.getDatedebut()) == service.getAnnee(Today) ||
                        service.getAnnee(evenement.getDatedebut()) == service.getAnnee(Today) - 1) {
                    if (evenement.getStatut() != Status.SUPPRIME) {
                        eventsDTO.add(EvenementDTO.fromEntity(evenement));
                        wsResource.broadcast(
                                new ProgressMessage(true, "loadevents", "Chargement des évènements...",
                                        (nb / events.size()) * 100));
                        nb++;
                    }
                }
            }
            wsResource.broadcast(
                    new ProgressMessage(true, "loadevents", "Chargement des évènements terminé...", 100));
            return Response.ok(eventsDTO).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur de chargement du demandeur..."));
            return Response.noContent().build();
        }
    }

    @GET
    @Path("/typeevenement/{typeevenementid}")
    @RolesAllowed("user")
    public Response getEventsByTypeEvenement(@PathParam("typeevenementid") String typeevenementid) {
        Log.debug("UUID demandé : " + typeevenementid);
        wsResource.broadcast(new ProgressMessage(true, "loadtypeevenement", "Chargement du type d'évènement...", 0));
        UUID uuid = UUID.fromString(typeevenementid);
        TypeEvenement typeEvenement = TypeEvenement.findById(uuid);
        if (typeEvenement != null) {
            wsResource.broadcast(new ProgressMessage(true,
                    "loadtypeevenement", "Chargement du type d'évènement terminé...", 100));
            List<EvenementDTO> eventsDTO = new ArrayList<EvenementDTO>();
            int nb = 0;
            Date Today = new Date();
            List<Evenement> events = service.getExamensFor(typeEvenement);
            for (Evenement evenement : events) {
                if (service.getAnnee(evenement.getDatedebut()) == service.getAnnee(Today) ||
                        service.getAnnee(evenement.getDatedebut()) == service.getAnnee(Today) - 1) {
                    if (evenement.getStatut() != Status.SUPPRIME) {
                        eventsDTO.add(EvenementDTO.fromEntity(evenement));
                        wsResource.broadcast(
                                new ProgressMessage(true, "loadevents", "Chargement des évènements...",
                                        (nb / events.size()) * 100));
                        nb++;
                    }
                }
            }
            wsResource.broadcast(
                    new ProgressMessage(true, "loadevents", "Chargement des évènements terminé...", 100));
            return Response.ok(eventsDTO).build();
        } else {
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Erreur de chargement du type d'évènement..."));
            return Response.noContent().build();
        }
    }

    @GET
    @Path("/mes-evenements")
    @RolesAllowed("user")
    public Response getMesEvenements(@QueryParam("statut") String statutParam, @QueryParam("saison") String saison) {
        EnumSet<Status> statutsFiltre;
        try {
            statutsFiltre = parseStatutFilter(statutParam);
        } catch (IllegalArgumentException e) {
            Log.warn("Statut invalide dans le filtre : " + statutParam, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Statut invalide : " + statutParam).build();
        }

        String currentUser = jwt.getSubject(); // 👈 récupère le username depuis le JWT
        Log.debug("Chargement des évènements pour : " + currentUser);
        List<Evenement> events = evenementRepository.findByCreatedBy(currentUser);
        Log.debug("Nb devents de lutilisateur " + currentUser + " = " + events.size());
        List<EvenementDTO> eventsDTO = events.stream()
                .filter(e -> (statutsFiltre == null
                        ? e.getStatut() != Status.SUPPRIME
                        : statutsFiltre.contains(e.getStatut()))
                        && (saison == null || saison.isEmpty() || saison.equals(e.getSaison())))
                .map(EvenementDTO::fromEntity)
                .toList();

        Log.debug("Nb deventsJSON de lutilisateur " + currentUser + " = " + eventsDTO.size());
        return Response.ok(eventsDTO).build();
    }

    @GET
    @Path("/conflict")
    @RolesAllowed("user")
    public Response getEventsInConflict(@QueryParam("debut") String debut, @QueryParam("fin") String fin) {
        Log.info("conflit demandé : " + debut + " - " + fin);
        wsResource.broadcast(new ProgressMessage(true, "conflicts", "Chargement des évènements en conflit...", 0));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<Evenement> conflicts = null;
        Date debutDate = null;
        Date finDate = null;
        try {
            debutDate = df.parse(debut);
            finDate = df.parse(fin);
        } catch (ParseException e) {
            Log.error("Probleme dans le format de la date", e);
            wsResource.broadcast(
                    new InfoMessage("[Erreur]", "Format de date invalide..."));
            return Response.status(Response.Status.BAD_REQUEST).entity("Format de date invalide").build();
        }
        conflicts = service.getAllEvenementsInConflict(debutDate, finDate, wsResource);
        Log.info("Nb de confilcts trouvés : " + conflicts.size());
        if (conflicts != null && conflicts.size() > 0) {
            List<EvenementDTO> eventsDTO = new ArrayList<EvenementDTO>();
            for (Evenement evenement : conflicts) {
                if (evenement.getStatut() != Status.SUPPRIME) {
                    eventsDTO.add(EvenementDTO.fromEntity(evenement));
                }
            }
            wsResource.broadcast(
                    new ProgressMessage(true, "conflicts", "Chargement des évènements en conflit terminé...", 100));
            return Response.ok(eventsDTO).build();
        } else {
            wsResource.broadcast(new ProgressMessage(true, "conflicts",
                    "Chargement des évènements en conflit terminé...", 100));
            wsResource.broadcast(
                    new InfoMessage("[Info]", "Aucun évènement en conflit..."));
            return Response.noContent().build();
        }
    }

    @GET
    @Path("/stats/moniteurs")
    @RolesAllowed("admin")
    public Response getStatsMoniteurs(@QueryParam("saison") String saison) {
        Map<Moniteur, Long> counts = service.getEvenementCountsByMoniteur(saison);
        List<MoniteurDTO> dtos = Moniteur.<Moniteur>listAll().stream()
                .map(m -> {
                    MoniteurDTO dto = MoniteurDTO.fromEntity(m);
                    dto.setNbevents(counts.getOrDefault(m, 0L).intValue());
                    return dto;
                })
                .sorted((a, b) -> b.nbevents - a.nbevents)
                .toList();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/stats/demandeurs")
    @RolesAllowed("admin")
    public Response getStatsDemandeurs(@QueryParam("saison") String saison) {
        Map<Demandeur, Long> counts = service.getEvenementCountsByDemandeur(saison);
        List<DemandeurDTO> dtos = Demandeur.<Demandeur>listAll().stream()
                .map(d -> {
                    DemandeurDTO dto = DemandeurDTO.fromEntity(d);
                    dto.setNbevents(counts.getOrDefault(d, 0L).intValue());
                    return dto;
                })
                .sorted((a, b) -> b.nbevents - a.nbevents)
                .toList();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/stats/types")
    @RolesAllowed("admin")
    public Response getStatsTypes(@QueryParam("saison") String saison) {
        Map<TypeEvenement, Long> counts = service.getEvenementCountsByType(saison);
        List<TypeEvenementDTO> dtos = TypeEvenement.<TypeEvenement>listAll().stream()
                .map(t -> {
                    TypeEvenementDTO dto = TypeEvenementDTO.fromEntity(t);
                    dto.setNbevents(counts.getOrDefault(t, 0L).intValue());
                    return dto;
                })
                .sorted((a, b) -> b.nbevents - a.nbevents)
                .toList();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/stats/saisons")
    @RolesAllowed("user")
    public Response getStatsSaisons() {
        boolean isAdmin = jwt.getGroups().contains("admin");
        String currentUser = isAdmin ? null : jwt.getSubject();
        Map<String, Long> counts = service.getEvenementCountsBySaison(currentUser);
        List<Map<String, Object>> result = counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> Map.<String, Object>of("saison", e.getKey(), "nbevents", e.getValue()))
                .toList();
        return Response.ok(result).build();
    }

    private EnumSet<Status> parseStatutFilter(String statutParam) throws IllegalArgumentException {
        if (statutParam == null || statutParam.isBlank()) {
            return null;
        }
        return EnumSet.copyOf(
                Arrays.stream(statutParam.split(","))
                        .map(String::trim)
                        .map(Status::valueOf)
                        .toList());
    }
}
