package com.uteq.sgaac.services;

import com.uteq.sgaac.dto.PlazaEstudianteDTO;
import com.uteq.sgaac.dto.PlazaFormDto;
import com.uteq.sgaac.model.usuario_sistema;

import java.util.List;

public interface PlazaService {
    void createPlazas(PlazaFormDto formDto);

    List<PlazaEstudianteDTO> findPlazasParaEstudiante(usuario_sistema usuario);
}
