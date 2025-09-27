package com.uteq.sgaac.services;

import com.uteq.sgaac.model.Resultado;
import com.uteq.sgaac.model.ResultadoId;
import com.uteq.sgaac.repository.ResultadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResultadoService {

    @Autowired
    private ResultadoRepository resultadoRepository;

    public Resultado save(Resultado resultado) {
        return resultadoRepository.save(resultado);
    }

    public List<Resultado> findAll() {
        return resultadoRepository.findAll();
    }

    public Optional<Resultado> findById(ResultadoId id) {
        return resultadoRepository.findById(id);
    }

    public void deleteById(ResultadoId id) {
        resultadoRepository.deleteById(id);
    }
}
