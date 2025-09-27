package com.uteq.sgaac.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "postulacion_documento", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"postulacion_id", "tipo_documento"})
})
public class PostulacionDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postulacion_id", nullable = false)
    @JsonIgnore
    private Postulacion postulacion;

    @Column(name = "tipo_documento", nullable = false, length = 50)
    private String tipoDocumento;

    @Column(name = "es_valido", nullable = false)
    private boolean esValido = false;

    // Constructors
    public PostulacionDocumento() {
    }

    public PostulacionDocumento(Postulacion postulacion, String tipoDocumento, boolean esValido) {
        this.postulacion = postulacion;
        this.tipoDocumento = tipoDocumento;
        this.esValido = esValido;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Postulacion getPostulacion() {
        return postulacion;
    }

    public void setPostulacion(Postulacion postulacion) {
        this.postulacion = postulacion;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public boolean isEsValido() {
        return esValido;
    }

    public void setEsValido(boolean esValido) {
        this.esValido = esValido;
    }
}