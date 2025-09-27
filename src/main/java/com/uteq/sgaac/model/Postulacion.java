package com.uteq.sgaac.model;

import jakarta.persistence.*;
//import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "postulacion", schema = "public")
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_postulacion")
    private Long idPostulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante", nullable = false, foreignKey = @ForeignKey(name = "fk_post_est"))
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asignatura", nullable = false, foreignKey = @ForeignKey(name = "fk_post_asig"))
    private Asignatura asignatura;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_postulacion", nullable = false)
    private PostulacionEstado estadoPostulacion = PostulacionEstado.EN_REVISION;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_convocatoria", nullable = false, foreignKey = @ForeignKey(name = "fk_postulacion_convocatoria"))
    private Convocatoria convocatoria;

    @OneToMany(mappedBy = "postulacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostulacionRequisito> requisitos;

    // Getters and Setters

    public Long getIdPostulacion() {
        return idPostulacion;
    }

    public void setIdPostulacion(Long idPostulacion) {
        this.idPostulacion = idPostulacion;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public PostulacionEstado getEstadoPostulacion() {
        return estadoPostulacion;
    }

    public void setEstadoPostulacion(PostulacionEstado estadoPostulacion) {
        this.estadoPostulacion = estadoPostulacion;
    }

    public Convocatoria getConvocatoria() {
        return convocatoria;
    }

    public void setConvocatoria(Convocatoria convocatoria) {
        this.convocatoria = convocatoria;
    }

    public Set<PostulacionRequisito> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(Set<PostulacionRequisito> requisitos) {
        this.requisitos = requisitos;
    }

    @Column(name = "ruta_solicitud")
    private String rutaSolicitud;

    @Column(name = "ruta_cedula")
    private String rutaCedula;

    @Column(name = "ruta_matricula")
    private String rutaMatricula;

    @Column(name = "ruta_historial")
    private String rutaHistorial;

    public String getRutaSolicitud() {
        return rutaSolicitud;
    }

    public void setRutaSolicitud(String rutaSolicitud) {
        this.rutaSolicitud = rutaSolicitud;
    }

    public String getRutaCedula() {
        return rutaCedula;
    }

    public void setRutaCedula(String rutaCedula) {
        this.rutaCedula = rutaCedula;
    }

    public String getRutaMatricula() {
        return rutaMatricula;
    }

    public void setRutaMatricula(String rutaMatricula) {
        this.rutaMatricula = rutaMatricula;
    }

    public String getRutaHistorial() {
        return rutaHistorial;
    }

    public void setRutaHistorial(String rutaHistorial) {
        this.rutaHistorial = rutaHistorial;
    }

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    @OneToMany(mappedBy = "postulacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PostulacionDocumento> documentos = new java.util.HashSet<>();

    public Set<PostulacionDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(Set<PostulacionDocumento> documentos) {
        this.documentos = documentos;
    }

    public boolean isDocumentoValido(String tipoDocumento) {
        if (this.documentos == null) {
            return false;
        }
        return this.documentos.stream()
                .filter(d -> d.getTipoDocumento().equals(tipoDocumento))
                .findFirst()
                .map(PostulacionDocumento::isEsValido)
                .orElse(false);
    }
}
