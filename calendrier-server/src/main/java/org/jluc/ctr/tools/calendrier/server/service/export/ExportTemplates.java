package org.jluc.ctr.tools.calendrier.server.service.export;

import java.util.List;
import java.util.Map;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate (basePath = "ExportTemplates")
public class ExportTemplates {
    public static native TemplateInstance evenements(Map<String, List<LigneExport>> parType, String periode);
}