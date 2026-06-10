package com.apicultor.apicutor.repository;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Tratamento;
import com.apicultor.apicutor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TratamentoRepository extends JpaRepository<Tratamento, Long> {
    List<Tratamento> findByColmeia(Colmeia colmeia);
    List<Tratamento> findByColmeia_Apiario(Apiario apiario);
    List<Tratamento> findByColmeia_Apiario_Proprietario(Usuario proprietario);
}
