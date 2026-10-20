package com.tijetravel.backend.service;

import com.tijetravel.backend.dto.CrearReservaDTO;
import com.tijetravel.backend.dto.ReservaDTO;
import com.tijetravel.backend.exception.ConflictException;
import com.tijetravel.backend.exception.ForbiddenException;
import com.tijetravel.backend.exception.ValidationException;
import com.tijetravel.backend.model.*;
import com.tijetravel.backend.repository.*;
import com.tijetravel.backend.util.NormalizadorTexto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PreciosYReglasNegocioTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private TuristaRepository turistaRepository;

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HotelService hotelService;

    @InjectMocks
    private ReservaService reservaService;

    private Turista turistaTitular;
    private Turista turistaFamiliar;
    private Turista turistaAjeno;
    private Usuario usuarioCliente;
    private Vuelo vuelo;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        Sucursal sucursal = new Sucursal(1L, "SUC-01", "Buenos Aires", "Corrientes 100", "011-1111");

        turistaTitular = new Turista(1L, "TUR-01", "30111222", "Juan", "Pérez", "Calle 1", "juan@test.com", null, "11223344", sucursal, null);
        turistaFamiliar = new Turista(2L, "TUR-02", "30222333", "María", "Pérez", "Calle 1", "maria@test.com", null, "11223355", sucursal, turistaTitular);
        turistaAjeno = new Turista(3L, "TUR-03", "40111222", "Carlos", "Gómez", "Calle 2", "carlos@test.com", null, "11223366", sucursal, null);

        usuarioCliente = new Usuario(1L, "USR-01", "juanperez", "pass", Rol.CLIENTE, "30111222");

        vuelo = new Vuelo(1L, "TJ-100", LocalDateTime.of(2026, 11, 1, 8, 0), "Buenos Aires", "Córdoba", 100, 80, 20, new BigDecimal("160000.00"));
        hotel = new Hotel(1L, "HOT-01", "Hotel Córdoba Centro", "Córdoba", "Colón 100", "0351-1111", 50, new BigDecimal("30000.00"));
    }

    @Test
    void normalizadorTexto_comparaCiudadesIgnorandoTildesYMayusculas() {
        assertTrue(NormalizadorTexto.sonIguales("Córdoba", "cordoba"));
        assertTrue(NormalizadorTexto.sonIguales(" San Carlos de Bariloche ", "san carlos DE BARILOCHE"));
        assertTrue(NormalizadorTexto.sonIguales("Buenos Aires", "buenos aires"));
        assertFalse(NormalizadorTexto.sonIguales("Córdoba", "Salta"));
    }

    @Test
    void crearReserva_calculaPreciosEstandarYTuristaCorrectamente() {
        CrearReservaDTO dto = new CrearReservaDTO();
        dto.setTuristaDni("30111222");
        dto.setVueloNumero("TJ-100");
        dto.setHotelCodigo("HOT-01");
        dto.setClaseVuelo(ClaseVuelo.TURISTA);
        dto.setTipoHospedaje(TipoHospedaje.ESTANDAR);
        dto.setFechaLlegada(LocalDate.of(2026, 11, 1));
        dto.setFechaPartida(LocalDate.of(2026, 11, 4)); // 3 noches

        when(usuarioRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuarioCliente));
        when(turistaRepository.findByDni("30111222")).thenReturn(Optional.of(turistaTitular));
        when(vueloRepository.findByNumero("TJ-100")).thenReturn(Optional.of(vuelo));
        when(hotelRepository.findByCodigo("HOT-01")).thenReturn(Optional.of(hotel));
        when(reservaRepository.existsByTuristaIdAndVueloId(1L, 1L)).thenReturn(false);
        when(reservaRepository.countReservasPorVueloYClase(1L, ClaseVuelo.TURISTA)).thenReturn(5L);
        when(hotelService.hayDisponibilidad(1L, dto.getFechaLlegada(), dto.getFechaPartida(), 1)).thenReturn(true);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> {
            Reserva r = i.getArgument(0);
            r.setId(10L);
            return r;
        });

        ReservaDTO resultado = reservaService.crearReserva(dto, "juanperez");

        assertNotNull(resultado);
        // Vuelo clase TURISTA: $160.000
        assertEquals(new BigDecimal("160000.00"), resultado.getPrecioVueloCapturado());
        // Hotel noche ESTANDAR: $30.000
        assertEquals(new BigDecimal("30000.00"), resultado.getPrecioHotelCapturado());
        assertEquals(3L, resultado.getNoches());
        // Total = $160.000 + (3 * $30.000) = $160.000 + $90.000 = $250.000
        assertEquals(new BigDecimal("250000.00"), resultado.getPrecioTotal());
        assertEquals(EstadoReserva.PENDIENTE, resultado.getEstado());
    }

    @Test
    void crearReserva_calculaRecargosPrimeraClaseYPensionCompleta() {
        CrearReservaDTO dto = new CrearReservaDTO();
        dto.setTuristaDni("30111222");
        dto.setVueloNumero("TJ-100");
        dto.setHotelCodigo("HOT-01");
        dto.setClaseVuelo(ClaseVuelo.PRIMERA); // +10%
        dto.setTipoHospedaje(TipoHospedaje.COMPLETA); // +5%
        dto.setFechaLlegada(LocalDate.of(2026, 11, 1));
        dto.setFechaPartida(LocalDate.of(2026, 11, 3)); // 2 noches

        when(usuarioRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuarioCliente));
        when(turistaRepository.findByDni("30111222")).thenReturn(Optional.of(turistaTitular));
        when(vueloRepository.findByNumero("TJ-100")).thenReturn(Optional.of(vuelo));
        when(hotelRepository.findByCodigo("HOT-01")).thenReturn(Optional.of(hotel));
        when(reservaRepository.existsByTuristaIdAndVueloId(1L, 1L)).thenReturn(false);
        when(reservaRepository.countReservasPorVueloYClase(1L, ClaseVuelo.PRIMERA)).thenReturn(2L);
        when(hotelService.hayDisponibilidad(1L, dto.getFechaLlegada(), dto.getFechaPartida(), 1)).thenReturn(true);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> {
            Reserva r = i.getArgument(0);
            r.setId(11L);
            return r;
        });

        ReservaDTO resultado = reservaService.crearReserva(dto, "juanperez");

        // Vuelo base $160.000 + 10% = $176.000
        assertEquals(new BigDecimal("176000.00"), resultado.getPrecioVueloCapturado());
        // Hotel base $30.000 + 5% = $31.500
        assertEquals(new BigDecimal("31500.00"), resultado.getPrecioHotelCapturado());
        assertEquals(2L, resultado.getNoches());
        // Total = $176.000 + (2 * $31.500) = $176.000 + $63.000 = $239.000
        assertEquals(new BigDecimal("239000.00"), resultado.getPrecioTotal());
    }

    @Test
    void crearReserva_rechazaReservaDuplicadaDelMismoTuristaParaElMismoVuelo() {
        CrearReservaDTO dto = new CrearReservaDTO();
        dto.setTuristaDni("30111222");
        dto.setVueloNumero("TJ-100");
        dto.setHotelCodigo("HOT-01");
        dto.setClaseVuelo(ClaseVuelo.TURISTA);
        dto.setTipoHospedaje(TipoHospedaje.ESTANDAR);
        dto.setFechaLlegada(LocalDate.of(2026, 11, 1));
        dto.setFechaPartida(LocalDate.of(2026, 11, 4));

        when(usuarioRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuarioCliente));
        when(turistaRepository.findByDni("30111222")).thenReturn(Optional.of(turistaTitular));
        when(vueloRepository.findByNumero("TJ-100")).thenReturn(Optional.of(vuelo));
        when(hotelRepository.findByCodigo("HOT-01")).thenReturn(Optional.of(hotel));
        // Ya existe reserva para este turista y este vuelo
        when(reservaRepository.existsByTuristaIdAndVueloId(1L, 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> reservaService.crearReserva(dto, "juanperez"));
    }

    @Test
    void crearReserva_rechazaSiClienteIntentaReservarParaTuristaAjeno() {
        CrearReservaDTO dto = new CrearReservaDTO();
        dto.setTuristaDni("40111222"); // DNI ajeno
        dto.setVueloNumero("TJ-100");
        dto.setHotelCodigo("HOT-01");
        dto.setClaseVuelo(ClaseVuelo.TURISTA);
        dto.setTipoHospedaje(TipoHospedaje.ESTANDAR);
        dto.setFechaLlegada(LocalDate.of(2026, 11, 1));
        dto.setFechaPartida(LocalDate.of(2026, 11, 4));

        when(usuarioRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuarioCliente));
        when(turistaRepository.findByDni("40111222")).thenReturn(Optional.of(turistaAjeno));
        when(turistaRepository.findByDni("30111222")).thenReturn(Optional.of(turistaTitular));

        assertThrows(ForbiddenException.class, () -> reservaService.crearReserva(dto, "juanperez"));
    }

    @Test
    void crearReserva_permiteReservarParaFamiliarDelGrupo() {
        CrearReservaDTO dto = new CrearReservaDTO();
        dto.setTuristaDni("30222333"); // DNI del familiar
        dto.setVueloNumero("TJ-100");
        dto.setHotelCodigo("HOT-01");
        dto.setClaseVuelo(ClaseVuelo.TURISTA);
        dto.setTipoHospedaje(TipoHospedaje.ESTANDAR);
        dto.setFechaLlegada(LocalDate.of(2026, 11, 1));
        dto.setFechaPartida(LocalDate.of(2026, 11, 4));

        when(usuarioRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuarioCliente));
        when(turistaRepository.findByDni("30222333")).thenReturn(Optional.of(turistaFamiliar));
        when(turistaRepository.findByDni("30111222")).thenReturn(Optional.of(turistaTitular));
        when(vueloRepository.findByNumero("TJ-100")).thenReturn(Optional.of(vuelo));
        when(hotelRepository.findByCodigo("HOT-01")).thenReturn(Optional.of(hotel));
        when(reservaRepository.existsByTuristaIdAndVueloId(2L, 1L)).thenReturn(false);
        when(reservaRepository.countReservasPorVueloYClase(1L, ClaseVuelo.TURISTA)).thenReturn(10L);
        when(hotelService.hayDisponibilidad(1L, dto.getFechaLlegada(), dto.getFechaPartida(), 1)).thenReturn(true);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));

        ReservaDTO resultado = reservaService.crearReserva(dto, "juanperez");
        assertNotNull(resultado);
        assertEquals("30222333", resultado.getTuristaDni());
    }

    @Test
    void pagoSimulado_esIdempotente() {
        Reserva reserva = new Reserva();
        reserva.setId(5L);
        reserva.setTurista(turistaTitular);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(new BigDecimal("250000.00"));

        when(usuarioRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuarioCliente));
        when(reservaRepository.findById(5L)).thenReturn(Optional.of(reserva));
        when(turistaRepository.findByDni("30111222")).thenReturn(Optional.of(turistaTitular));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));

        // Primer pago
        ReservaDTO pagada1 = reservaService.procesarPago(5L, "juanperez");
        assertEquals(EstadoReserva.PAGADO, pagada1.getEstado());

        // Segundo pago (idempotente: no debe fallar ni alterar nada)
        ReservaDTO pagada2 = reservaService.procesarPago(5L, "juanperez");
        assertEquals(EstadoReserva.PAGADO, pagada2.getEstado());
        assertEquals(new BigDecimal("250000.00"), pagada2.getPrecioTotal());
    }

    @Test
    void eliminarOModificar_rechazaSiLaReservaYaEstaPagada() {
        Reserva reserva = new Reserva();
        reserva.setId(5L);
        reserva.setTurista(turistaTitular);
        reserva.setEstado(EstadoReserva.PAGADO);

        Usuario usuarioVendedor = new Usuario(2L, "USR-VEND", "vendedor", "pass", Rol.VENDEDOR, "20999888");

        when(usuarioRepository.findByUsername("vendedor")).thenReturn(Optional.of(usuarioVendedor));
        when(reservaRepository.findById(5L)).thenReturn(Optional.of(reserva));

        // Intentar eliminar pagada
        assertThrows(ConflictException.class, () -> reservaService.eliminarReservaPendiente(5L, "vendedor"));

        // Intentar modificar pagada
        CrearReservaDTO dtoMod = new CrearReservaDTO();
        assertThrows(ConflictException.class, () -> reservaService.modificarReservaPendiente(5L, dtoMod, "vendedor"));
    }
}

