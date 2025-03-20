package org.jluc.appli.bibliothequena.server.model;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LivreService {

    public String getLivreByISBN(String isbn) {
        List<Livre> livres = Livre.listAll();
        String name = "Livre Not Found ==> " + isbn;
        for (Livre livre : livres) {
            if (livre.getISBN().equalsIgnoreCase(isbn)) {
                name = livre.getName();
                break;
            }
        }
        return "Livre " + name;
    }

    public String getLivreByName(String name) {
        List<Livre> livres = Livre.listAll();
        String isbn = "Livre non trouvéFound ==> " + name;
        for (Livre livre : livres) {
            if (livre.getName().equalsIgnoreCase(name)) {
                isbn = livre.getISBN();
                break;
            }
        }
        return "Livre " + name + " --> ISBN : " + isbn;
    }

}