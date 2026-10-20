package com.tijetravel.backend.service;

import com.tijetravel.backend.dto.CrearReservaDTO;
import com.tijetravel.backend.dto.ReservaDTO;
import com.tijetravel.backend.exception.ConflictException;
import com.tijetravel.backend.exception.ForbiddenException;
import com.tijetravel.backend.exception.ResourceNotFoundException;
import com.tijetravel.backend.exception.ValidationException;
import com.tijetravel.backend.model.*;
import com.tijetravel.backend.repository.*;
import com.tijetravel.backend.util.NormalizadorTexto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final TuristaRepository turistaRepository;
    private final VueloRepository vueloRepository;
    private final HotelRepository hotelRepository;
    private final UsuarioRepository usuarioRepository;
    private final HotelService hotelService;

    public ReservaService(ReservaRepository reservaRepository,
                          TuristaRepository turistaRepository,
                          VueloRepository vueloRepository,
                          HotelRepository hotelRepository,
                          UsuarioRepository usuarioRepository,
                          HotelService hotelService) {
        this.reservaRepository = reservaRepository;
        this.turistaRepository = turistaRepository;
        this.vueloRepository = vueloRepository;
        this.hotelRepository = hotelRepository;
        this.usuarioRepository = usuarioRepository;
        this.hotelService = hotelService;
    }

    @Transactional
    public ReservaDTO crearReserva(CrearReservaDTO dto, String usernameActual) {
        // 1. Obtener usuario actual y verificar permisos de reserva
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario actual no encontrado: " + usernameActual));

        Turista turista = turistaRepository.findByDni(dto.getTuristaDni().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Turista no encontrado con DNI: " + dto.getTuristaDni()));

        // Si es CLIENTE, solo puede reservar para sí mismo o para miembros de su grupo familiar
        if (usuarioActual.getRol() == Rol.CLIENTE) {
            validarPertenenciaGrupoFamiliar(usuarioActual.getDni(), turista);
        }

        // 2. Obtener vuelo y validar
        Vuelo vuelo = vueloRepository.findByNumero(dto.getVueloNumero().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con numero: " + dto.getVueloNumero()));

        // 3. Obtener hotel y validar
        Hotel hotel = hotelRepository.findByCodigo(dto.getHotelCodigo().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel no encontrado con codigo: " + dto.getHotelCodigo()));

        // 4. Validar coherencia de fechas y ciudades
        LocalDate llegada = dto.getFechaLlegada();
        LocalDate partida = dto.getFechaPartida();

        if (llegada == null || partida == null) {
            throw new ValidationException("Las fechas de llegada y partida son obligatorias");
        }
        if (!partida.isAfter(llegada)) {
            throw new ValidationException("La fecha de partida debe ser posterior a la fecha de llegada");
        }
        if (!llegada.isEqual(vuelo.getFechaHora().toLocalDate())) {
            throw new ValidationException("La fecha de llegada debe coincidir con el dia del vuelo (" + vuelo.getFechaHora().toLocalDate() + ")");
        }
        if (!NormalizadorTexto.sonIguales(hotel.getCiudad(), vuelo.getDestino())) {
            throw new ValidationException("La ciudad del hotel (" + hotel.getCiudad() + ") debe coincidir con el destino del vuelo (" + vuelo.getDestino() + ")");
        }

        // 5. Validar duplicados: Impedir dos reservas del mismo turista para el mismo vuelo
        if (reservaRepository.existsByTuristaIdAndVueloId(turista.getId(), vuelo.getId())) {
            throw new ConflictException("El turista con DNI " + turista.getDni() + " ya posee una reserva para el vuelo " + vuelo.getNumero());
        }

        // 6. Validar cupo de vuelo para la clase elegida
        long reservasClase = reservaRepository.countReservasPorVueloYClase(vuelo.getId(), dto.getClaseVuelo());
        int cupoClase = (dto.getClaseVuelo() == ClaseVuelo.TURISTA) ? vuelo.getCupoTurista() : vuelo.getCupoPrimera();
        if (reservasClase >= cupoClase) {
            throw new ConflictException("No hay cupo disponible en clase " + dto.getClaseVuelo() + " para el vuelo " + vuelo.getNumero());
        }

        // 7. Validar ocupacion del hotel en el intervalo [llegada, partida)
        if (!hotelService.hayDisponibilidad(hotel.getId(), llegada, partida, 1)) {
            throw new ConflictException("No hay disponibilidad en el hotel " + hotel.getNombre() + " para las fechas seleccionadas");
        }

        // 8. Calculo y captura inmutable de precios
        BigDecimal factorVuelo = (dto.getClaseVuelo() == ClaseVuelo.PRIMERA) ? new BigDecimal("1.10") : BigDecimal.ONE;
        BigDecimal precioVueloCapturado = vuelo.getPrecioBase().multiply(factorVuelo).setScale(2, RoundingMode.HALF_UP);

        BigDecimal factorHotel = (dto.getTipoHospedaje() == TipoHospedaje.COMPLETA) ? new BigDecimal("1.05") : BigDecimal.ONE;
        BigDecimal precioHotelCapturado = hotel.getPrecioBaseNoche().multiply(factorHotel).setScale(2, RoundingMode.HALF_UP);

        long noches = ChronoUnit.DAYS.between(llegada, partida);
        BigDecimal totalHotel = precioHotelCapturado.multiply(BigDecimal.valueOf(noches));
        BigDecimal precioTotal = precioVueloCapturado.add(totalHotel).setScale(2, RoundingMode.HALF_UP);

        // 9. Guardar dato historico de sucursal de contratacion
        String sucursalHistorica = turista.getSucursal().getCodigo() + " - " + turista.getSucursal().getCiudad();

        // 10. Generar codigo unico de reserva
        String codigoReserva = "RES-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Reserva reserva = new Reserva();
        reserva.setCodigo(codigoReserva);
        reserva.setTurista(turista);
        reserva.setVuelo(vuelo);
        reserva.setHotel(hotel);
        reserva.setSucursalContratacion(sucursalHistorica);
        reserva.setClaseVuelo(dto.getClaseVuelo());
        reserva.setTipoHospedaje(dto.getTipoHospedaje());
        reserva.setFechaLlegada(llegada);
        reserva.setFechaPartida(partida);
        reserva.setPrecioVueloCapturado(precioVueloCapturado);
        reserva.setPrecioHotelCapturado(precioHotelCapturado);
        reserva.setPrecioTotal(precioTotal);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setFechaPago(null);

        Reserva guardada = reservaRepository.save(reserva);
        return convertirADTO(guardada);
    }

    @Transactional
    public ReservaDTO procesarPago(Long reservaId, String usernameActual) {
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usernameActual));

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + reservaId));

        // Solo el titular/familiar o vendedor/admin puede pagar la reserva
        if (usuarioActual.getRol() == Rol.CLIENTE) {
            validarPertenenciaGrupoFamiliar(usuarioActual.getDni(), reserva.getTurista());
        }

        // Idempotencia: Si ya esta pagada, retornar sin volver a cobrar ni alterar importes
        if (reserva.getEstado() == EstadoReserva.PAGADO) {
            return convertirADTO(reserva);
        }

        reserva.setEstado(EstadoReserva.PAGADO);
        reserva.setFechaPago(LocalDateTime.now());

        Reserva guardada = reservaRepository.save(reserva);
        return convertirADTO(guardada);
    }

    @Transactional
    public ReservaDTO modificarReservaPendiente(Long reservaId, CrearReservaDTO dto, String usernameActual) {
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usernameActual));

        // Solo VENDEDOR y ADMINISTRADOR pueden modificar reservas pendientes
        if (usuarioActual.getRol() == Rol.CLIENTE) {
            throw new ForbiddenException("Los clientes no tienen permiso para modificar reservas directamente");
        }

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + reservaId));

        if (reserva.getEstado() == EstadoReserva.PAGADO) {
            throw new ConflictException("Las reservas pagadas no pueden modificarse");
        }

        Turista turista = turistaRepository.findByDni(dto.getTuristaDni().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Turista no encontrado"));
        Vuelo vuelo = vueloRepository.findByNumero(dto.getVueloNumero().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado"));
        Hotel hotel = hotelRepository.findByCodigo(dto.getHotelCodigo().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel no encontrado"));

        LocalDate llegada = dto.getFechaLlegada();
        LocalDate partida = dto.getFechaPartida();

        if (!partida.isAfter(llegada)) {
            throw new ValidationException("La fecha de partida debe ser posterior a la fecha de llegada");
        }
        if (!llegada.isEqual(vuelo.getFechaHora().toLocalDate())) {
            throw new ValidationException("La fecha de llegada debe coincidir con el dia del vuelo");
        }
        if (!NormalizadorTexto.sonIguales(hotel.getCiudad(), vuelo.getDestino())) {
            throw new ValidationException("La ciudad del hotel debe coincidir con el destino del vuelo");
        }

        // Validar unicidad si cambio de vuelo o turista
        if (!reserva.getVuelo().getId().equals(vuelo.getId()) || !reserva.getTurista().getId().equals(turista.getId())) {
            if (reservaRepository.existsByTuristaIdAndVueloId(turista.getId(), vuelo.getId())) {
                throw new ConflictException("El turista ya posee una reserva para este vuelo");
            }
        }

        // Recalcular precios
        BigDecimal factorVuelo = (dto.getClaseVuelo() == ClaseVuelo.PRIMERA) ? new BigDecimal("1.10") : BigDecimal.ONE;
        BigDecimal precioVueloCapturado = vuelo.getPrecioBase().multiply(factorVuelo).setScale(2, RoundingMode.HALF_UP);

        BigDecimal factorHotel = (dto.getTipoHospedaje() == TipoHospedaje.COMPLETA) ? new BigDecimal("1.05") : BigDecimal.ONE;
        BigDecimal precioHotelCapturado = hotel.getPrecioBaseNoche().multiply(factorHotel).setScale(2, RoundingMode.HALF_UP);

        long noches = ChronoUnit.DAYS.between(llegada, partida);
        BigDecimal totalHotel = precioHotelCapturado.multiply(BigDecimal.valueOf(noches));
        BigDecimal precioTotal = precioVueloCapturado.add(totalHotel).setScale(2, RoundingMode.HALF_UP);

        reserva.setTurista(turista);
        reserva.setVuelo(vuelo);
        reserva.setHotel(hotel);
        reserva.setClaseVuelo(dto.getClaseVuelo());
        reserva.setTipoHospedaje(dto.getTipoHospedaje());
        reserva.setFechaLlegada(llegada);
        reserva.setFechaPartida(partida);
        reserva.setPrecioVueloCapturado(precioVueloCapturado);
        reserva.setPrecioHotelCapturado(precioHotelCapturado);
        reserva.setPrecioTotal(precioTotal);

        Reserva guardada = reservaRepository.save(reserva);
        return convertirADTO(guardada);
    }

    @Transactional
    public void eliminarReservaPendiente(Long reservaId, String usernameActual) {
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usernameActual));

        if (usuarioActual.getRol() == Rol.CLIENTE) {
            throw new ForbiddenException("Los clientes no tienen permiso para eliminar reservas");
        }

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + reservaId));

        if (reserva.getEstado() == EstadoReserva.PAGADO) {
            throw new ConflictException("Las reservas pagadas no pueden eliminarse");
        }

        reservaRepository.delete(reserva);
    }

    @Transactional(readOnly = true)
    public ReservaDTO obtenerPorId(Long id, String usernameActual) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usernameActual));

        if (usuarioActual.getRol() == Rol.CLIENTE) {
            validarPertenenciaGrupoFamiliar(usuarioActual.getDni(), reserva.getTurista());
        }

        return convertirADTO(reserva);
    }

    @Transactional(readOnly = true)
    public ReservaDTO obtenerPorCodigo(String codigo, String usernameActual) {
        Reserva reserva = reservaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con codigo: " + codigo));

        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usernameActual));

        if (usuarioActual.getRol() == Rol.CLIENTE) {
            validarPertenenciaGrupoFamiliar(usuarioActual.getDni(), reserva.getTurista());
        }

        return convertirADTO(reserva);
    }

    @Transactional(readOnly = true)
    public Page<ReservaDTO> listarReservas(String codigoTurista,
                                          String dni,
                                          String numeroVuelo,
                                          String codigoHotel,
                                          LocalDate fechaDesde,
                                          LocalDate fechaHasta,
                                          EstadoReserva estado,
                                          int page,
                                          int size,
                                          String usernameActual) {
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usernameActual));

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Si es CLIENTE, acotar estrictamente a su grupo familiar
        if (usuarioActual.getRol() == Rol.CLIENTE) {
            Turista turistaUsuario = turistaRepository.findByDni(usuarioActual.getDni())
                    .orElseThrow(() -> new ResourceNotFoundException("Turista vinculado al usuario no encontrado"));
            Long titularId = turistaUsuario.getTitular() != null ? turistaUsuario.getTitular().getId() : turistaUsuario.getId();
            Page<Reserva> paginaFamilia = reservaRepository.findReservasGrupoFamiliar(titularId, pageable);
            return paginaFamilia.map(this::convertirADTO);
        }

        // Si es VENDEDOR o ADMIN, buscar con filtros combinables y paginacion
        Page<Reserva> pagina = reservaRepository.buscarReservas(
                codigoTurista != null && !codigoTurista.trim().isEmpty() ? codigoTurista.trim() : null,
                dni != null && !dni.trim().isEmpty() ? dni.trim() : null,
                numeroVuelo != null && !numeroVuelo.trim().isEmpty() ? numeroVuelo.trim() : null,
                codigoHotel != null && !codigoHotel.trim().isEmpty() ? codigoHotel.trim() : null,
                fechaDesde,
                fechaHasta,
                estado,
                pageable
        );

        return pagina.map(this::convertirADTO);
    }

    private void validarPertenenciaGrupoFamiliar(String userDni, Turista turistaDestino) {
        Turista turistaUsuario = turistaRepository.findByDni(userDni)
                .orElseThrow(() -> new ResourceNotFoundException("Turista asociado al usuario no encontrado"));

        Long titularUsuario = turistaUsuario.getTitular() != null ? turistaUsuario.getTitular().getId() : turistaUsuario.getId();
        Long titularDestino = turistaDestino.getTitular() != null ? turistaDestino.getTitular().getId() : turistaDestino.getId();

        if (!titularUsuario.equals(titularDestino)) {
            throw new ForbiddenException("No tiene permisos para acceder o reservar para un turista ajeno a su grupo familiar");
        }
    }

    public ReservaDTO convertirADTO(Reserva r) {
        if (r == null) {
            return null;
        }
        ReservaDTO dto = new ReservaDTO();
        dto.setId(r.getId());
        dto.setCodigo(r.getCodigo());

        if (r.getTurista() != null) {
            dto.setTuristaId(r.getTurista().getId());
            dto.setTuristaDni(r.getTurista().getDni());
            dto.setTuristaNombreCompleto(r.getTurista().getNombre() + " " + r.getTurista().getApellido());
        }

        if (r.getVuelo() != null) {
            dto.setVueloId(r.getVuelo().getId());
            dto.setVueloNumero(r.getVuelo().getNumero());
            dto.setVueloOrigen(r.getVuelo().getOrigen());
            dto.setVueloDestino(r.getVuelo().getDestino());
            dto.setVueloFechaHora(r.getVuelo().getFechaHora());
        }

        if (r.getHotel() != null) {
            dto.setHotelId(r.getHotel().getId());
            dto.setHotelCodigo(r.getHotel().getCodigo());
            dto.setHotelNombre(r.getHotel().getNombre());
            dto.setHotelCiudad(r.getHotel().getCiudad());
        }

        dto.setSucursalContratacion(r.getSucursalContratacion());
        dto.setClaseVuelo(r.getClaseVuelo());
        dto.setTipoHospedaje(r.getTipoHospedaje());
        dto.setFechaLlegada(r.getFechaLlegada());
        dto.setFechaPartida(r.getFechaPartida());

        if (r.getFechaLlegada() != null && r.getFechaPartida() != null) {
            dto.setNoches(ChronoUnit.DAYS.between(r.getFechaLlegada(), r.getFechaPartida()));
        }

        dto.setPrecioVueloCapturado(r.getPrecioVueloCapturado());
        dto.setPrecioHotelCapturado(r.getPrecioHotelCapturado());
        dto.setPrecioTotal(r.getPrecioTotal());
        dto.setEstado(r.getEstado());
        dto.setFechaPago(r.getFechaPago());

        return dto;
    }
}

