package com.tijetravel.frontend.service;

import com.tijetravel.frontend.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class BackendApiClient {

    private final RestClient restClient;
    private final String backendUrl;

    public BackendApiClient(@Value("${backend.api.url:http://localhost:8080}") String backendUrl) {
        this.backendUrl = backendUrl;
        this.restClient = RestClient.builder()
                .baseUrl(backendUrl)
                .build();
    }

    private String crearHeaderBasicAuth(String username, String password) {
        String credenciales = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credenciales.getBytes(StandardCharsets.UTF_8));
    }

    // ==========================================
    // CATALOGO PUBLICO Y BUSQUEDA (VISITANTES)
    // ==========================================

    public BusquedaResponseDTO buscarViajes(BusquedaForm form) {
        return restClient.post()
                .uri("/api/v1/public/buscar")
                .contentType(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(BusquedaResponseDTO.class);
    }

    public List<String> obtenerOrigenes() {
        return restClient.get()
                .uri("/api/v1/public/ciudades-origen")
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    public List<String> obtenerDestinos() {
        return restClient.get()
                .uri("/api/v1/public/ciudades-destino")
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    public List<String> obtenerCiudadesHoteles() {
        return restClient.get()
                .uri("/api/v1/public/ciudades-hoteles")
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    public List<String> obtenerNombresHoteles() {
        return restClient.get()
                .uri("/api/v1/public/nombres-hoteles")
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    public List<String> obtenerCiudadesSucursales() {
        return restClient.get()
                .uri("/api/v1/public/ciudades-sucursales")
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    public List<SucursalDTO> obtenerSucursales() {
        return restClient.get()
                .uri("/api/v1/public/sucursales")
                .retrieve()
                .body(new ParameterizedTypeReference<List<SucursalDTO>>() {});
    }

    // ==========================================
    // AUTENTICACION
    // ==========================================

    public AuthUserSession login(String username, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        Map<String, Object> resp = restClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (resp != null) {
            String rol = (String) resp.get("rol");
            String dni = (String) resp.get("dni");
            String nombreCompleto = (String) resp.get("nombreCompleto");
            return new AuthUserSession(username, password, rol, dni, nombreCompleto);
        }
        return null;
    }

    public void registrarCliente(Map<String, Object> datosRegistro) {
        restClient.post()
                .uri("/api/v1/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .body(datosRegistro)
                .retrieve()
                .toBodilessEntity();
    }

    // ==========================================
    // TURISTAS Y GRUPO FAMILIAR
    // ==========================================

    public List<TuristaDTO> obtenerGrupoFamiliar(String dni, AuthUserSession session) {
        return restClient.get()
                .uri("/api/v1/turistas/grupo-familiar/{dni}", dni)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(new ParameterizedTypeReference<List<TuristaDTO>>() {});
    }

    public TuristaDTO obtenerTuristaPorDni(String dni, AuthUserSession session) {
        return restClient.get()
                .uri("/api/v1/turistas/dni/{dni}", dni)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(TuristaDTO.class);
    }

    public TuristaDTO crearTurista(TuristaDTO dto, AuthUserSession session) {
        return restClient.post()
                .uri("/api/v1/turistas")
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(TuristaDTO.class);
    }

    public TuristaDTO actualizarTurista(Long id, TuristaDTO dto, AuthUserSession session) {
        return restClient.put()
                .uri("/api/v1/turistas/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(TuristaDTO.class);
    }

    public void eliminarTurista(Long id, AuthUserSession session) {
        restClient.delete()
                .uri("/api/v1/turistas/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .toBodilessEntity();
    }

    public Map<String, Object> listarTuristas(String codigo, String dni, String nombre, int page, int size, AuthUserSession session) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/turistas")
                        .queryParam("codigo", codigo)
                        .queryParam("dni", dni)
                        .queryParam("nombre", nombre)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // ==========================================
    // RESERVAS Y PAGOS
    // ==========================================

    public ReservaDTO crearReserva(Map<String, Object> req, AuthUserSession session) {
        return restClient.post()
                .uri("/api/v1/reservas")
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(ReservaDTO.class);
    }

    public ReservaDTO obtenerReservaPorId(Long id, AuthUserSession session) {
        return restClient.get()
                .uri("/api/v1/reservas/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(ReservaDTO.class);
    }

    public ReservaDTO procesarPagoReserva(Long id, AuthUserSession session) {
        return restClient.post()
                .uri("/api/v1/reservas/{id}/pago", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(ReservaDTO.class);
    }

    public Map<String, Object> listarReservas(String codigoTurista, String dni, String numeroVuelo, String codigoHotel,
                                             LocalDate fechaDesde, LocalDate fechaHasta, String estado,
                                             int page, int size, AuthUserSession session) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/reservas")
                        .queryParam("codigoTurista", codigoTurista)
                        .queryParam("dni", dni)
                        .queryParam("numeroVuelo", numeroVuelo)
                        .queryParam("codigoHotel", codigoHotel)
                        .queryParam("fechaDesde", fechaDesde)
                        .queryParam("fechaHasta", fechaHasta)
                        .queryParam("estado", estado)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public void eliminarReservaPendiente(Long id, AuthUserSession session) {
        restClient.delete()
                .uri("/api/v1/reservas/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .toBodilessEntity();
    }

    public ReservaDTO modificarReservaPendiente(Long id, Map<String, Object> req, AuthUserSession session) {
        return restClient.put()
                .uri("/api/v1/reservas/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(ReservaDTO.class);
    }

    // ==========================================
    // CATALOGO PARA VENDEDOR / ADMIN
    // ==========================================

    public Map<String, Object> buscarVuelos(String origen, String destino, int page, int size, String orden, AuthUserSession session) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/vuelos")
                        .queryParam("origen", origen)
                        .queryParam("destino", destino)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("orden", orden)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public VueloDTO crearVuelo(VueloDTO dto, AuthUserSession session) {
        return restClient.post()
                .uri("/api/v1/vuelos")
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(VueloDTO.class);
    }

    public VueloDTO actualizarVuelo(Long id, VueloDTO dto, AuthUserSession session) {
        return restClient.put()
                .uri("/api/v1/vuelos/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(VueloDTO.class);
    }

    public void eliminarVuelo(Long id, AuthUserSession session) {
        restClient.delete()
                .uri("/api/v1/vuelos/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .toBodilessEntity();
    }

    public Map<String, Object> buscarHoteles(String ciudad, String nombre, int page, int size, String orden, AuthUserSession session) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/hoteles")
                        .queryParam("ciudad", ciudad)
                        .queryParam("nombre", nombre)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("orden", orden)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public HotelDTO crearHotel(HotelDTO dto, AuthUserSession session) {
        return restClient.post()
                .uri("/api/v1/hoteles")
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(HotelDTO.class);
    }

    public HotelDTO actualizarHotel(Long id, HotelDTO dto, AuthUserSession session) {
        return restClient.put()
                .uri("/api/v1/hoteles/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(HotelDTO.class);
    }

    public void eliminarHotel(Long id, AuthUserSession session) {
        restClient.delete()
                .uri("/api/v1/hoteles/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .toBodilessEntity();
    }

    public SucursalDTO crearSucursal(SucursalDTO dto, AuthUserSession session) {
        return restClient.post()
                .uri("/api/v1/sucursales")
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(SucursalDTO.class);
    }

    public SucursalDTO actualizarSucursal(Long id, SucursalDTO dto, AuthUserSession session) {
        return restClient.put()
                .uri("/api/v1/sucursales/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(SucursalDTO.class);
    }

    public void eliminarSucursal(Long id, AuthUserSession session) {
        restClient.delete()
                .uri("/api/v1/sucursales/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .toBodilessEntity();
    }

    // ==========================================
    // ADMINISTRACION DE USUARIOS Y BALANCE
    // ==========================================

    public Map<String, Object> listarUsuarios(String codigo, String dni, String username, String rol, int page, int size, AuthUserSession session) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/usuarios")
                        .queryParam("codigo", codigo)
                        .queryParam("dni", dni)
                        .queryParam("username", username)
                        .queryParam("rol", rol)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public void crearUsuarioAdmin(Map<String, Object> req, AuthUserSession session) {
        restClient.post()
                .uri("/api/v1/usuarios")
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .toBodilessEntity();
    }

    public void cambiarRolUsuario(Long id, String rol, AuthUserSession session) {
        restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/usuarios/{id}/rol")
                        .queryParam("rol", rol)
                        .build(id))
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .toBodilessEntity();
    }

    public BalanceDTO obtenerBalance(AuthUserSession session) {
        return restClient.get()
                .uri("/api/v1/balance")
                .header(HttpHeaders.AUTHORIZATION, crearHeaderBasicAuth(session.getUsername(), session.getPassword()))
                .retrieve()
                .body(BalanceDTO.class);
    }
}

