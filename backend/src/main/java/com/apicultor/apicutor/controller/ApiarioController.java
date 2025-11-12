package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List; 
import java.util.Optional;

@RestController
@RequestMapping("/api/apiarios")
public class ApiarioController {

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Apiario> getAllApiarios() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        return apiarioRepository.findByProprietario(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Apiario> getApiarioById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Apiario> apiario = apiarioRepository.findById(id);
        if (apiario.isPresent() && apiario.get().getProprietario().getId().equals(usuario.getId())) {
            return ResponseEntity.ok(apiario.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Apiario createApiario(@RequestBody Apiario apiario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        apiario.setProprietario(usuario);
        return apiarioRepository.save(apiario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Apiario> updateApiario(@PathVariable Long id, @RequestBody Apiario apiarioDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        
        Optional<Apiario> apiarioOptional = apiarioRepository.findById(id);
        if (apiarioOptional.isPresent() && apiarioOptional.get().getProprietario().getId().equals(usuario.getId())) {
            Apiario apiario = apiarioOptional.get();
            apiario.setNome(apiarioDetails.getNome());
            apiario.setLocalizacao(apiarioDetails.getLocalizacao());
            apiario.setLatitude(apiarioDetails.getLatitude());
            apiario.setLongitude(apiarioDetails.getLongitude());
            apiario.setDescricao(apiarioDetails.getDescricao());
            
            return ResponseEntity.ok(apiarioRepository.save(apiario));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteApiario(@PathVariable Long id) {
        Optional<Apiario> apiarioOptional = apiarioRepository.findById(id);
        if (apiarioOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        apiarioRepository.delete(apiarioOptional.get());
        return ResponseEntity.ok().build();
    }
}