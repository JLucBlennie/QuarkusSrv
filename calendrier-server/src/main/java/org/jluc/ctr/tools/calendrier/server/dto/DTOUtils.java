package org.jluc.ctr.tools.calendrier.server.dto;

import java.util.Date;

public class DTOUtils {
    private DTOUtils() {
    }

    public static Date toDate(long timestamp) {
        return timestamp > 0 ? new Date(timestamp) : null;
    }
}
