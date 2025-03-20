package org.jluc.ctr.tools.calendrier.server.evenements;

public enum TypeActivite {
	ALL("Tout"), N4_GP("N4 - GP"), INITIATEUR("Initiateur"), TSI("TSI"), MF1("MF1"), MF2("MF2"), TIV("TIV"),
	SECOURISME("Secourisme"), HANDISUB("HandiSub");

	private String label;

	TypeActivite(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}
