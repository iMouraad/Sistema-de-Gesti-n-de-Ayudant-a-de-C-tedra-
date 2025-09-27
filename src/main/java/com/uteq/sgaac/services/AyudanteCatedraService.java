package com.uteq.sgaac.services;

import com.uteq.sgaac.model.AyudanteCatedra;
import com.uteq.sgaac.repository.AyudanteCatedraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AyudanteCatedraService {

    @Autowired
    private AyudanteCatedraRepository ayudanteCatedraRepository;

    public AyudanteCatedra save(AyudanteCatedra ayudante) {
        return ayudanteCatedraRepository.save(ayudante);
    }

    public List<AyudanteCatedra> findAll() {
        return ayudanteCatedraRepository.findAll();
    }

    public Optional<AyudanteCatedra> findById(Long id) {
        return ayudanteCatedraRepository.findById(id);
    }

    public void deleteById(Long id) {
        ayudanteCatedraRepository.deleteById(id);
    }
}
