package com.alura.literalura.model;

import jakarta.persistence.*;

@Entity
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String idioma;

    private Double downloads;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Double getDownloads() {
        return downloads;
    }

    public void setDownloads(Double downloads) {
        this.downloads = downloads;
    }

    @ManyToOne
    private Autor autor;

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }


 @Override
public String toString() {

    return """
    -------- LIVRO --------
    Titulo: %s
    Autor: %s
    Idioma: %s
    Downloads: %.0f
    ----------------------
    """.formatted(
            titulo,
            autor.getNome(),
            idioma,
            downloads
    );
}
}