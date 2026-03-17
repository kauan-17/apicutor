package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.ProducaoRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:producao_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.h2.console.enabled=false"
})
@AutoConfigureMockMvc
class ProducaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    @Autowired
    private ProducaoRepository producaoRepository;

    private Colmeia colmeia;

    @BeforeEach
    void setup() {
        producaoRepository.deleteAll();
        colmeiaRepository.deleteAll();
        apiarioRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("teste");
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("x");
        usuario = usuarioRepository.save(usuario);

        Apiario apiario = new Apiario();
        apiario.setNome("Apiário 1");
        apiario.setProprietario(usuario);
        apiario = apiarioRepository.save(apiario);

        Colmeia c = new Colmeia();
        c.setIdentificacao("C-001");
        c.setApiario(apiario);
        this.colmeia = colmeiaRepository.save(c);
    }

    @Test
    @WithMockUser(username = "teste")
    void createProducao_acceptsColmeiaId() throws Exception {
        String body = "{"
                + "\"colmeiaId\":" + colmeia.getId() + ","
                + "\"dataColheita\":\"2026-03-17\","
                + "\"tipoProduto\":\"MEL\","
                + "\"quantidade\":1.5,"
                + "\"unidadeMedida\":\"KG\","
                + "\"lote\":\"L-001\","
                + "\"observacoes\":\"ok\""
                + "}";

        mockMvc.perform(
                        post("/api/producao")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.colmeiaId").value(colmeia.getId()))
                .andExpect(jsonPath("$.tipoProduto").value("MEL"))
                .andExpect(jsonPath("$.unidadeMedida").value("KG"));
    }
}

