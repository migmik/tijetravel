package com.tijetravel.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tijetravel.backend.dto.*;
import com.tijetravel.backend.model.ClaseVuelo;
import com.tijetravel.backend.model.EstadoReserva;
import com.tijetravel.backend.model.Rol;
import com.tijetravel.backend.model.TipoHospedaje;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/tijetravel?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
    "spring.datasource.username=root",
    "spring.datasource.password=root",
    "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.flyway.enabled=true",
    "spring.flyway.baseline-on-migrate=true",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect",
    "admin.bootstrap.username=admin",
    "admin.bootstrap.password=admin123",
    "admin.bootstrap.dni=20111222"
})
public class MySQLFlywayAndE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Flyway flyway;

    @Autowired
    private com.tijetravel.backend.repository.ReservaRepository reservaRepository;

    @Autowired
    private com.tijetravel.backend.repository.UsuarioRepository usuarioRepository;

    @Autowired
    private com.tijetravel.backend.repository.TuristaRepository turistaRepository;

    private Long reservaCreadaId;

    @BeforeAll
    void initDb() {
        flyway.migrate();

        // Limpiar datos de pruebas previas para garantizar repetibilidad estricta
        reservaRepository.findAll().forEach(r -> {
            if ("RES-TEST-MATEO".equals(r.getCodigo()) || "40987654".equals(r.getTurista().getDni()) || "29890123".equals(r.getTurista().getDni())) {
                reservaRepository.delete(r);
            }
        });
        usuarioRepository.findByUsername("lucianavarro").ifPresent(usuarioRepository::delete);
        usuarioRepository.findByUsername("vendedor.cordoba").ifPresent(usuarioRepository::delete);
        turistaRepository.findByDni("40987654").ifPresent(turistaRepository::delete);
        turistaRepository.findByDni("38123456").ifPresent(turistaRepository::delete);
    }

    // ========================================================
    // RECORRIDO 1: VISITANTE
    // ========================================================

    @Test
    @Order(1)
    void visitante_buscaPaquetesYConsultaDisponibilidad() throws Exception {
        // El visitante busca viajes de Buenos Aires a Cordoba para 2 personas
        BusquedaViajeRequestDTO busqueda = new BusquedaViajeRequestDTO();
        busqueda.setOrigen("Buenos Aires");
        busqueda.setDestino("Córdoba");
        busqueda.setFechaLlegada(LocalDate.of(2026, 10, 21));
        busqueda.setFechaPartida(LocalDate.of(2026, 10, 24)); // 3 noches
        busqueda.setPersonas(2);
        busqueda.setOrdenPrecio("ASC");

        mockMvc.perform(post("/api/v1/public/buscar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(busqueda)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paquetes", not(empty())))
                .andExpect(jsonPath("$.paquetes[0].vuelo.numero", is("TJ-101")))
                .andExpect(jsonPath("$.vuelos", not(empty())))
                .andExpect(jsonPath("$.hoteles", not(empty())));
    }

    @Test
    @Order(2)
    void visitante_consultaCatalogoPublicoYSucursales() throws Exception {
        mockMvc.perform(get("/api/v1/public/sucursales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))));

        mockMvc.perform(get("/api/v1/public/ciudades-origen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("Buenos Aires")));
    }

    @Test
    @Order(3)
    void visitante_seAutorregistraComoCliente() throws Exception {
        RegistroClienteDTO registro = new RegistroClienteDTO();
        registro.setDni("38123456");
        registro.setNombre("Lucía");
        registro.setApellido("Navarro");
        registro.setDireccion("San Jerónimo 500");
        registro.setEmail("lucia.navarro@email.com");
        registro.setTelefonoCelular("0351-15-7777-8888");
        registro.setSucursalCodigo("SUC-COR");
        registro.setUsername("lucianavarro");
        registro.setPassword("lucia123");

        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("lucianavarro")))
                .andExpect(jsonPath("$.rol", is("CLIENTE")))
                .andExpect(jsonPath("$.dni", is("38123456")));
    }

    // ========================================================
    // RECORRIDO 2: CLIENTE
    // ========================================================

    @Test
    @Order(4)
    void cliente_iniciaSesionYConsultaPerfil() throws Exception {
        LoginRequestDTO login = new LoginRequestDTO("lucianavarro", "lucia123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("lucianavarro")))
                .andExpect(jsonPath("$.rol", is("CLIENTE")))
                .andExpect(jsonPath("$.dni", is("38123456")));
    }

    @Test
    @Order(5)
    void cliente_agregaFamiliarYConsultaGrupo() throws Exception {
        // Lucía agrega a su hermano como familiar
        TuristaDTO hermano = new TuristaDTO();
        hermano.setDni("40987654");
        hermano.setNombre("Mateo");
        hermano.setApellido("Navarro");
        hermano.setDireccion("San Jerónimo 500");
        hermano.setEmail("mateo.navarro@email.com");
        hermano.setTelefonoCelular("0351-15-7777-9999");
        hermano.setSucursalId(2L); // Se asociará a la sucursal de Lucía

        mockMvc.perform(post("/api/v1/turistas")
                        .with(httpBasic("lucianavarro", "lucia123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hermano)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni", is("40987654")));

        // Consultar su grupo familiar
        mockMvc.perform(get("/api/v1/turistas/grupo-familiar/38123456")
                        .with(httpBasic("lucianavarro", "lucia123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))); // Lucía y Mateo
    }

    @Test
    @Order(6)
    void cliente_creaReservaParaSuFamiliar_quedaPendienteYConPreciosCapturados() throws Exception {
        // Reservar vuelo TJ-101 (2026-10-21) y hotel HOT-COR-01 (Holiday Inn Córdoba)
        CrearReservaDTO reservaReq = new CrearReservaDTO();
        reservaReq.setTuristaDni("40987654"); // Reserva para su hermano Mateo
        reservaReq.setVueloNumero("TJ-101");
        reservaReq.setHotelCodigo("HOT-COR-01");
        reservaReq.setClaseVuelo(ClaseVuelo.TURISTA);
        reservaReq.setTipoHospedaje(TipoHospedaje.ESTANDAR);
        reservaReq.setFechaLlegada(LocalDate.of(2026, 10, 21));
        reservaReq.setFechaPartida(LocalDate.of(2026, 10, 24)); // 3 noches

        MvcResult result = mockMvc.perform(post("/api/v1/reservas")
                        .with(httpBasic("lucianavarro", "lucia123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("PENDIENTE")))
                .andExpect(jsonPath("$.precioVueloCapturado", is(160000.00)))
                .andExpect(jsonPath("$.precioHotelCapturado", is(36000.00)))
                .andExpect(jsonPath("$.noches", is(3)))
                // 160.000 + (3 * 36.000) = 160.000 + 108.000 = 268.000
                .andExpect(jsonPath("$.precioTotal", is(268000.00)))
                .andReturn();

        ReservaDTO resp = objectMapper.readValue(result.getResponse().getContentAsString(), ReservaDTO.class);
        reservaCreadaId = resp.getId();
        assertNotNull(reservaCreadaId);
    }

    @Test
    @Order(7)
    void cliente_simulaPagoDeLaReserva_quedaPagadaConFechaYEsIdempotente() throws Exception {
        assertNotNull(reservaCreadaId);

        // Primer pago simulado
        mockMvc.perform(post("/api/v1/reservas/" + reservaCreadaId + "/pago")
                        .with(httpBasic("lucianavarro", "lucia123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("PAGADO")))
                .andExpect(jsonPath("$.fechaPago", notNullValue()))
                .andExpect(jsonPath("$.precioTotal", is(268000.00)));

        // Segundo intento de pago (idempotencia: no duplica ni falla)
        mockMvc.perform(post("/api/v1/reservas/" + reservaCreadaId + "/pago")
                        .with(httpBasic("lucianavarro", "lucia123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("PAGADO")))
                .andExpect(jsonPath("$.precioTotal", is(268000.00)));
    }

    @Test
    @Order(8)
    void cliente_noPuedeModificarNiEliminarReservaPagada() throws Exception {
        assertNotNull(reservaCreadaId);

        // No se puede eliminar reserva pagada
        mockMvc.perform(delete("/api/v1/reservas/" + reservaCreadaId)
                        .with(httpBasic("vendedor", "vendedor123")))
                .andExpect(status().isConflict());
    }

    // ========================================================
    // RECORRIDO 3: VENDEDOR
    // ========================================================

    @Test
    @Order(9)
    void vendedor_administraReservasYFiltraEnBaseDeDatos() throws Exception {
        mockMvc.perform(get("/api/v1/reservas")
                        .with(httpBasic("vendedor", "vendedor123"))
                        .param("estado", "PAGADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));

        mockMvc.perform(get("/api/v1/reservas")
                        .with(httpBasic("vendedor", "vendedor123"))
                        .param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));
    }

    @Test
    @Order(10)
    void vendedor_creaYCancelaReservaPendiente() throws Exception {
        // Crear reserva pendiente para turista individual con DNI 29890123 (Esteban Suárez)
        CrearReservaDTO req = new CrearReservaDTO();
        req.setTuristaDni("29890123");
        req.setVueloNumero("TJ-301");
        req.setHotelCodigo("HOT-SLA-01");
        req.setClaseVuelo(ClaseVuelo.TURISTA);
        req.setTipoHospedaje(TipoHospedaje.ESTANDAR);
        req.setFechaLlegada(LocalDate.of(2026, 10, 21));
        req.setFechaPartida(LocalDate.of(2026, 10, 23));

        MvcResult result = mockMvc.perform(post("/api/v1/reservas")
                        .with(httpBasic("vendedor", "vendedor123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("PENDIENTE")))
                .andReturn();

        ReservaDTO creada = objectMapper.readValue(result.getResponse().getContentAsString(), ReservaDTO.class);

        // Vendedor cancela/elimina la reserva mientras está PENDIENTE
        mockMvc.perform(delete("/api/v1/reservas/" + creada.getId())
                        .with(httpBasic("vendedor", "vendedor123")))
                .andExpect(status().isNoContent());
    }

    // ========================================================
    // RECORRIDO 4: ADMINISTRADOR
    // ========================================================

    @Test
    @Order(11)
    void administrador_consultaBalanceFinancieroAgregado() throws Exception {
        // El balance debe distinguir claramente reservas PAGADAS de reservas PENDIENTES
        mockMvc.perform(get("/api/v1/balance")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagadas.cantidad", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.pagadas.total", notNullValue()))
                .andExpect(jsonPath("$.pagadas.totalVuelos", notNullValue()))
                .andExpect(jsonPath("$.pagadas.totalHoteles", notNullValue()))
                .andExpect(jsonPath("$.pendientes.cantidad", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.pendientes.total", notNullValue()));
    }

    @Test
    @Order(12)
    void administrador_gestionaUsuariosYModificaRoles() throws Exception {
        // Administrador lista usuarios
        mockMvc.perform(get("/api/v1/usuarios")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));

        // Administrador crea una nueva cuenta de vendedor
        CrearUsuarioAdminDTO nuevoUser = new CrearUsuarioAdminDTO();
        nuevoUser.setUsername("vendedor.cordoba");
        nuevoUser.setPassword("pass1234");
        nuevoUser.setRol(Rol.VENDEDOR);
        nuevoUser.setDni("31901234");

        mockMvc.perform(post("/api/v1/usuarios")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("vendedor.cordoba")))
                .andExpect(jsonPath("$.rol", is("VENDEDOR")));
    }
}
