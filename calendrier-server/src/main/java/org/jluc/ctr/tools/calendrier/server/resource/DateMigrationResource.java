package org.jluc.ctr.tools.calendrier.server.resource;

import org.jluc.ctr.tools.calendrier.server.service.migration.DateMigrationService;
import org.jluc.ctr.tools.calendrier.server.service.migration.DateMigrationService.MigrationReport;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// ⚠️ ENDPOINT TEMPORAIRE - À SUPPRIMER APRÈS USAGE UNIQUE
@Path("/admin/migration")
public class DateMigrationResource {

    @Inject
    DateMigrationService migrationService;

    @GET
    @Path("/fix-dates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response runMigration() {
        Log.warn("=== DÉBUT MIGRATION DATES (endpoint temporaire) ===");
        MigrationReport report = null;
        // report = migrationService.fixAllDates();
        Log.warn("=== FIN MIGRATION DATES ===");
        return Response.ok(report == null ? "Aucune migration effectuée" : report).build();
    }
}