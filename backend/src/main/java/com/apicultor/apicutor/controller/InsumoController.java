package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.dto.InsumoInputDTO;
import com.apicultor.apicutor.vo.InsumoVO;
import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Insumo;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.InsumoRepository;
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
@RequestMapping("/api/insumos")
public class InsumoController {

    @Autowired
    private InsumoRepository insumoRepository;

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<InsumoVO>> getAll() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();

        List<Insumo> lista;
        if (isAdmin(usuario)) {
            lista = insumoRepository.findAll();
        } else if (isFuncionario(usuario)) {
            List<Insumo> result = new ArrayList<>();
            if (usuario.getApiariosVinculados() != null) {
                for (Apiario a : usuario.getApiariosVinculados()) {
                    result.addAll(insumoRepository.findByApiario(a));
                }
            }
            lista = result;
        } else {
            lista = insumoRepository.findByApiario_Proprietario(usuario);
        }

        return ResponseEntity.ok(lista.stream().map(InsumoVO::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoVO> getById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Insumo> opt = insumoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Insumo i = opt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, i.getApiario()))
            return ResponseEntity.ok(new InsumoVO(i));
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/apiario/{apiarioId}")
    public ResponseEntity<List<InsumoVO>> getByApiario(@PathVariable Long apiarioId) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Apiario> apiarioOpt = apiarioRepository.findById(apiarioId);
        if (apiarioOpt.isEmpty()) return ResponseEntity.notFound().build();
        Apiario apiario = apiarioOpt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            List<Insumo> lista = insumoRepository.findByApiario(apiario);
            return ResponseEntity.ok(lista.stream().map(InsumoVO::new).collect(Collectors.toList()));
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<InsumoVO> create(@RequestBody InsumoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (input == null || input.getApiarioId() == null) return ResponseEntity.badRequest().build();

        Optional<Apiario> apiarioOpt = apiarioRepository.findById(input.getApiarioId());
        if (apiarioOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Apiario apiario = apiarioOpt.get();

        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            Insumo insumo = new Insumo();
            insumo.setApiario(apiario);
            applyInput(insumo, input);
            insumo = insumoRepository.save(insumo);
            return ResponseEntity.ok(new InsumoVO(insumo));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoVO> update(@PathVariable Long id, @RequestBody InsumoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Insumo> opt = insumoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Insumo insumo = opt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, insumo.getApiario())) {
            applyInput(insumo, input);
            insumo = insumoRepository.save(insumo);
            return ResponseEntity.ok(new InsumoVO(insumo));
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Insumo> opt = insumoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Insumo i = opt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, i.getApiario())) {
            insumoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    private void applyInput(Insumo i, InsumoInputDTO input) {
        i.setTipoInsumo(input.getTipoInsumo());
        i.setDescricao(input.getDescricao());
        i.setQuantidade(input.getQuantidade());
        i.setUnidade(input.getUnidade());
        i.setTipoMovimento(input.getTipoMovimento());
        i.setDataMovimento(input.getDataMovimento());
        i.setObservacoes(input.getObservacoes());
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
