package org.jluc.appli.bibliothequena.server.model;

import java.util.List;

import org.jluc.appli.bibliothequena.server.model.json.LivreJSON;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LivreService {

    public Livre getLivreByISBN(String isbn) {
        Livre livreToReturn = null;
        List<Livre> livres = Livre.listAll();
        for (Livre livre : livres) {
            if (livre.getISBN().equalsIgnoreCase(isbn)) {
                livreToReturn = livre;
                break;
            }
        }
        return livreToReturn;
    }

    public Livre getLivreByName(String name) {
        Livre livreToReturn = null;
        List<Livre> livres = Livre.listAll();
        for (Livre livre : livres) {
            if (livre.getName().equalsIgnoreCase(name)) {
                livreToReturn = livre;
                break;
            }
        }
        return livreToReturn;
    }

    public Livre updateFromJSON(LivreJSON livrejson) {
        Livre livre = getLivreByISBN(livrejson.getISBN());
        if (livre == null) {
            livre = new Livre();
        }
        livre.setISBN(livrejson.getISBN());
        livre.setName(livrejson.getName());
        livre.setAuthor(livrejson.getAuthor());
        livre.setNote(livrejson.getNote());
        livre.setImage(livrejson.getImage());
        livre.setStatut(livrejson.getStatut());
        livre.setComment(livrejson.getComment());
        return livre;
    }

    public LivreJSON toLivreJSON(Livre livre) {
        LivreJSON livrejson = new LivreJSON(livre.id,
                livre.getISBN(), livre.getName(), livre.getAuthor(), livre.getImage(), livre.getNote(),
                livre.getStatut(), livre.getComment());
        return livrejson;
    }

}