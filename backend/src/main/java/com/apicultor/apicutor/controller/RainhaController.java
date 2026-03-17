package com.apicultor.apicutor.controller;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<Rainha> getAll() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return List.of();
        if (isAdmin(usuario)) return rainhaRepository.findAll();
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        if (isFuncionario) {
            List<Rainha> result = new ArrayList<>();
            if (usuario.getApiariosVinculados() != null) {
                for (Apiario a : usuario.getApiariosVinculados()) {
                    result.addAll(rainhaRepository.findByColmeia_Apiario(a));
                }
            }
            return result;
        }
        return rainhaRepository.findByColmeia_Apiario_Proprietario(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rainha> getById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Rainha> opt = rainhaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Rainha rainha = opt.get();
        Apiario apiario = rainha.getColmeia() != null ? rainha.getColmeia().getApiario() : null;
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) return ResponseEntity.ok(rainha);
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/colmeia/{colmeiaId}")
    public ResponseEntity<Rainha> getByColmeia(@PathVariable Long colmeiaId) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(colmeiaId);
        if (colmeiaOpt.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(colmeia.getRainha());
    }

    @PostMapping
    public ResponseEntity<Rainha> create(@RequestBody RainhaRequest request) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (request.getColmeiaId() == null) return ResponseEntity.badRequest().build();
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(request.getColmeiaId());
        if (colmeiaOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Colmeia colmeia = colmeiaOpt.get();
        Apiario apiario = colmeia.getApiario();
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();

        Rainha rainha = colmeia.getRainha() != null ? colmeia.getRainha() : new Rainha();
        applyRequest(rainha, request);
        rainha = rainhaRepository.save(rainha);

        colmeia.setRainha(rainha);
        rainha.setColmeia(colmeia);
        colmeiaRepository.save(colmeia);

        return ResponseEntity.ok(rainha);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rainha> update(@PathVariable Long id, @RequestBody RainhaRequest request) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Rainha> opt = rainhaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Rainha rainha = opt.get();
        Apiario apiario = rainha.getColmeia() != null ? rainha.getColmeia().getApiario() : null;
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) return ResponseEntity.status(403).build();

        applyRequest(rainha, request);
        rainha = rainhaRepository.save(rainha);

        if (request.getColmeiaId() != null) {
            Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(request.getColmeiaId());
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

        return ResponseEntity.ok(rainha);
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

    private void applyRequest(Rainha rainha, RainhaRequest request) {
        if (request.getMarcacao() != null) rainha.setMarcacao(request.getMarcacao());
        if (request.getRaca() != null) rainha.setRaca(request.getRaca());
        if (request.getDataNascimento() != null) rainha.setDataNascimento(request.getDataNascimento());
        if (request.getDataIntroducao() != null) rainha.setDataIntroducao(request.getDataIntroducao());
        if (request.getOrigem() != null) rainha.setOrigem(request.getOrigem());
        if (request.getObservacoes() != null) rainha.setObservacoes(request.getObservacoes());
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

    public static class RainhaRequest {
        private Long colmeiaId;
        private String marcacao;
        private String raca;
        private LocalDate dataNascimento;
        private LocalDate dataIntroducao;
        private Rainha.Origem origem;
        private String observacoes;

        public Long getColmeiaId() { return colmeiaId; }
        public void setColmeiaId(Long colmeiaId) { this.colmeiaId = colmeiaId; }
        public String getMarcacao() { return marcacao; }
        public void setMarcacao(String marcacao) { this.marcacao = marcacao; }
        public String getRaca() { return raca; }
        public void setRaca(String raca) { this.raca = raca; }
        public LocalDate getDataNascimento() { return dataNascimento; }
        public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
        public LocalDate getDataIntroducao() { return dataIntroducao; }
        public void setDataIntroducao(LocalDate dataIntroducao) { this.dataIntroducao = dataIntroducao; }
        public Rainha.Origem getOrigem() { return origem; }
        public void setOrigem(Rainha.Origem origem) { this.origem = origem; }
        public String getObservacoes() { return observacoes; }
        public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    }

}
