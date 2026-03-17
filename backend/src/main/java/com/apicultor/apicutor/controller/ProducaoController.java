package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.dto.ProducaoInputDTO;
import com.apicultor.apicutor.vo.ProducaoVO;
import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Producao;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.ProducaoRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import com.apicultor.apicutor.service.ProducaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/producao")
public class ProducaoController {

    @Autowired
    private ProducaoService producaoService;

    @Autowired
    private ProducaoRepository producaoRepository;

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<ProducaoVO>> getAll() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();

        List<Producao> producoes;
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (isAdmin(usuario)) {
            producoes = producaoService.listAll();
        } else if (isFuncionario) {
            List<Producao> result = new ArrayList<>();
            if (usuario.getApiariosVinculados() != null) {
                for (Apiario a : usuario.getApiariosVinculados()) {
                    result.addAll(producaoRepository.findByColmeia_Apiario(a));
                }
            }
            producoes = result;
        } else {
            producoes = producaoRepository.findByColmeia_Apiario_Proprietario(usuario);
        }

        return ResponseEntity.ok(producoes.stream().map(ProducaoVO::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProducaoVO> getById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Producao> opt = producaoService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Producao p = opt.get();
        Apiario apiario = p.getColmeia() != null ? p.getColmeia().getApiario() : null;
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) return ResponseEntity.ok(new ProducaoVO(p));
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/colmeia/{colmeiaId}")
    public ResponseEntity<List<ProducaoVO>> getByColmeia(@PathVariable Long colmeiaId) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(colmeiaId);
        if (colmeiaOpt.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            List<Producao> producoes = producaoRepository.findByColmeia(colmeia);
            return ResponseEntity.ok(producoes.stream().map(ProducaoVO::new).collect(Collectors.toList()));
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<ProducaoVO> create(@RequestBody ProducaoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (input == null || input.getColmeiaId() == null) return ResponseEntity.badRequest().build();

        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(input.getColmeiaId());
        if (colmeiaOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            Producao producao = new Producao();
            producao.setColmeia(colmeia);
            applyInput(producao, input);
            producao = producaoService.save(producao);
            return ResponseEntity.ok(new ProducaoVO(producao));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProducaoVO> update(@PathVariable Long id, @RequestBody ProducaoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Producao> opt = producaoService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Producao producao = opt.get();
        Apiario apiario = producao.getColmeia() != null ? producao.getColmeia().getApiario() : null;
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            applyInput(producao, input);
            producao = producaoService.save(producao);
            return ResponseEntity.ok(new ProducaoVO(producao));
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Producao> opt = producaoService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Producao p = opt.get();
        Apiario apiario = p.getColmeia() != null ? p.getColmeia().getApiario() : null;
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            producaoService.delete(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    private void applyInput(Producao producao, ProducaoInputDTO input) {
        if (producao == null || input == null) return;
        producao.setDataColheita(input.getDataColheita());
        producao.setTipoProduto(input.getTipoProduto());
        producao.setQuantidade(input.getQuantidade());
        producao.setUnidadeMedida(input.getUnidadeMedida());
        producao.setLote(input.getLote());
        producao.setObservacoes(input.getObservacoes());
    }

    // Helpers
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
