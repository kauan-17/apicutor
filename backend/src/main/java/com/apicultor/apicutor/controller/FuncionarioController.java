package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final UsuarioRepository usuarioRepository;
    private final ApiarioRepository apiarioRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioController(UsuarioRepository usuarioRepository, ApiarioRepository apiarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.apiarioRepository = apiarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APICULTOR','ADMIN')")
    public ResponseEntity<?> cadastrarFuncionario(@RequestBody FuncionarioRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario apicultor = usuarioRepository.findByUsername(username).orElseThrow();

        if (usuarioRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nome de usuário já está em uso"));
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email já está em uso"));
        }

        Optional<Apiario> apiarioOpt = apiarioRepository.findById(request.getApiarioId());
        if (apiarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Apiário inválido"));
        }
        Apiario apiario = apiarioOpt.get();
        if (!apiario.getProprietario().getId().equals(apicultor.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Você não é proprietário deste apiário"));
        }

        Usuario funcionario = new Usuario();
        funcionario.setNome(request.getNome());
        funcionario.setUsername(request.getUsername());
        funcionario.setEmail(request.getEmail());
        funcionario.setSenha(passwordEncoder.encode(request.getPassword()));
        funcionario.setRoles(java.util.Collections.singleton("ROLE_FUNCIONARIO"));
        funcionario.getApiariosVinculados().add(apiario);

        usuarioRepository.save(funcionario);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Funcionário cadastrado com sucesso");
        response.put("id", funcionario.getId());
        response.put("apiarioId", apiario.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('APICULTOR','ADMIN')")
    public ResponseEntity<?> listarFuncionariosPorApiario(@RequestParam("apiarioId") Long apiarioId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario apicultor = usuarioRepository.findByUsername(username).orElseThrow();

        Optional<Apiario> apiarioOpt = apiarioRepository.findById(apiarioId);
        if (apiarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Apiário inválido"));
        }
        Apiario apiario = apiarioOpt.get();
        if (!apiario.getProprietario().getId().equals(apicultor.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Você não é proprietário deste apiário"));
        }

        return ResponseEntity.ok(usuarioRepository.findByApiariosVinculados_Id(apiarioId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('APICULTOR','FUNCIONARIO','ADMIN')")
    public ResponseEntity<?> getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();

        Map<String, Object> body = new HashMap<>();
        body.put("id", usuario.getId());
        body.put("username", usuario.getUsername());
        body.put("nome", usuario.getNome());
        body.put("email", usuario.getEmail());
        body.put("roles", usuario.getRoles());
        java.util.Set<Apiario> vinculos = usuario.getApiariosVinculados();
        if (vinculos != null && !vinculos.isEmpty()) {
            java.util.List<Long> ids = vinculos.stream().map(Apiario::getId).toList();
            body.put("apiariosIds", ids);
            java.util.List<Map<String, Object>> apiariosInfo = vinculos.stream().map(a -> {
                Map<String, Object> info = new HashMap<>();
                info.put("id", a.getId());
                info.put("nome", a.getNome());
                info.put("localizacao", a.getLocalizacao());
                return info;
            }).toList();
            body.put("apiarios", apiariosInfo);
            // Compatibilidade com clientes que esperam um único apiário
            body.put("apiarioId", ids.get(0));
            body.put("apiario", apiariosInfo.get(0));
        }
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{funcionarioId}/realocar")
    @PreAuthorize("hasAnyRole('APICULTOR','ADMIN')")
    public ResponseEntity<?> realocarFuncionario(@PathVariable Long funcionarioId, @RequestBody RealocarRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario solicitante = usuarioRepository.findByUsername(username).orElseThrow();

        Optional<Usuario> funcionarioOpt = usuarioRepository.findById(funcionarioId);
        if (funcionarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario funcionario = funcionarioOpt.get();

        Optional<Apiario> destinoOpt = apiarioRepository.findById(request.getApiarioId());
        if (destinoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Apiário de destino inválido"));
        }
        Apiario destino = destinoOpt.get();

        boolean isAdmin = solicitante.getRoles() != null && solicitante.getRoles().contains("ROLE_ADMIN");
        if (!isAdmin && !destino.getProprietario().getId().equals(solicitante.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Você não é proprietário do apiário de destino"));
        }

        // Opcional: garantir que o funcionário atual pertence a algum apiário do solicitante
        if (!isAdmin) {
            boolean pertenceAoSolicitante = funcionario.getApiariosVinculados().stream()
                    .anyMatch(a -> a.getProprietario() != null && a.getProprietario().getId().equals(solicitante.getId()));
            if (!pertenceAoSolicitante && !funcionario.getApiariosVinculados().isEmpty()) {
                return ResponseEntity.status(403).body(Map.of("error", "Funcionário não pertence a um apiário seu"));
            }
        }

        funcionario.getApiariosVinculados().clear();
        funcionario.getApiariosVinculados().add(destino);
        usuarioRepository.save(funcionario);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Funcionário realocado com sucesso");
        response.put("funcionarioId", funcionario.getId());
        response.put("novoApiarioId", destino.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{funcionarioId}/atribuir")
    @PreAuthorize("hasAnyRole('APICULTOR','ADMIN')")
    public ResponseEntity<?> atribuirFuncionario(@PathVariable Long funcionarioId, @RequestBody RealocarRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario solicitante = usuarioRepository.findByUsername(username).orElseThrow();

        Optional<Usuario> funcionarioOpt = usuarioRepository.findById(funcionarioId);
        if (funcionarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario funcionario = funcionarioOpt.get();

        Optional<Apiario> destinoOpt = apiarioRepository.findById(request.getApiarioId());
        if (destinoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Apiário de destino inválido"));
        }
        Apiario destino = destinoOpt.get();

        boolean isAdmin = solicitante.getRoles() != null && solicitante.getRoles().contains("ROLE_ADMIN");
        if (!isAdmin && !destino.getProprietario().getId().equals(solicitante.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Você não é proprietário do apiário de destino"));
        }

        // Adiciona associação (sem remover vínculos anteriores)
        boolean jaVinculado = funcionario.getApiariosVinculados().stream().anyMatch(a -> a.getId().equals(destino.getId()));
        if (!jaVinculado) {
            funcionario.getApiariosVinculados().add(destino);
            usuarioRepository.save(funcionario);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", jaVinculado ? "Funcionário já vinculado a este apiário" : "Funcionário atribuído ao apiário com sucesso");
        response.put("funcionarioId", funcionario.getId());
        response.put("apiarioId", destino.getId());
        return ResponseEntity.ok(response);
    }

    public static class RealocarRequest {
        private Long apiarioId;

        public Long getApiarioId() { return apiarioId; }
        public void setApiarioId(Long apiarioId) { this.apiarioId = apiarioId; }
    }

    public static class FuncionarioRequest {
        private String nome;
        private String username;
        private String email;
        private String password;
        private Long apiarioId;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Long getApiarioId() { return apiarioId; }
        public void setApiarioId(Long apiarioId) { this.apiarioId = apiarioId; }
    }
}