package com.apicultor.apicutor.repository;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByApiario(Apiario apiario);
}
