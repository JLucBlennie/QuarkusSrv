package org.jluc.ctr.tools.calendrier.server.service.google.calendar;

public enum TypeCalendar {
    CTR(3, "planning.ctrbpl@gmail.com"), TIV(4, "TIV CTR BPL"), RECYCLEUR(5, "Recycleur BPL"), MF2(6, "MF2 CTR BPL"), MODULE_20_40(7, "Module 20-40"), ANTEOR(8, "ANTEOR"), GP_N4(9,
            "GP N4 CTR BPL"), RIFA(10, "RIFA"), TSI(11, "TSI"), HANDI(12, "Handi"), MF1(13, "MF1 CTR BPL"), MODULE_6_20(14, "Module 6 - 20"), INITIATEUR(15, "INITIATEUR CTR BPL"), TEK(16, "TEK");

    String name;
    int index;

    TypeCalendar(int i, String name) {
        index = i;
        this.name = name;
    }

    String getCalendarName(){
        return name;
    }
}
