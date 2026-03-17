package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.dto.TarefaInputDTO;
import com.apicultor.apicutor.dto.TarefaVO;
import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Tarefa;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.TarefaRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<TarefaVO>> listByApiario(@RequestParam("apiarioId") Long apiarioId) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Apiario> apiarioOpt = apiarioRepository.findById(apiarioId);
        if (apiarioOpt.isEmpty()) return ResponseEntity.notFound().build();
        Apiario apiario = apiarioOpt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            List<Tarefa> tarefas = tarefaRepository.findByApiario(apiario);
            List<TarefaVO> vos = tarefas.stream().map(TarefaVO::new).collect(Collectors.toList());
            return ResponseEntity.ok(vos);
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<TarefaVO> create(@RequestBody TarefaInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (input.apiarioId == null || input.titulo == null || input.titulo.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Apiario> apiarioOpt = apiarioRepository.findById(input.apiarioId);
        if (apiarioOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Apiario apiario = apiarioOpt.get();
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario) || isFuncionario) {
            Tarefa t = new Tarefa();
            t.setApiario(apiario);
            t.setTitulo(input.titulo.trim());
            t.setPrazo(parseDate(input.prazo));
            String status = input.status == null || input.status.trim().isEmpty() ? "Pendente" : input.status;
            t.setStatus(status);
            t = tarefaRepository.save(t);
            return ResponseEntity.ok(new TarefaVO(t));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaVO> update(@PathVariable Long id, @RequestBody TarefaInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Tarefa> opt = tarefaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Tarefa t = opt.get();
        Apiario apiario = t.getApiario();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            if (input.titulo != null) t.setTitulo(input.titulo.trim());
            if (input.prazo != null) t.setPrazo(parseDate(input.prazo));
            if (input.status != null) t.setStatus(input.status);
            t = tarefaRepository.save(t);
            return ResponseEntity.ok(new TarefaVO(t));
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Tarefa> opt = tarefaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Tarefa t = opt.get();
        Apiario apiario = t.getApiario();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            tarefaRepository.delete(t);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    private LocalDate parseDate(String iso) {
        try {
            return iso == null || iso.isBlank() ? null : LocalDate.parse(iso);
        } catch (Exception e) {
            return null;
        }
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
