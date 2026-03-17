package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.dto.RainhaInputDTO;
import com.apicultor.apicutor.vo.RainhaVO;
import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Rainha;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.RainhaRepository;
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
@RequestMapping("/api/rainhas")
public class RainhaController {

    @Autowired
    private RainhaRepository rainhaRepository;

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<RainhaVO>> getAll() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();

        List<Rainha> rainhas;
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (isAdmin(usuario)) {
            rainhas = rainhaRepository.findAll();
        } else if (isFuncionario) {
            List<Rainha> result = new ArrayList<>();
            if (usuario.getApiariosVinculados() != null) {
                for (Apiario a : usuario.getApiariosVinculados()) {
                    result.addAll(rainhaRepository.findByColmeia_Apiario(a));
                }
            }
            rainhas = result;
        } else {
            rainhas = rainhaRepository.findByColmeia_Apiario_Proprietario(usuario);
        }

        return ResponseEntity.ok(rainhas.stream().map(RainhaVO::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RainhaVO> getById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Rainha> opt = rainhaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Rainha rainha = opt.get();
        Apiario apiario = rainha.getColmeia() != null ? rainha.getColmeia().getApiario() : null;
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) return ResponseEntity.ok(new RainhaVO(rainha));
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/colmeia/{colmeiaId}")
    public ResponseEntity<RainhaVO> getByColmeia(@PathVariable Long colmeiaId) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(colmeiaId);
        if (colmeiaOpt.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();
        Rainha rainha = colmeia.getRainha();
        return ResponseEntity.ok(rainha != null ? new RainhaVO(rainha) : null);
    }

    @PostMapping
    public ResponseEntity<RainhaVO> create(@RequestBody RainhaInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (input == null || input.getColmeiaId() == null) return ResponseEntity.badRequest().build();
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(input.getColmeiaId());
        if (colmeiaOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();

        Rainha rainha = colmeia.getRainha() != null ? colmeia.getRainha() : new Rainha();
        applyInput(rainha, input);
        rainha = rainhaRepository.save(rainha);

        colmeia.setRainha(rainha);
        rainha.setColmeia(colmeia);
        colmeiaRepository.save(colmeia);

        return ResponseEntity.ok(new RainhaVO(rainha));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RainhaVO> update(@PathVariable Long id, @RequestBody RainhaInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Rainha> opt = rainhaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Rainha rainha = opt.get();
        Apiario apiario = rainha.getColmeia() != null ? rainha.getColmeia().getApiario() : null;
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();

        applyInput(rainha, input);
        rainha = rainhaRepository.save(rainha);

        if (input != null && input.getColmeiaId() != null) {
            Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(input.getColmeiaId());
            if (colmeiaOpt.isEmpty()) return ResponseEntity.badRequest().build();
            Colmeia target = colmeiaOpt.get();
            if (!isAdmin(usuario) && !canAccessApiario(usuario, target.getApiario())) return ResponseEntity.status(403).build();

            Colmeia atual = rainha.getColmeia();
            if (atual != null && atual.getId() != null && !atual.getId().equals(target.getId())) {
                atual.setRainha(null);
                colmeiaRepository.save(atual);
            }
            target.setRainha(rainha);
            rainha.setColmeia(target);
            colmeiaRepository.save(target);
        }

        return ResponseEntity.ok(new RainhaVO(rainha));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Rainha> opt = rainhaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Rainha rainha = opt.get();
        Apiario apiario = rainha.getColmeia() != null ? rainha.getColmeia().getApiario() : null;
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();

        Colmeia colmeia = rainha.getColmeia();
        if (colmeia != null) {
            colmeia.setRainha(null);
            colmeiaRepository.save(colmeia);
        }
        rainhaRepository.delete(rainha);
        return ResponseEntity.ok().build();
    }

    private void applyInput(Rainha rainha, RainhaInputDTO input) {
        if (input == null || rainha == null) return;
        if (input.getMarcacao() != null) rainha.setMarcacao(input.getMarcacao());
        if (input.getRaca() != null) rainha.setRaca(input.getRaca());
        if (input.getDataNascimento() != null) rainha.setDataNascimento(input.getDataNascimento());
        if (input.getDataIntroducao() != null) rainha.setDataIntroducao(input.getDataIntroducao());
        if (input.getOrigem() != null) rainha.setOrigem(input.getOrigem());
        if (input.getObservacoes() != null) rainha.setObservacoes(input.getObservacoes());
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
