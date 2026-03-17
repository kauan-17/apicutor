package com.apicultor.apicutor.repository;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Rainha;
import com.apicultor.apicutor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RainhaRepository extends JpaRepository<Rainha, Long> {
    Optional<Rainha> findByColmeia(Colmeia colmeia);
    List<Rainha> findByColmeia_Apiario(Apiario apiario);
    List<Rainha> findByColmeia_Apiario_Proprietario(Usuario proprietario);
}

