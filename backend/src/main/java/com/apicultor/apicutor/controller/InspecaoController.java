package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Inspecao;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.InspecaoRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inspecoes")
public class InspecaoController {

    @Autowired
    private InspecaoRepository inspecaoRepository;

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Inspecao> getAll() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return List.of();
        if (isAdmin(usuario)) return inspecaoRepository.findAll();
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (isFuncionario) {
            List<Inspecao> result = new ArrayList<>();
            if (usuario.getApiariosVinculados() != null) {
                for (Apiario a : usuario.getApiariosVinculados()) {
                    result.addAll(inspecaoRepository.findByColmeia_Apiario(a));
                }
            }
            return result;
        }
        return inspecaoRepository.findByColmeia_Apiario_Proprietario(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inspecao> getById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Inspecao> opt = inspecaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Inspecao inspecao = opt.get();
        Apiario apiario = inspecao.getColmeia() != null ? inspecao.getColmeia().getApiario() : null;
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) return ResponseEntity.ok(inspecao);
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/colmeia/{colmeiaId}")
    public ResponseEntity<List<Inspecao>> getByColmeia(@PathVariable Long colmeiaId) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(colmeiaId);
        if (colmeiaOpt.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            return ResponseEntity.ok(inspecaoRepository.findByColmeia(colmeia));
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<Inspecao> create(@RequestBody Inspecao inspecao) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (inspecao.getColmeia() == null || inspecao.getColmeia().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(inspecao.getColmeia().getId());
        if (colmeiaOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();

        inspecao.setColmeia(colmeia);
        inspecao.setResponsavel(usuario);
        if (inspecao.getDataHora() == null) inspecao.setDataHora(LocalDateTime.now());
        return ResponseEntity.ok(inspecaoRepository.save(inspecao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inspecao> update(@PathVariable Long id, @RequestBody Inspecao inspecaoDetails) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Inspecao> opt = inspecaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Inspecao inspecao = opt.get();
        Apiario apiario = inspecao.getColmeia() != null ? inspecao.getColmeia().getApiario() : null;
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();

        inspecao.setDataHora(inspecaoDetails.getDataHora());
        inspecao.setPresencaRainha(inspecaoDetails.getPresencaRainha());
        inspecao.setPresencaOvos(inspecaoDetails.getPresencaOvos());
        inspecao.setPresencaLarvas(inspecaoDetails.getPresencaLarvas());
        inspecao.setQuadrosComCria(inspecaoDetails.getQuadrosComCria());
        inspecao.setQuadrosComMel(inspecaoDetails.getQuadrosComMel());
        inspecao.setQuadrosComPolen(inspecaoDetails.getQuadrosComPolen());
        inspecao.setSinaisDoenças(inspecaoDetails.getSinaisDoenças());
        inspecao.setObservacoes(inspecaoDetails.getObservacoes());

        return ResponseEntity.ok(inspecaoRepository.save(inspecao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Inspecao> opt = inspecaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Inspecao inspecao = opt.get();
        Apiario apiario = inspecao.getColmeia() != null ? inspecao.getColmeia().getApiario() : null;
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();
        inspecaoRepository.delete(inspecao);
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
