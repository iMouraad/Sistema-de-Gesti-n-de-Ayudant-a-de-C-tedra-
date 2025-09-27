package com.uteq.sgaac.services;

import com.uteq.sgaac.model.EvaluacionMeritos;
import com.uteq.sgaac.repository.EvaluacionMeritosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluacionMeritosService {

    @Autowired
    private EvaluacionMeritosRepository evaluacionMeritosRepository;

    public EvaluacionMeritos save(EvaluacionMeritos evaluacion) {
        return evaluacionMeritosRepository.save(evaluacion);
    }

    public List<EvaluacionMeritos> findAll() {
        return evaluacionMeritosRepository.findAll();
    }

    public Optional<EvaluacionMeritos> findById(Long id) {
        return evaluacionMeritosRepository.findById(id);
    }

    public void deleteById(Long id) {
        evaluacionMeritosRepository.deleteById(id);
    }
}
