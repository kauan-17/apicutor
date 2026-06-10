package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.dto.AlimentacaoInputDTO;
import com.apicultor.apicutor.vo.AlimentacaoVO;
import com.apicultor.apicutor.model.Alimentacao;
import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.AlimentacaoRepository;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
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
@RequestMapping("/api/alimentacao")
public class AlimentacaoController {

    @Autowired
    private AlimentacaoRepository alimentacaoRepository;

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<AlimentacaoVO>> getAll() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();

        List<Alimentacao> lista;
        if (isAdmin(usuario)) {
            lista = alimentacaoRepository.findAll();
        } else if (isFuncionario(usuario)) {
            List<Alimentacao> result = new ArrayList<>();
            if (usuario.getApiariosVinculados() != null) {
                for (Apiario a : usuario.getApiariosVinculados()) {
                    result.addAll(alimentacaoRepository.findByColmeia_Apiario(a));
                }
            }
            lista = result;
        } else {
            lista = alimentacaoRepository.findByColmeia_Apiario_Proprietario(usuario);
        }

        return ResponseEntity.ok(lista.stream().map(AlimentacaoVO::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlimentacaoVO> getById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Alimentacao> opt = alimentacaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Alimentacao a = opt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, a.getColmeia() != null ? a.getColmeia().getApiario() : null))
            return ResponseEntity.ok(new AlimentacaoVO(a));
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/colmeia/{colmeiaId}")
    public ResponseEntity<List<AlimentacaoVO>> getByColmeia(@PathVariable Long colmeiaId) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(colmeiaId);
        if (colmeiaOpt.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOpt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, colmeia.getApiario())) {
            List<Alimentacao> lista = alimentacaoRepository.findByColmeia(colmeia);
            return ResponseEntity.ok(lista.stream().map(AlimentacaoVO::new).collect(Collectors.toList()));
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<AlimentacaoVO> create(@RequestBody AlimentacaoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (input == null || input.getColmeiaId() == null) return ResponseEntity.badRequest().build();

        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(input.getColmeiaId());
        if (colmeiaOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Colmeia colmeia = colmeiaOpt.get();

        if (isAdmin(usuario) || canAccessApiario(usuario, colmeia.getApiario())) {
            Alimentacao alimentacao = new Alimentacao();
            alimentacao.setColmeia(colmeia);
            alimentacao.setResponsavel(usuario);
            applyInput(alimentacao, input);
            alimentacao = alimentacaoRepository.save(alimentacao);
            return ResponseEntity.ok(new AlimentacaoVO(alimentacao));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlimentacaoVO> update(@PathVariable Long id, @RequestBody AlimentacaoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Alimentacao> opt = alimentacaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Alimentacao alimentacao = opt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, alimentacao.getColmeia() != null ? alimentacao.getColmeia().getApiario() : null)) {
            applyInput(alimentacao, input);
            alimentacao = alimentacaoRepository.save(alimentacao);
            return ResponseEntity.ok(new AlimentacaoVO(alimentacao));
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Alimentacao> opt = alimentacaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Alimentacao a = opt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, a.getColmeia() != null ? a.getColmeia().getApiario() : null)) {
            alimentacaoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    private void applyInput(Alimentacao a, AlimentacaoInputDTO input) {
        a.setDataAplicacao(input.getDataAplicacao());
        a.setTipoAlimento(input.getTipoAlimento());
        a.setQuantidade(input.getQuantidade());
        a.setUnidade(input.getUnidade());
        a.setObservacoes(input.getObservacoes());
    }

    private Usuario getUsuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        String username = auth.getName();
        if (username == null || "anonymousUser".equalsIgnoreCase(username)) return null;
        return usuarioRepository.findByUsername(username).orElse(null);
    }

    private boolean hasRole(Usuario u, String role) {
        return u.getRoles() != null && u.getRoles().contains(role);
    }

    private boolean isAdmin(Usuario u) {
        return hasRole(u, "ROLE_ADMIN") || hasRole(u, "ADMIN");
    }

    private boolean isFuncionario(Usuario u) {
        return hasRole(u, "ROLE_FUNCIONARIO") || hasRole(u, "FUNCIONARIO");
    }

    private boolean canAccessApiario(Usuario usuario, Apiario apiario) {
        if (usuario == null || apiario == null) return false;
        if (isAdmin(usuario)) return true;
        if (apiario.getProprietario() != null && apiario.getProprietario().getId() != null
                && apiario.getProprietario().getId().equals(usuario.getId())) return true;
        if (!isFuncionario(usuario) || usuario.getApiariosVinculados() == null) return false;
        Long apiarioId = apiario.getId();
        if (apiarioId == null) return false;
        return usuario.getApiariosVinculados().stream()
                .filter(a -> a != null && a.getId() != null)
                .anyMatch(a -> a.getId().equals(apiarioId));
    }
}
