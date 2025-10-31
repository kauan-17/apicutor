package com.apicultor.apicutor.repository;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColmeiaRepository extends JpaRepository<Colmeia, Long> {
    List<Colmeia> findByApiario(Apiario apiario);
    List<Colmeia> findByApiarioProprietario(Usuario proprietario);
}