package org.jluc.ctr.tools.calendrier.server.service.export;

import java.util.List;

public record LigneExport(String type, String demandeur, String lieu, List<String> sessions) {
}