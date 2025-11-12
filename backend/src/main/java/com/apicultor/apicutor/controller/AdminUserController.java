package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*; 

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UsuarioRepository usuarioRepository;

    private static final Set<String> ALLOWED_ROLES = Set.of("ROLE_USER", "ROLE_ADMIN");

    public AdminUserController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Map<String, Object>> listUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Usuario u : usuarios) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getId());
            item.put("username", u.getUsername());
            item.put("email", u.getEmail());
            item.put("roles", u.getRoles());
            item.put("ativo", u.isAtivo());
            result.add(item);
        }
        return result;
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<?> setRoles(@PathVariable Long id, @RequestBody Set<String> roles) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Valida roles permitidos
        for (String role : roles) {
            if (!ALLOWED_ROLES.contains(role)) {
                return ResponseEntity.badRequest().body("Role inválida: " + role);
            }
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setRoles(new HashSet<>(roles));
        usuarioRepository.save(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("username", usuario.getUsername());
        response.put("roles", usuario.getRoles());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/roles/add")
    public ResponseEntity<?> addRole(@PathVariable Long id, @RequestBody String role) {
        if (!ALLOWED_ROLES.contains(role)) {
            return ResponseEntity.badRequest().body("Role inválida: " + role);
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();
        usuario.getRoles().add(role);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(usuario.getRoles());
    }

    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<?> removeRole(@PathVariable Long id, @PathVariable String role) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();
        usuario.getRoles().remove(role);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuario.getRoles());
    }
}