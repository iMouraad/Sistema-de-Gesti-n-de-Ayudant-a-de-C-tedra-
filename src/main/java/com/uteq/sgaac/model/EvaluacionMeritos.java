package com.uteq.sgaac.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "evaluacion_meritos", schema = "public")
public class EvaluacionMeritos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion", nullable = false)
    private Long idEvaluacion;

    // Relación con Postulacion (única)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_postulacion", nullable = false,
                unique = true,
                foreignKey = @ForeignKey(name = "fk_meritos_post"))
    private Postulacion postulacion;

    @Column(name = "puntaje_total", nullable = false, precision = 6, scale = 2)
    private BigDecimal puntajeTotal;

    // Enum de estado (meritos_estado_enum)
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private MeritosEstado estado = MeritosEstado.NO_CALCULADO;

    // --- Getters y Setters ---

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(Long idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public Postulacion getPostulacion() {
        return postulacion;
    }

    public void setPostulacion(Postulacion postulacion) {
        this.postulacion = postulacion;
    }

    public BigDecimal getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(BigDecimal puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }

    public MeritosEstado getEstado() {
        return estado;
    }

    public void setEstado(MeritosEstado estado) {
        this.estado = estado;
    }
}
