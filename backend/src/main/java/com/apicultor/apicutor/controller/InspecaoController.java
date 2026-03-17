package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.dto.InspecaoInputDTO;
import com.apicultor.apicutor.vo.InspecaoVO;
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
import java.util.stream.Collectors;

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
    public ResponseEntity<List<InspecaoVO>> getAll() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        List<Inspecao> inspecoes;
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (isAdmin(usuario)) {
            inspecoes = inspecaoRepository.findAll();
        } else if (isFuncionario) {
            List<Inspecao> result = new ArrayList<>();
            if (usuario.getApiariosVinculados() != null) {
                for (Apiario a : usuario.getApiariosVinculados()) {
                    result.addAll(inspecaoRepository.findByColmeia_Apiario(a));
                }
            }
            inspecoes = result;
        } else {
            inspecoes = inspecaoRepository.findByColmeia_Apiario_Proprietario(usuario);
        }
        return ResponseEntity.ok(inspecoes.stream().map(InspecaoVO::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspecaoVO> getById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Inspecao> opt = inspecaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Inspecao inspecao = opt.get();
        Apiario apiario = inspecao.getColmeia() != null ? inspecao.getColmeia().getApiario() : null;
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) return ResponseEntity.ok(new InspecaoVO(inspecao));
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/colmeia/{colmeiaId}")
    public ResponseEntity<List<InspecaoVO>> getByColmeia(@PathVariable Long colmeiaId) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(colmeiaId);
        if (colmeiaOpt.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            List<Inspecao> inspecoes = inspecaoRepository.findByColmeia(colmeia);
            return ResponseEntity.ok(inspecoes.stream().map(InspecaoVO::new).collect(Collectors.toList()));
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<InspecaoVO> create(@RequestBody InspecaoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (input == null || input.getColmeiaId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(input.getColmeiaId());
        if (colmeiaOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();

        Inspecao inspecao = new Inspecao();
        inspecao.setColmeia(colmeia);
        inspecao.setResponsavel(usuario);
        applyInput(inspecao, input);
        if (inspecao.getDataHora() == null) inspecao.setDataHora(LocalDateTime.now());
        inspecao = inspecaoRepository.save(inspecao);
        return ResponseEntity.ok(new InspecaoVO(inspecao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InspecaoVO> update(@PathVariable Long id, @RequestBody InspecaoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Inspecao> opt = inspecaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Inspecao inspecao = opt.get();
        Apiario apiario = inspecao.getColmeia() != null ? inspecao.getColmeia().getApiario() : null;
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();

        applyInput(inspecao, input);
        inspecao = inspecaoRepository.save(inspecao);
        return ResponseEntity.ok(new InspecaoVO(inspecao));
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

    private void applyInput(Inspecao inspecao, InspecaoInputDTO input) {
        if (inspecao == null || input == null) return;
        inspecao.setDataHora(input.getDataHora());
        inspecao.setPresencaRainha(input.getPresencaRainha());
        inspecao.setPresencaOvos(input.getPresencaOvos());
        inspecao.setPresencaLarvas(input.getPresencaLarvas());
        inspecao.setQuadrosComCria(input.getQuadrosComCria());
        inspecao.setQuadrosComMel(input.getQuadrosComMel());
        inspecao.setQuadrosComPolen(input.getQuadrosComPolen());
        inspecao.setSinaisDoenças(input.getSinaisDoencas());
        inspecao.setObservacoes(input.getObservacoes());
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
