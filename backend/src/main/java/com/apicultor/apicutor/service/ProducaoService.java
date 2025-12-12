package com.apicultor.apicutor.service;

import com.apicultor.apicutor.model.Producao;
import com.apicultor.apicutor.repository.ProducaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProducaoService {

    private final ProducaoRepository producaoRepository;

    public ProducaoService(ProducaoRepository producaoRepository) {
        this.producaoRepository = producaoRepository;
    }

    public List<Producao> listAll() {
        return producaoRepository.findAll();
    }

    public Optional<Producao> findById(Long id) {
        return producaoRepository.findById(id);
    }

    public Producao save(Producao producao) {
        return producaoRepository.save(producao);
    }

    public void delete(Long id) {
        producaoRepository.deleteById(id);
    }
}

