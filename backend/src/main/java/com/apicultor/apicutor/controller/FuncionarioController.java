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
        funcionario.setApiarioVinculado(apiario);

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

        return ResponseEntity.ok(usuarioRepository.findByApiarioVinculado_Id(apiarioId));
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