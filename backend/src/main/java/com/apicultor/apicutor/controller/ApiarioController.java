package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.dto.ApiarioInputDTO;
import com.apicultor.apicutor.vo.ApiarioVO;
import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List; 
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apiarios")
public class ApiarioController {

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<ApiarioVO>> getAllApiarios() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();

        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");

        List<Apiario> apiarios;
        if (isAdmin(usuario)) {
            apiarios = apiarioRepository.findAll();
        } else if (isFuncionario) {
            apiarios = new java.util.ArrayList<>(usuario.getApiariosVinculados());
        } else {
            apiarios = apiarioRepository.findByProprietario(usuario);
        }

        List<ApiarioVO> result = apiarios.stream().map(ApiarioVO::new).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiarioVO> getApiarioById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();

        Optional<Apiario> apiarioOpt = apiarioRepository.findById(id);
        if (apiarioOpt.isEmpty()) return ResponseEntity.notFound().build();
        Apiario apiario = apiarioOpt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            return ResponseEntity.ok(new ApiarioVO(apiario));
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<ApiarioVO> createApiario(@RequestBody ApiarioInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (input == null || input.getNome() == null || input.getNome().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Apiario apiario = new Apiario();
        apiario.setNome(input.getNome().trim());
        apiario.setLocalizacao(input.getLocalizacao());
        apiario.setLatitude(input.getLatitude());
        apiario.setLongitude(input.getLongitude());
        apiario.setDescricao(input.getDescricao());
        apiario.setProprietario(usuario);
        apiario = apiarioRepository.save(apiario);
        return ResponseEntity.ok(new ApiarioVO(apiario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiarioVO> updateApiario(@PathVariable Long id, @RequestBody ApiarioInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();

        Optional<Apiario> apiarioOptional = apiarioRepository.findById(id);
        if (apiarioOptional.isEmpty()) return ResponseEntity.notFound().build();
        Apiario apiario = apiarioOptional.get();
        if (!isAdmin(usuario) && (apiario.getProprietario() == null || !apiario.getProprietario().getId().equals(usuario.getId()))) {
            return ResponseEntity.status(403).build();
        }
        if (input != null) {
            if (input.getNome() != null) apiario.setNome(input.getNome());
            apiario.setLocalizacao(input.getLocalizacao());
            apiario.setLatitude(input.getLatitude());
            apiario.setLongitude(input.getLongitude());
            apiario.setDescricao(input.getDescricao());
        }
        apiario = apiarioRepository.save(apiario);
        return ResponseEntity.ok(new ApiarioVO(apiario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteApiario(@PathVariable Long id) {
        Optional<Apiario> apiarioOptional = apiarioRepository.findById(id);
        if (apiarioOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        apiarioRepository.delete(apiarioOptional.get());
        return ResponseEntity.ok().build();
    }

    private Usuario getUsuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        String username = auth.getName();
        if (username == null || "anonymousUser".equalsIgnoreCase(username)) return null;
        return usuarioRepository.findByUsername(username).orElse(null);
    }

    private boolean hasRole(Usuario usuario, String role) {
        return usuario.getRoles() != null && usuario.getRoles().contains(role);
    }

    private boolean isAdmin(Usuario usuario) {
        return hasRole(usuario, "ROLE_ADMIN") || hasRole(usuario, "ADMIN");
    }

    private boolean canAccessApiario(Usuario usuario, Apiario apiario) {
        if (usuario == null || apiario == null) return false;
        if (isAdmin(usuario)) return true;
        if (apiario.getProprietario() != null && apiario.getProprietario().getId() != null
                && apiario.getProprietario().getId().equals(usuario.getId())) {
            return true;
        }
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (!isFuncionario || usuario.getApiariosVinculados() == null) return false;
        Long apiarioId = apiario.getId();
        if (apiarioId == null) return false;
        return usuario.getApiariosVinculados().stream()
                .filter(a -> a != null && a.getId() != null)
                .anyMatch(a -> a.getId().equals(apiarioId));
    }
}
