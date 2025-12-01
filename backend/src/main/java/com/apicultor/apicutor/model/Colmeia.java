package com.apicultor.apicutor.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "colmeias")
public class Colmeia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String identificacao;
    
    @Enumerated(EnumType.STRING)
    private TipoColmeia tipo;
    
    private LocalDate dataInstalacao;
    
    @ManyToOne
    @JoinColumn(name = "apiario_id", nullable = false)
    @JsonBackReference
    private Apiario apiario;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rainha_id")
    @JsonManagedReference
    private Rainha rainha;
    
    @OneToMany(mappedBy = "colmeia", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Inspecao> inspecoes = new ArrayList<>();
    
    @OneToMany(mappedBy = "colmeia", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Producao> producoes = new ArrayList<>();
    
    private String observacoes;
    
    @Enumerated(EnumType.STRING)
    private StatusColmeia status = StatusColmeia.ATIVA;

    // Novos campos solicitados
    @Enumerated(EnumType.STRING)
    private TipoAbelha tipoAbelha;

    @Enumerated(EnumType.STRING)
    private StatusRainha rainhaStatus; // NOVA ou ANTIGA

    @Enumerated(EnumType.STRING)
    private OrigemColonia origemColonia; // CAPTURA ou DIVISAO

    private Boolean melgueira = false; // indica se possui melgueira

    // Quantidade de melgueiras (se o backend receber este campo)
    // Mantém compatibilidade: quando quantidade > 0, frontend também envia melgueira=true
    private Integer quantidadeMelgueiras = 0;
    
    public enum TipoColmeia {
        LANGSTROTH, DADANT, SCHENK, WARRÉ, QUENIANA, OUTRO
    }

    public enum StatusColmeia {
        ATIVA, INATIVA, PERDIDA, EM_OBSERVACAO, DOENTE
    }

    public enum TipoAbelha {
        AFRICANA, EUROPEIA, CARNICA, ITALIANA, MISTA, OUTRA
    }

    public enum StatusRainha {
        NOVA, ANTIGA
    }

    public enum OrigemColonia {
        CAPTURA, DIVISAO
    }
}
