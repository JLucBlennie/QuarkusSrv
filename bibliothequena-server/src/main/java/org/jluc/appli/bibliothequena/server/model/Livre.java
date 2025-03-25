package org.jluc.appli.bibliothequena.server.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Livre extends PanacheEntity {
    /* "id": 1,
    "isbn": "9782253262244",
    "name": "Qui ne se plante pas ne pousse jamais",
    "author": "Sophie TAL MEN",
    "image": "",
    "note": "4",
    "statut": "Lu"
 */
    private String isbn;
    private String name;
    private String author;
    private String image;
    private int note;
    private Status statut = Status.LU;
    private String comment;
    
    public Livre() {
    }
    
    public Livre(String isbn, String name, String author, String image, int note, Status statut, String comment) {
        this.name = name;
        this.isbn = isbn;
        this.author = author;
        this.image = image;
        this.note = note;
        this.statut = statut;
        this.comment = comment;
    }

    public String toString() {
        return "Livre [isbn = " + isbn + ", name = " + name + "]";
    }

    public String getName() {
        return name;
    }

    public String getISBN() {
        return isbn;
    }

    public String getAuthor(){
        return author;
    }

    public int getNote(){
        return note;
    }

    public String getImage(){
        return image;
    }

    public String getComment(){
        return comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setNote(int note){
        this.note = note;
    }

    public void setImage(String image){
        this.image = image;
    }

    public void setComment(String comment){
        this.comment= comment;
    }
}