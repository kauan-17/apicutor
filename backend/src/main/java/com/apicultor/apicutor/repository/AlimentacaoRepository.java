package com.apicultor.apicutor.repository;

import com.apicultor.apicutor.model.Alimentacao;
import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlimentacaoRepository extends JpaRepository<Alimentacao, Long> {
    List<Alimentacao> findByColmeia(Colmeia colmeia);
    List<Alimentacao> findByColmeia_Apiario(Apiario apiario);
    List<Alimentacao> findByColmeia_Apiario_Proprietario(Usuario proprietario);
}
