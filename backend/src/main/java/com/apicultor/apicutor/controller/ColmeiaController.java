package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.dto.ColmeiaInputDTO;
import com.apicultor.apicutor.vo.ColmeiaVO;
import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Colmeia;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.ColmeiaRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/colmeias")
public class ColmeiaController {

    @Autowired
    private ColmeiaRepository colmeiaRepository;

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<ColmeiaVO>> getAllColmeias() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        boolean isAdmin = hasRole(usuario, "ROLE_ADMIN") || hasRole(usuario, "ADMIN");
        boolean isFuncionario = hasRole(usuario, "ROLE_FUNCIONARIO") || hasRole(usuario, "FUNCIONARIO");
        List<Colmeia> colmeias;
        if (isAdmin) {
            colmeias = colmeiaRepository.findAll();
        } else if (isFuncionario) {
            // Colmeias dos apiários vinculados ao usuário
            java.util.List<Colmeia> result = new java.util.ArrayList<>();
            for (Apiario a : usuario.getApiariosVinculados()) {
                result.addAll(colmeiaRepository.findByApiario(a));
            }
            colmeias = result;
        } else {
            // Apicultor: colmeias dos apiários do proprietário
            colmeias = colmeiaRepository.findByApiarioProprietario(usuario);
        }
        return ResponseEntity.ok(colmeias.stream().map(ColmeiaVO::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColmeiaVO> getColmeiaById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Colmeia> colmeia = colmeiaRepository.findById(id);
        if (colmeia.isEmpty()) return ResponseEntity.notFound().build();
        Apiario apiario = colmeia.get().getApiario();
        if (isAdmin(usuario) || canAccessApiario(usuario, apiario)) {
            return ResponseEntity.ok(new ColmeiaVO(colmeia.get()));
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    public ResponseEntity<ColmeiaVO> createColmeia(@RequestBody ColmeiaInputDTO input) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        if (input == null || input.getApiarioId() == null) return ResponseEntity.badRequest().build();
        Optional<Apiario> apiario = apiarioRepository.findById(input.getApiarioId());
        if (apiario.isEmpty()) return ResponseEntity.badRequest().build();
        Apiario a = apiario.get();
        // Cadastro e ligação: permitido para ADMIN, APICULTOR (proprietário) e FUNCIONARIO vinculado
        if (isAdmin(usuario) || canAccessApiario(usuario, a)) {
            Colmeia colmeia = new Colmeia();
            applyInput(colmeia, input);
            colmeia.setApiario(a);
            colmeia = colmeiaRepository.save(colmeia);
            return ResponseEntity.ok(new ColmeiaVO(colmeia));
        }
        return ResponseEntity.status(403).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColmeiaVO> updateColmeia(@PathVariable Long id, @RequestBody ColmeiaInputDTO input) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Colmeia> colmeiaOptional = colmeiaRepository.findById(id);
        if (colmeiaOptional.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOptional.get();
        Apiario apiario = colmeia.getApiario();
        // Execução (atualizar): somente ADMIN ou APICULTOR proprietário
        if (isAdmin(usuario) || (apiario.getProprietario().getId().equals(usuario.getId()))) {
            applyInput(colmeia, input);
            colmeia = colmeiaRepository.save(colmeia);
            return ResponseEntity.ok(new ColmeiaVO(colmeia));
        }
        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColmeia(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Colmeia> colmeiaOptional = colmeiaRepository.findById(id);
        if (colmeiaOptional.isEmpty()) return ResponseEntity.notFound().build();
        Colmeia colmeia = colmeiaOptional.get();
        Apiario apiario = colmeia.getApiario();
        // Execução (exclusão): somente ADMIN ou APICULTOR proprietário
        if (isAdmin(usuario) || (apiario.getProprietario().getId().equals(usuario.getId()))) {
            colmeiaRepository.delete(colmeia);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }
    
    @GetMapping("/apiario/{apiarioId}")
    public ResponseEntity<List<ColmeiaVO>> getColmeiasByApiario(@PathVariable Long apiarioId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Apiario> apiario = apiarioRepository.findById(apiarioId);
        if (apiario.isEmpty()) return ResponseEntity.notFound().build();
        Apiario a = apiario.get();
        if (isAdmin(usuario) || canAccessApiario(usuario, a)) {
            List<Colmeia> colmeias = colmeiaRepository.findByApiario(a);
            return ResponseEntity.ok(colmeias.stream().map(ColmeiaVO::new).collect(Collectors.toList()));
        }
        return ResponseEntity.status(403).build();
    }

    // Helpers de autorização
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

    private void applyInput(Colmeia colmeia, ColmeiaInputDTO input) {
        if (input == null || colmeia == null) return;
        if (input.getIdentificacao() != null) colmeia.setIdentificacao(input.getIdentificacao());
        if (input.getTipo() != null) colmeia.setTipo(input.getTipo());
        colmeia.setDataInstalacao(input.getDataInstalacao());
        colmeia.setObservacoes(input.getObservacoes());
        if (input.getStatus() != null) colmeia.setStatus(input.getStatus());
        colmeia.setTipoAbelha(input.getTipoAbelha());
        colmeia.setRainhaStatus(input.getRainhaStatus());
        colmeia.setOrigemColonia(input.getOrigemColonia());

        Integer qtd = input.getQuantidadeMelgueiras();
        Boolean melgueira = input.getMelgueira();
        if (qtd != null && qtd > 0) {
            colmeia.setQuantidadeMelgueiras(qtd);
            colmeia.setMelgueira(true);
        } else if (Boolean.FALSE.equals(melgueira)) {
            colmeia.setMelgueira(false);
            colmeia.setQuantidadeMelgueiras(0);
        } else {
            colmeia.setMelgueira(melgueira);
            colmeia.setQuantidadeMelgueiras(qtd);
        }
    }
}
