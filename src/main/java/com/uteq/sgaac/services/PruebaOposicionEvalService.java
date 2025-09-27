package com.uteq.sgaac.services;

import com.uteq.sgaac.model.Docente;
import com.uteq.sgaac.model.PruebaOposicion;
import com.uteq.sgaac.model.PruebaOposicionEval;
import com.uteq.sgaac.repository.DocenteRepository;
import com.uteq.sgaac.repository.PruebaOposicionEvalRepository;
import com.uteq.sgaac.repository.PruebaOposicionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class PruebaOposicionEvalService {

    private final PruebaOposicionEvalRepository pruebaOposicionEvalRepository;
    private final PruebaOposicionRepository pruebaOposicionRepository;
    private final DocenteRepository docenteRepository;
    private static final BigDecimal UMBRAL_APROBACION = new BigDecimal("14.00"); // Aprobación con 14/20

    public PruebaOposicionEvalService(PruebaOposicionEvalRepository pruebaOposicionEvalRepository, PruebaOposicionRepository pruebaOposicionRepository, DocenteRepository docenteRepository) {
        this.pruebaOposicionEvalRepository = pruebaOposicionEvalRepository;
        this.pruebaOposicionRepository = pruebaOposicionRepository;
        this.docenteRepository = docenteRepository;
    }

    @Transactional
    public void guardarYCalcularResultados(Long idPruebaOposicion, List<PruebaOposicionEval> evaluaciones) {
        PruebaOposicion prueba = pruebaOposicionRepository.findById(idPruebaOposicion)
                .orElseThrow(() -> new IllegalArgumentException("Prueba de oposición no encontrada con ID: " + idPruebaOposicion));

        // Limpiar evaluaciones anteriores para esta prueba
        pruebaOposicionEvalRepository.deleteByOposicion(prueba);

        BigDecimal puntajeSumaTotal = BigDecimal.ZERO;
        int numeroEvaluadores = 0;

        for (PruebaOposicionEval eval : evaluaciones) {
            // Asegurarse de que el docente está cargado y asignado
            Docente docente = docenteRepository.findById(eval.getDocente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));
            eval.setDocente(docente);
            eval.setOposicion(prueba);

            BigDecimal puntajeIndividual = eval.getMaterialPuntos().add(eval.getCalidadPuntos()).add(eval.getRespuestasPuntos());
            
            pruebaOposicionEvalRepository.save(eval);
            puntajeSumaTotal = puntajeSumaTotal.add(puntajeIndividual);
            numeroEvaluadores++;
        }

        BigDecimal puntajePromedio = BigDecimal.ZERO;
        if (numeroEvaluadores > 0) {
            puntajePromedio = puntajeSumaTotal.divide(new BigDecimal(numeroEvaluadores), 2, RoundingMode.HALF_UP);
        }

        prueba.setPuntajeTotal(puntajePromedio);
        prueba.setEstado("CALIFICADA");

        if (puntajePromedio.compareTo(UMBRAL_APROBACION) >= 0) {
            prueba.setResultado("APROBADO");
        } else {
            prueba.setResultado("REPROBADO");
        }

        pruebaOposicionRepository.save(prueba);
    }

    public Optional<PruebaOposicionEval> findById(Long id) {
        return pruebaOposicionEvalRepository.findById(id);
    }
}
