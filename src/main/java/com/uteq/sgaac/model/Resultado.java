package com.uteq.sgaac.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "resultado", schema = "public")
public class Resultado {

    @EmbeddedId
    private ResultadoId id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId("postulacionId") // Maps the 'postulacionId' attribute of the embedded ID
    @JoinColumn(name = "id_postulacion")
    private Postulacion postulacion;

    // Enum estado_final (resultado_estado_enum)
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_final", nullable = false)
    private ResultadoEstado estadoFinal;

    @Column(name = "puntaje_total_final", precision = 6, scale = 2)
    private BigDecimal puntajeTotalFinal;

    // --- Getters y Setters ---

    public ResultadoId getId() {
        return id;
    }

    public void setId(ResultadoId id) {
        this.id = id;
    }

    public Postulacion getPostulacion() {
        return postulacion;
    }

    public void setPostulacion(Postulacion postulacion) {
        this.postulacion = postulacion;
    }

    public ResultadoEstado getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(ResultadoEstado estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public BigDecimal getPuntajeTotalFinal() {
        return puntajeTotalFinal;
    }

    public void setPuntajeTotalFinal(BigDecimal puntajeTotalFinal) {
        this.puntajeTotalFinal = puntajeTotalFinal;
    }
}
