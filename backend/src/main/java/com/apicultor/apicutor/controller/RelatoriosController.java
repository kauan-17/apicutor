package com.apicultor.apicutor.controller;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Producao;
import com.apicultor.apicutor.model.Usuario;
import com.apicultor.apicutor.repository.ApiarioRepository;
import com.apicultor.apicutor.repository.ProducaoRepository;
import com.apicultor.apicutor.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class RelatoriosController {

    @Autowired
    private ProducaoRepository producaoRepository;

    @Autowired
    private ApiarioRepository apiarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/producao/mensal/{apiarioId}/{ano}")
    public ResponseEntity<Map<Month, Double>> producaoMensal(@PathVariable Long apiarioId, @PathVariable int ano) {
        Usuario usuario = getUsuarioAtual();
        Apiario apiario = apiarioRepository.findById(apiarioId).orElse(null);
        if (apiario == null) return ResponseEntity.notFound().build();
        if (!isAdmin(usuario) && !canAccessApiario(usuario, apiario)) {
            return ResponseEntity.status(403).build();
        }
        LocalDate inicio = LocalDate.of(ano, 1, 1);
        LocalDate fim = LocalDate.of(ano, 12, 31);
        List<Producao> producoes = producaoRepository.findByColmeia_ApiarioAndDataColheitaBetween(apiario, inicio, fim);
        Map<Month, Double> acumulado = new EnumMap<>(Month.class);
        for (Month m : Month.values()) acumulado.put(m, 0.0);
        for (Producao p : producoes) {
            if (p.getDataColheita() != null && p.getQuantidade() != null) {
                Month m = p.getDataColheita().getMonth();
                acumulado.put(m, acumulado.get(m) + p.getQuantidade());
            }
        }
        return ResponseEntity.ok(acumulado);
    }

    private Usuario getUsuarioAtual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return usuarioRepository.findByUsername(username).orElseThrow();
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

