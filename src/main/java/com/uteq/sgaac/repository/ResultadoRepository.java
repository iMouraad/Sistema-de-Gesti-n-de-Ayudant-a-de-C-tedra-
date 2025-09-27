package com.uteq.sgaac.repository;

import com.uteq.sgaac.model.Resultado;
import com.uteq.sgaac.model.ResultadoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultadoRepository extends JpaRepository<Resultado, ResultadoId> {
}
