package com.apicultor.apicutor.service;

import com.apicultor.apicutor.model.Inspecao;
import com.apicultor.apicutor.repository.InspecaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InspecaoService {

    @Autowired
    private InspecaoRepository inspecaoRepository;

    public List<Inspecao> listAll() {
        return inspecaoRepository.findAll();
    }

    public Optional<Inspecao> findById(Long id) {
        return inspecaoRepository.findById(id);
    }

    public Inspecao save(Inspecao inspecao) {
        if (inspecao.getDataHora() == null) {
            inspecao.setDataHora(LocalDateTime.now());
        }
        return inspecaoRepository.save(inspecao);
    }

    public void deleteById(Long id) {
        inspecaoRepository.deleteById(id);
    }
}

