package org.jluc.appli.bibliothequena.server.model.json;

import org.jluc.appli.bibliothequena.server.model.Status;

public class LivreJSON {

    private Long id;
    private String isbn;
    private String name;
    private String author;
    private String image;
    private int note;
    private Status statut = Status.LU;
    private String comment;

    public LivreJSON() {
    }

    public LivreJSON(Long id, String isbn, String name, String author, String image, int note, Status statut,
            String comment) {
        this.id = id;
        this.name = name;
        this.isbn = isbn;
        this.author = author;
        this.image = image;
        this.note = note;
        this.statut = statut;
        this.comment = comment;
    }

    public String toString() {
        return "Livre [isbn = " + isbn + ", name = " + name + ", author = " + author + ", image = " + image
                + ", note = " + note + ", statut = " + statut + ", comment = " + comment + "]";
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getISBN() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public int getNote() {
        return note;
    }

    public String getImage() {
        return image;
    }

    public String getComment() {
        return comment;
    }

    public Status getStatut() {
        return statut;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStatut(Status statut) {
        this.statut = statut;
    }
}
