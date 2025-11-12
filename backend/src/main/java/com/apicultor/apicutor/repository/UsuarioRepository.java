package com.apicultor.apicutor.repository;

import com.apicultor.apicutor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<Usuario> findByApiarioVinculado_Id(Long apiarioId);
}