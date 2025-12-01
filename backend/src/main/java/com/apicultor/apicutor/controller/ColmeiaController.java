package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/colmeias")
public class ColmeiaController {

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Colmeia> getAllColmeias() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        boolean isAdmin = hasRole(usuario, "ROLE_ADMIN") || hasRole(usuario, "ADMIN");
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (isAdmin) {
            return colmeiaRepository.findAll();
        }
        if (isFuncionario) {
            // Colmeias dos apiários vinculados ao usuário
            java.util.List<Colmeia> result = new java.util.ArrayList<>();
            for (Apiario a : usuario.getApiariosVinculados()) {
                result.addAll(colmeiaRepository.findByApiario(a));
            }
            return result;
        }
        // Apicultor: colmeias dos apiários do proprietário
        return colmeiaRepository.findByApiarioProprietario(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Colmeia> getColmeiaById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Colmeia> colmeia = colmeiaRepository.findById(id);
        if (colmeia.isEmpty()) return ResponseEntity.notFound().build();
        Apiario apiario = colmeia.get().getApiario();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            return ResponseEntity.ok(colmeia.get());
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<Colmeia> createColmeia(@RequestBody Colmeia colmeia) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Apiario> apiario = apiarioRepository.findById(colmeia.getApiario().getId());
        if (apiario.isEmpty()) return ResponseEntity.badRequest().build();
        Apiario a = apiario.get();
        // Cadastro e ligação: permitido para ADMIN, APICULTOR (proprietário) e FUNCIONARIO vinculado
        if (isAdmin(usuario) || canAccessApiario(usuario, a)) {
            colmeia.setApiario(a);
            return ResponseEntity.ok(colmeiaRepository.save(colmeia));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Colmeia> updateColmeia(@PathVariable Long id, @RequestBody Colmeia colmeiaDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Colmeia> colmeiaOptional = colmeiaRepository.findById(id);
        if (colmeiaOptional.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOptional.get();
        Apiario apiario = colmeia.getApiario();
        // Execução (atualizar): somente ADMIN ou APICULTOR proprietário
        if (isAdmin(usuario) || (apiario.getProprietario().getId().equals(usuario.getId()))) {
            colmeia.setIdentificacao(colmeiaDetails.getIdentificacao());
            colmeia.setTipo(colmeiaDetails.getTipo());
            colmeia.setDataInstalacao(colmeiaDetails.getDataInstalacao());
            colmeia.setObservacoes(colmeiaDetails.getObservacoes());
            colmeia.setStatus(colmeiaDetails.getStatus());
            // Novos campos
            colmeia.setTipoAbelha(colmeiaDetails.getTipoAbelha());
            colmeia.setRainhaStatus(colmeiaDetails.getRainhaStatus());
            colmeia.setOrigemColonia(colmeiaDetails.getOrigemColonia());
            colmeia.setMelgueira(colmeiaDetails.getMelgueira());
            colmeia.setQuantidadeMelgueiras(colmeiaDetails.getQuantidadeMelgueiras());
            return ResponseEntity.ok(colmeiaRepository.save(colmeia));
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColmeia(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Colmeia> colmeiaOptional = colmeiaRepository.findById(id);
        if (colmeiaOptional.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOptional.get();
        Apiario apiario = colmeia.getApiario();
        // Execução (exclusão): somente ADMIN ou APICULTOR proprietário
        if (isAdmin(usuario) || (apiario.getProprietario().getId().equals(usuario.getId()))) {
            colmeiaRepository.delete(colmeia);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }
    
    @GetMapping("/apiario/{apiarioId}")
    public ResponseEntity<List<Colmeia>> getColmeiasByApiario(@PathVariable Long apiarioId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Apiario> apiario = apiarioRepository.findById(apiarioId);
        if (apiario.isEmpty()) return ResponseEntity.notFound().build();
        Apiario a = apiario.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, a)) {
            List<Colmeia> colmeias = colmeiaRepository.findByApiario(a);
            return ResponseEntity.ok(colmeias);
        }
        return ResponseEntity.status(403).build();
    }

    // Helpers de autorização
    private boolean hasRole(Usuario usuario, String role) {
        return usuario.getRoles() != null && usuario.getRoles().contains(role);
    }

    private boolean isAdmin(Usuario usuario) {
        return hasRole(usuario, "ROLE_ADMIN") || hasRole(usuario, "ADMIN");
    }

    private boolean canAccessApiario(Usuario usuario, Apiario apiario) {
        if (usuario == null || apiario == null) return false;
        if (isAdmin(usuario)) return true;
        // proprietário sempre pode
        if (apiario.getProprietario() != null && apiario.getProprietario().getId() != null
                && apiario.getProprietario().getId().equals(usuario.getId())) {
            return true;
        }
        // funcionário vinculado pode acessar (comparando por ID do apiário)
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (!isFuncionario || usuario.getApiariosVinculados() == null) return false;
        Long apiarioId = apiario.getId();
        if (apiarioId == null) return false;
        return usuario.getApiariosVinculados().stream()
                .filter(a -> a != null && a.getId() != null)
                .anyMatch(a -> a.getId().equals(apiarioId));
    }
}
