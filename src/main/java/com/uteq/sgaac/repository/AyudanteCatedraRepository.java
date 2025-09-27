package com.uteq.sgaac.repository;

import com.uteq.sgaac.model.AyudanteCatedra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AyudanteCatedraRepository extends JpaRepository<AyudanteCatedra, Long> {
}
