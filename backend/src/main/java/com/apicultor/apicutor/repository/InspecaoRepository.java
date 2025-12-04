package com.apicultor.apicutor.repository;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Inspecao;
import com.apicultor.apicutor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspecaoRepository extends JpaRepository<Inspecao, Long> {
    List<Inspecao> findByColmeia(Colmeia colmeia);
    List<Inspecao> findByColmeia_Apiario(Apiario apiario);
    List<Inspecao> findByColmeia_Apiario_Proprietario(Usuario proprietario);
}

