package com.apicultor.apicutor.repository;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Producao;
import com.apicultor.apicutor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProducaoRepository extends JpaRepository<Producao, Long> {
    List<Producao> findByColmeia(Colmeia colmeia);
    List<Producao> findByColmeia_Apiario(Apiario apiario);
    List<Producao> findByColmeia_Apiario_Proprietario(Usuario proprietario);
    List<Producao> findByColmeia_ApiarioAndDataColheitaBetween(Apiario apiario, LocalDate inicio, LocalDate fim);
}

