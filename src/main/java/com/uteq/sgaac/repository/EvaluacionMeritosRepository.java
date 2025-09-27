package com.uteq.sgaac.repository;

import com.uteq.sgaac.model.EvaluacionMeritos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionMeritosRepository extends JpaRepository<EvaluacionMeritos, Long> {
}
