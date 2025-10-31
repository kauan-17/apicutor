package com.apicultor.apicutor.repository;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiarioRepository extends JpaRepository<Apiario, Long> {
    List<Apiario> findByProprietario(Usuario proprietario);
}