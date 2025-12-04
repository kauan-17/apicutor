package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Inspecao;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.InspecaoRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import com.apicultor.apicutor.service.InspecaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inspecoes")
public class InspecaoController {

    @Autowired
    private InspecaoService inspecaoService;

    @Autowired
    private InspecaoRepository inspecaoRepository;

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Inspecao> getAllInspecoes() {
        Usuario usuario = getUsuarioAtual();
        if (isAdmin(usuario)) {
            return inspecaoService.listAll();
        }
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
        // Apicultor: inspeções das colmeias dos seus apiários
        return inspecaoRepository.findByColmeia_Apiario_Proprietario(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inspecao> getInspecaoById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        Optional<Inspecao> inspecaoOpt = inspecaoService.findById(id);
        if (inspecaoOpt.isEmpty()) return ResponseEntity.notFound().build();
        Inspecao inspecao = inspecaoOpt.get();
        Apiario apiario = inspecao.getColmeia() != null ? inspecao.getColmeia().getApiario() : null;
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            return ResponseEntity.ok(inspecao);
        }
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/colmeia/{colmeiaId}")
    public ResponseEntity<List<Inspecao>> getInspecoesByColmeia(@PathVariable Long colmeiaId) {
        Usuario usuario = getUsuarioAtual();
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
    public ResponseEntity<Inspecao> createInspecao(@RequestBody Inspecao inspecao) {
        Usuario usuario = getUsuarioAtual();
        if (inspecao.getColmeia() == null || inspecao.getColmeia().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(inspecao.getColmeia().getId());
        if (colmeiaOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario) || isFuncionario) {
            inspecao.setColmeia(colmeia);
            inspecao.setResponsavel(usuario);
            return ResponseEntity.ok(inspecaoService.save(inspecao));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inspecao> updateInspecao(@PathVariable Long id, @RequestBody Inspecao inspecaoDetails) {
        Usuario usuario = getUsuarioAtual();
        Optional<Inspecao> inspecaoOpt = inspecaoService.findById(id);
        if (inspecaoOpt.isEmpty()) return ResponseEntity.notFound().build();
        Inspecao inspecao = inspecaoOpt.get();
        Apiario apiario = inspecao.getColmeia() != null ? inspecao.getColmeia().getApiario() : null;
        // Atualização: ADMIN ou APICULTOR proprietário
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            inspecao.setDataHora(inspecaoDetails.getDataHora());
            inspecao.setPresencaRainha(inspecaoDetails.getPresencaRainha());
            inspecao.setPresencaOvos(inspecaoDetails.getPresencaOvos());
            inspecao.setPresencaLarvas(inspecaoDetails.getPresencaLarvas());
            inspecao.setQuadrosComCria(inspecaoDetails.getQuadrosComCria());
            inspecao.setQuadrosComMel(inspecaoDetails.getQuadrosComMel());
            inspecao.setQuadrosComPolen(inspecaoDetails.getQuadrosComPolen());
            inspecao.setSinaisDoenças(inspecaoDetails.getSinaisDoenças());
            inspecao.setObservacoes(inspecaoDetails.getObservacoes());
            return ResponseEntity.ok(inspecaoService.save(inspecao));
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInspecao(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        Optional<Inspecao> inspecaoOpt = inspecaoService.findById(id);
        if (inspecaoOpt.isEmpty()) return ResponseEntity.notFound().build();
        Inspecao inspecao = inspecaoOpt.get();
        Apiario apiario = inspecao.getColmeia() != null ? inspecao.getColmeia().getApiario() : null;
        // Exclusão: ADMIN ou APICULTOR proprietário
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            inspecaoService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    // Helpers
    private Usuario getUsuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return usuarioRepository.findByUsername(username).orElseThrow();
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

