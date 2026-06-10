package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.dto.TratamentoInputDTO;
import com.apicultor.apicutor.vo.TratamentoVO;
import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Tratamento;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.TratamentoRepository;
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
@RequestMapping("/api/tratamentos")
public class TratamentoController {

    @Autowired
    private TratamentoRepository tratamentoRepository;

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<TratamentoVO>> getAll() {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();

        List<Tratamento> tratamentos;
        if (isAdmin(usuario)) {
            tratamentos = tratamentoRepository.findAll();
        } else if (isFuncionario(usuario)) {
            List<Tratamento> result = new ArrayList<>();
            if (usuario.getApiariosVinculados() != null) {
                for (Apiario a : usuario.getApiariosVinculados()) {
                    result.addAll(tratamentoRepository.findByColmeia_Apiario(a));
                }
            }
            tratamentos = result;
        } else {
            tratamentos = tratamentoRepository.findByColmeia_Apiario_Proprietario(usuario);
        }

        return ResponseEntity.ok(tratamentos.stream().map(TratamentoVO::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TratamentoVO> getById(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Tratamento> opt = tratamentoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Tratamento t = opt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, t.getColmeia() != null ? t.getColmeia().getApiario() : null))
            return ResponseEntity.ok(new TratamentoVO(t));
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/colmeia/{colmeiaId}")
    public ResponseEntity<List<TratamentoVO>> getByColmeia(@PathVariable Long colmeiaId) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(colmeiaId);
        if (colmeiaOpt.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOpt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, colmeia.getApiario())) {
            List<Tratamento> lista = tratamentoRepository.findByColmeia(colmeia);
            return ResponseEntity.ok(lista.stream().map(TratamentoVO::new).collect(Collectors.toList()));
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<TratamentoVO> create(@RequestBody TratamentoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        if (input == null || input.getColmeiaId() == null) return ResponseEntity.badRequest().build();

        Optional<Colmeia> colmeiaOpt = colmeiaRepository.findById(input.getColmeiaId());
        if (colmeiaOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Colmeia colmeia = colmeiaOpt.get();

        if (isAdmin(usuario) || canAccessApiario(usuario, colmeia.getApiario())) {
            Tratamento tratamento = new Tratamento();
            tratamento.setColmeia(colmeia);
            tratamento.setResponsavel(usuario);
            applyInput(tratamento, input);
            tratamento = tratamentoRepository.save(tratamento);
            return ResponseEntity.ok(new TratamentoVO(tratamento));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TratamentoVO> update(@PathVariable Long id, @RequestBody TratamentoInputDTO input) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Tratamento> opt = tratamentoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Tratamento tratamento = opt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, tratamento.getColmeia() != null ? tratamento.getColmeia().getApiario() : null)) {
            applyInput(tratamento, input);
            tratamento = tratamentoRepository.save(tratamento);
            return ResponseEntity.ok(new TratamentoVO(tratamento));
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario usuario = getUsuarioAtual();
        if (usuario == null) return ResponseEntity.status(401).build();
        Optional<Tratamento> opt = tratamentoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Tratamento t = opt.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, t.getColmeia() != null ? t.getColmeia().getApiario() : null)) {
            tratamentoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    private void applyInput(Tratamento t, TratamentoInputDTO input) {
        t.setDataAplicacao(input.getDataAplicacao());
        t.setTipoTratamento(input.getTipoTratamento());
        t.setProduto(input.getProduto());
        t.setDose(input.getDose());
        t.setUnidadeDose(input.getUnidadeDose());
        t.setObservacoes(input.getObservacoes());
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
