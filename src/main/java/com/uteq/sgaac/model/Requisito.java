package com.uteq.sgaac.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "requisito")
@Data
public class Requisito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requisito")
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;
}
