package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:colmeia_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.h2.console.enabled=false"
})
@AutoConfigureMockMvc
class ColmeiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    private Apiario apiario;
    private Apiario outroApiario;

    @BeforeEach
    void setup() {
        colmeiaRepository.deleteAll();
        apiarioRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("teste");
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("x");
        usuario = usuarioRepository.save(usuario);

        Apiario a = new Apiario();
        a.setNome("Apiário 1");
        a.setProprietario(usuario);
        this.apiario = apiarioRepository.save(a);

        Apiario b = new Apiario();
        b.setNome("Apiário 2");
        b.setProprietario(usuario);
        this.outroApiario = apiarioRepository.save(b);

        Usuario funcionario = new Usuario();
        funcionario.setUsername("func");
        funcionario.setNome("Funcionário");
        funcionario.setEmail("func@example.com");
        funcionario.setSenha("x");
        funcionario.getRoles().add("ROLE_FUNCIONARIO");
        funcionario.getApiariosVinculados().add(apiario);
        usuarioRepository.save(funcionario);
    }

    @Test
    @WithMockUser(username = "teste")
    void createColmeia_acceptsApiarioIdAndIdentificacao() throws Exception {
        String body = "{"
                + "\"apiarioId\":" + apiario.getId() + ","
                + "\"identificacao\":\"C-001\","
                + "\"tipo\":\"LANGSTROTH\","
                + "\"tipoAbelha\":\"EUROPEIA\","
                + "\"rainhaStatus\":\"NOVA\","
                + "\"origemColonia\":\"CAPTURA\","
                + "\"quantidadeMelgueiras\":0,"
                + "\"melgueira\":false,"
                + "\"dataInstalacao\":\"2026-03-17\","
                + "\"status\":\"ATIVA\","
                + "\"observacoes\":\"\""
                + "}";

        mockMvc.perform(
                        post("/api/colmeias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.apiarioId").value(apiario.getId()))
                .andExpect(jsonPath("$.identificacao").value("C-001"));
    }

    @Test
    @WithMockUser(username = "teste")
    void updateColmeia_owner_canUpdate() throws Exception {
        Colmeia c = new Colmeia();
        c.setApiario(apiario);
        c.setIdentificacao("C-OLD");
        c.setTipo(Colmeia.TipoColmeia.LANGSTROTH);
        c.setDataInstalacao(LocalDate.parse("2026-03-17"));
        c.setStatus(Colmeia.StatusColmeia.ATIVA);
        c.setTipoAbelha(Colmeia.TipoAbelha.EUROPEIA);
        c.setRainhaStatus(Colmeia.StatusRainha.NOVA);
        c.setOrigemColonia(Colmeia.OrigemColonia.CAPTURA);
        c.setMelgueira(false);
        c.setQuantidadeMelgueiras(0);
        c = colmeiaRepository.save(c);

        String body = "{"
                + "\"apiarioId\":" + apiario.getId() + ","
                + "\"identificacao\":\"C-NEW\","
                + "\"tipo\":\"LANGSTROTH\","
                + "\"tipoAbelha\":\"EUROPEIA\","
                + "\"rainhaStatus\":\"NOVA\","
                + "\"origemColonia\":\"CAPTURA\","
                + "\"quantidadeMelgueiras\":0,"
                + "\"melgueira\":false,"
                + "\"dataInstalacao\":\"2026-03-17\","
                + "\"status\":\"ATIVA\","
                + "\"observacoes\":\"Atualizada\""
                + "}";

        mockMvc.perform(
                        put("/api/colmeias/" + c.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(c.getId()))
                .andExpect(jsonPath("$.identificacao").value("C-NEW"))
                .andExpect(jsonPath("$.observacoes").value("Atualizada"));
    }

    @Test
    @WithMockUser(username = "func")
    void updateColmeia_funcionario_forbidden() throws Exception {
        Colmeia c = new Colmeia();
        c.setApiario(apiario);
        c.setIdentificacao("C-001");
        c.setTipo(Colmeia.TipoColmeia.LANGSTROTH);
        c.setDataInstalacao(LocalDate.parse("2026-03-17"));
        c.setStatus(Colmeia.StatusColmeia.ATIVA);
        c.setTipoAbelha(Colmeia.TipoAbelha.EUROPEIA);
        c.setRainhaStatus(Colmeia.StatusRainha.NOVA);
        c.setOrigemColonia(Colmeia.OrigemColonia.CAPTURA);
        c.setMelgueira(false);
        c.setQuantidadeMelgueiras(0);
        c = colmeiaRepository.save(c);

        String body = "{"
                + "\"apiarioId\":" + apiario.getId() + ","
                + "\"identificacao\":\"C-NEW\","
                + "\"dataInstalacao\":\"2026-03-17\""
                + "}";

        mockMvc.perform(
                        put("/api/colmeias/" + c.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "teste")
    void deleteColmeia_owner_canDelete() throws Exception {
        Colmeia c = new Colmeia();
        c.setApiario(apiario);
        c.setIdentificacao("C-001");
        c.setTipo(Colmeia.TipoColmeia.LANGSTROTH);
        c.setDataInstalacao(LocalDate.parse("2026-03-17"));
        c.setStatus(Colmeia.StatusColmeia.ATIVA);
        c.setTipoAbelha(Colmeia.TipoAbelha.EUROPEIA);
        c.setRainhaStatus(Colmeia.StatusRainha.NOVA);
        c.setOrigemColonia(Colmeia.OrigemColonia.CAPTURA);
        c.setMelgueira(false);
        c.setQuantidadeMelgueiras(0);
        c = colmeiaRepository.save(c);

        mockMvc.perform(delete("/api/colmeias/" + c.getId()))
                .andExpect(status().isOk());

        assertEquals(0, colmeiaRepository.count());
    }

    @Test
    @WithMockUser(username = "func")
    void deleteColmeia_funcionario_forbidden() throws Exception {
        Colmeia c = new Colmeia();
        c.setApiario(apiario);
        c.setIdentificacao("C-001");
        c.setTipo(Colmeia.TipoColmeia.LANGSTROTH);
        c.setDataInstalacao(LocalDate.parse("2026-03-17"));
        c.setStatus(Colmeia.StatusColmeia.ATIVA);
        c.setTipoAbelha(Colmeia.TipoAbelha.EUROPEIA);
        c.setRainhaStatus(Colmeia.StatusRainha.NOVA);
        c.setOrigemColonia(Colmeia.OrigemColonia.CAPTURA);
        c.setMelgueira(false);
        c.setQuantidadeMelgueiras(0);
        c = colmeiaRepository.save(c);

        mockMvc.perform(delete("/api/colmeias/" + c.getId()))
                .andExpect(status().isForbidden());

        assertEquals(1, colmeiaRepository.count());
    }

    @Test
    @WithMockUser(username = "func")
    void createColmeia_funcionario_canCreateOnLinkedApiario() throws Exception {
        String body = "{"
                + "\"apiarioId\":" + apiario.getId() + ","
                + "\"identificacao\":\"C-FUNC-001\","
                + "\"tipo\":\"LANGSTROTH\","
                + "\"tipoAbelha\":\"EUROPEIA\","
                + "\"rainhaStatus\":\"NOVA\","
                + "\"origemColonia\":\"CAPTURA\","
                + "\"quantidadeMelgueiras\":0,"
                + "\"melgueira\":false,"
                + "\"dataInstalacao\":\"2026-03-17\","
                + "\"status\":\"ATIVA\","
                + "\"observacoes\":\"\""
                + "}";

        mockMvc.perform(
                        post("/api/colmeias")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identificacao").value("C-FUNC-001"));
    }

    @Test
    @WithMockUser(username = "func")
    void listColmeias_funcionario_returnsOnlyLinkedApiarios() throws Exception {
        Colmeia c1 = new Colmeia();
        c1.setApiario(apiario);
        c1.setIdentificacao("C-LINK-001");
        c1.setTipo(Colmeia.TipoColmeia.LANGSTROTH);
        c1.setDataInstalacao(LocalDate.parse("2026-03-17"));
        c1.setStatus(Colmeia.StatusColmeia.ATIVA);
        c1.setTipoAbelha(Colmeia.TipoAbelha.EUROPEIA);
        c1.setRainhaStatus(Colmeia.StatusRainha.NOVA);
        c1.setOrigemColonia(Colmeia.OrigemColonia.CAPTURA);
        c1.setMelgueira(false);
        c1.setQuantidadeMelgueiras(0);
        colmeiaRepository.save(c1);

        Colmeia c2 = new Colmeia();
        c2.setApiario(outroApiario);
        c2.setIdentificacao("C-NOT-LINK-001");
        c2.setTipo(Colmeia.TipoColmeia.LANGSTROTH);
        c2.setDataInstalacao(LocalDate.parse("2026-03-17"));
        c2.setStatus(Colmeia.StatusColmeia.ATIVA);
        c2.setTipoAbelha(Colmeia.TipoAbelha.EUROPEIA);
        c2.setRainhaStatus(Colmeia.StatusRainha.NOVA);
        c2.setOrigemColonia(Colmeia.OrigemColonia.CAPTURA);
        c2.setMelgueira(false);
        c2.setQuantidadeMelgueiras(0);
        colmeiaRepository.save(c2);

        mockMvc.perform(get("/api/colmeias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].apiarioId").value(apiario.getId()))
                .andExpect(jsonPath("$[0].identificacao").value("C-LINK-001"));
    }
}
