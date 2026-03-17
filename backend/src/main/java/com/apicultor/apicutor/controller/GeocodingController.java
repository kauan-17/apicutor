package com.apicultor.apicutor.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/geocoding")
public class GeocodingController {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GeocodingController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    @GetMapping("/cep/{cep}")
    public ResponseEntity<?> geocodeCep(@PathVariable String cep) {
        String cleanCep = cep == null ? "" : cep.replaceAll("\\D", "");
        if (cleanCep.length() != 8) {
            return ResponseEntity.badRequest().body(Map.of("error", "CEP inválido"));
        }

        try {
            ViaCepData viaCep = fetchViaCep(cleanCep);
            if (viaCep == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", "Falha ao consultar CEP"));
            }
            if (Boolean.TRUE.equals(viaCep.erro)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "CEP não encontrado"));
            }

            String query = buildQuery(viaCep);
            GeocodeResult result = fetchNominatim(query);
            if (result == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Coordenadas não encontradas"));
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("cep", cleanCep);
            body.put("endereco", query);
            body.put("latitude", result.latitude);
            body.put("longitude", result.longitude);
            body.put("displayName", result.displayName);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro ao buscar coordenadas"));
        }
    }

    private ViaCepData fetchViaCep(String cleanCep) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://viacep.com.br/ws/" + cleanCep + "/json/"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) return null;
        JsonNode node = objectMapper.readTree(response.body());
        ViaCepData data = new ViaCepData();
        data.logradouro = text(node, "logradouro");
        data.bairro = text(node, "bairro");
        data.localidade = text(node, "localidade");
        data.uf = text(node, "uf");
        data.erro = node.has("erro") && node.get("erro").asBoolean(false);
        return data;
    }

    private GeocodeResult fetchNominatim(String query) throws Exception {
        String url = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" +
                URLEncoder.encode(query, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(12))
                .header("Accept", "application/json")
                .header("User-Agent", "apicutor/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) return null;
        JsonNode arr = objectMapper.readTree(response.body());
        if (!arr.isArray() || arr.isEmpty()) return null;
        JsonNode first = arr.get(0);
        String latStr = first.hasNonNull("lat") ? first.get("lat").asText() : null;
        String lonStr = first.hasNonNull("lon") ? first.get("lon").asText() : null;
        if (latStr == null || lonStr == null) return null;
        GeocodeResult result = new GeocodeResult();
        result.latitude = Double.parseDouble(latStr);
        result.longitude = Double.parseDouble(lonStr);
        result.displayName = first.hasNonNull("display_name") ? first.get("display_name").asText() : null;
        return result;
    }

    private String buildQuery(ViaCepData data) {
        StringBuilder sb = new StringBuilder();
        appendPart(sb, data.logradouro);
        appendPart(sb, data.bairro);
        appendPart(sb, data.localidade);
        appendPart(sb, data.uf);
        appendPart(sb, "Brasil");
        return sb.toString();
    }

    private void appendPart(StringBuilder sb, String value) {
        if (value == null) return;
        String v = value.trim();
        if (v.isEmpty()) return;
        if (!sb.isEmpty()) sb.append(", ");
        sb.append(v);
    }

    private String text(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : null;
    }

    private static class ViaCepData {
        String logradouro;
        String bairro;
        String localidade;
        String uf;
        Boolean erro;
    }

    private static class GeocodeResult {
        double latitude;
        double longitude;
        String displayName;
    }
}

