package com.uteq.sgaac.dto;

public class PlazaEstudianteDTO {

    private Long plazaId;
    private String asignaturaNombre;
    private String docenteNombre;
    private Integer cuposDisponibles;

    public PlazaEstudianteDTO(Long plazaId, String asignaturaNombre, String docenteNombre, Integer cuposDisponibles) {
        this.plazaId = plazaId;
        this.asignaturaNombre = asignaturaNombre;
        this.docenteNombre = docenteNombre;
        this.cuposDisponibles = cuposDisponibles;
    }

    // Getters and Setters

    public Long getPlazaId() {
        return plazaId;
    }

    public void setPlazaId(Long plazaId) {
        this.plazaId = plazaId;
    }

    public String getAsignaturaNombre() {
        return asignaturaNombre;
    }

    public void setAsignaturaNombre(String asignaturaNombre) {
        this.asignaturaNombre = asignaturaNombre;
    }

    public String getDocenteNombre() {
        return docenteNombre;
    }

    public void setDocenteNombre(String docenteNombre) {
        this.docenteNombre = docenteNombre;
    }

    public Integer getCuposDisponibles() {
        return cuposDisponibles;
    }

    public void setCuposDisponibles(Integer cuposDisponibles) {
        this.cuposDisponibles = cuposDisponibles;
    }
}
