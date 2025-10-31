package com.apicultor.apicutor.controller;

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
    public List<Colmeia> getAllColmeias() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        return colmeiaRepository.findByApiarioProprietario(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Colmeia> getColmeiaById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Colmeia> colmeia = colmeiaRepository.findById(id);
        if (colmeia.isPresent() && colmeia.get().getApiario().getProprietario().getId().equals(usuario.getId())) {
            return ResponseEntity.ok(colmeia.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Colmeia> createColmeia(@RequestBody Colmeia colmeia) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Apiario> apiario = apiarioRepository.findById(colmeia.getApiario().getId());
        if (apiario.isPresent() && apiario.get().getProprietario().getId().equals(usuario.getId())) {
            colmeia.setApiario(apiario.get());
            return ResponseEntity.ok(colmeiaRepository.save(colmeia));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Colmeia> updateColmeia(@PathVariable Long id, @RequestBody Colmeia colmeiaDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Colmeia> colmeiaOptional = colmeiaRepository.findById(id);
        if (colmeiaOptional.isPresent() && colmeiaOptional.get().getApiario().getProprietario().getId().equals(usuario.getId())) {
            Colmeia colmeia = colmeiaOptional.get();
            colmeia.setIdentificacao(colmeiaDetails.getIdentificacao());
            colmeia.setTipo(colmeiaDetails.getTipo());
            colmeia.setDataInstalacao(colmeiaDetails.getDataInstalacao());
            colmeia.setObservacoes(colmeiaDetails.getObservacoes());
            colmeia.setStatus(colmeiaDetails.getStatus());
            
            return ResponseEntity.ok(colmeiaRepository.save(colmeia));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColmeia(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Colmeia> colmeiaOptional = colmeiaRepository.findById(id);
        if (colmeiaOptional.isPresent() && colmeiaOptional.get().getApiario().getProprietario().getId().equals(usuario.getId())) {
            colmeiaRepository.delete(colmeiaOptional.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/apiario/{apiarioId}")
    public ResponseEntity<List<Colmeia>> getColmeiasByApiario(@PathVariable Long apiarioId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Apiario> apiario = apiarioRepository.findById(apiarioId);
        if (apiario.isPresent() && apiario.get().getProprietario().getId().equals(usuario.getId())) {
            List<Colmeia> colmeias = colmeiaRepository.findByApiario(apiario.get());
            return ResponseEntity.ok(colmeias);
        }
        return ResponseEntity.notFound().build();
    }
}