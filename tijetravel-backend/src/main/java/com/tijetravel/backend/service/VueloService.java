package com.tijetravel.backend.service;

import com.tijetravel.backend.dto.VueloDTO;
import com.tijetravel.backend.exception.ConflictException;
import com.tijetravel.backend.exception.ResourceNotFoundException;
import com.tijetravel.backend.exception.ValidationException;
import com.tijetravel.backend.model.ClaseVuelo;
import com.tijetravel.backend.model.Vuelo;
import com.tijetravel.backend.repository.ReservaRepository;
import com.tijetravel.backend.repository.VueloRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VueloService {

    private final VueloRepository vueloRepository;
    private final ReservaRepository reservaRepository;

    public VueloService(VueloRepository vueloRepository, ReservaRepository reservaRepository) {
        this.vueloRepository = vueloRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public List<String> obtenerOrigenes() {
        return vueloRepository.findDistinctOrigenes();
    }

    @Transactional(readOnly = true)
    public List<String> obtenerDestinos() {
        return vueloRepository.findDistinctDestinos();
    }

    @Transactional(readOnly = true)
    public VueloDTO obtenerPorNumero(String numero) {
        Vuelo vuelo = vueloRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con numero: " + numero));
        return enriquecerConPlazas(vuelo);
    }

    @Transactional(readOnly = true)
    public VueloDTO obtenerPorId(Long id) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id));
        return enriquecerConPlazas(vuelo);
    }

    @Transactional(readOnly = true)
    public Page<VueloDTO> buscarVuelos(String origen, String destino, LocalDateTime desde, LocalDateTime hasta, int page, int size, String ordenPrecio) {
        Sort sort = Sort.by("precioBase").ascending();
        if ("DESC".equalsIgnoreCase(ordenPrecio)) {
            sort = Sort.by("precioBase").descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Vuelo> pagina = vueloRepository.buscarVuelosFiltro(
                origen != null && !origen.trim().isEmpty() ? origen.trim() : null,
                destino != null && !destino.trim().isEmpty() ? destino.trim() : null,
                desde,
                hasta,
                pageable
        );

        return pagina.map(this::enriquecerConPlazas);
    }

    @Transactional(readOnly = true)
    public List<VueloDTO> buscarVuelosParaFechaYRuta(String origen, String destino, LocalDate fecha) {
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.atTime(LocalTime.MAX);
        List<Vuelo> vuelos = vueloRepository.buscarVuelosParaFecha(origen, destino, desde, hasta);
        List<VueloDTO> resultado = new ArrayList<>();
        for (int i = 0; i < vuelos.size(); i++) {
            resultado.add(enriquecerConPlazas(vuelos.get(i)));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<VueloDTO> sugerirVuelosDestino(String destino, LocalDate fechaLlegada) {
        LocalDateTime desde = fechaLlegada.minusDays(7).atStartOfDay();
        LocalDateTime hasta = fechaLlegada.plusDays(7).atTime(LocalTime.MAX);
        List<Vuelo> vuelos = vueloRepository.sugerirVuelosDestino(destino, desde, hasta);
        List<VueloDTO> resultado = new ArrayList<>();
        for (int i = 0; i < vuelos.size(); i++) {
            Vuelo v = vuelos.get(i);
            // Solo sugerir vuelos de fechas distintas a la solicitada
            if (!v.getFechaHora().toLocalDate().isEqual(fechaLlegada)) {
                resultado.add(enriquecerConPlazas(v));
            }
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public boolean hayDisponibilidad(Long vueloId, ClaseVuelo clase, int personas) {
        Vuelo vuelo = vueloRepository.findById(vueloId)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado"));

        long reservasClase = reservaRepository.countReservasPorVueloYClase(vueloId, clase);
        int cupo = (clase == ClaseVuelo.TURISTA) ? vuelo.getCupoTurista() : vuelo.getCupoPrimera();
        long disponibles = cupo - reservasClase;

        return disponibles >= personas;
    }

    @Transactional
    public VueloDTO crear(VueloDTO dto) {
        validarReglasVuelo(dto);

        if (vueloRepository.findByNumero(dto.getNumero()).isPresent()) {
            throw new ConflictException("Ya existe un vuelo con el numero: " + dto.getNumero());
        }

        Vuelo vuelo = new Vuelo();
        vuelo.setNumero(dto.getNumero().trim());
        vuelo.setFechaHora(dto.getFechaHora());
        vuelo.setOrigen(dto.getOrigen().trim());
        vuelo.setDestino(dto.getDestino().trim());
        vuelo.setAsientosTotales(dto.getAsientosTotales());
        vuelo.setCupoTurista(dto.getCupoTurista());
        vuelo.setCupoPrimera(dto.getCupoPrimera());
        vuelo.setPrecioBase(dto.getPrecioBase());

        Vuelo guardado = vueloRepository.save(vuelo);
        return enriquecerConPlazas(guardado);
    }

    @Transactional
    public VueloDTO actualizar(Long id, VueloDTO dto) {
        validarReglasVuelo(dto);

        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id));

        vuelo.setFechaHora(dto.getFechaHora());
        vuelo.setOrigen(dto.getOrigen().trim());
        vuelo.setDestino(dto.getDestino().trim());
        vuelo.setAsientosTotales(dto.getAsientosTotales());
        vuelo.setCupoTurista(dto.getCupoTurista());
        vuelo.setCupoPrimera(dto.getCupoPrimera());
        vuelo.setPrecioBase(dto.getPrecioBase());

        Vuelo guardado = vueloRepository.save(vuelo);
        return enriquecerConPlazas(guardado);
    }

    @Transactional
    public void eliminar(Long id) {
        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id));

        if (reservaRepository.existsByVueloId(id)) {
            throw new ConflictException("No se puede eliminar el vuelo porque tiene reservas asociadas.");
        }

        vueloRepository.delete(vuelo);
    }

    private void validarReglasVuelo(VueloDTO dto) {
        if (dto.getOrigen() != null && dto.getDestino() != null) {
            if (dto.getOrigen().trim().equalsIgnoreCase(dto.getDestino().trim())) {
                throw new ValidationException("El origen y el destino del vuelo deben ser distintos");
            }
        }
        if (dto.getCupoTurista() < 0 || dto.getCupoPrimera() < 0) {
            throw new ValidationException("Los cupos no pueden ser negativos");
        }
        if (dto.getCupoTurista() + dto.getCupoPrimera() > dto.getAsientosTotales()) {
            throw new ValidationException("La suma de cupos turista y primera no puede superar los asientos totales");
        }

        BigDecimal precio = dto.getPrecioBase();
        if (precio == null) {
            throw new ValidationException("El precio base es obligatorio");
        }
        BigDecimal min = new BigDecimal("150000.00");
        BigDecimal max = new BigDecimal("300000.00");
        if (precio.compareTo(min) < 0 || precio.compareTo(max) > 0) {
            throw new ValidationException("El precio base de vuelo debe estar entre $150.000 y $300.000");
        }
        BigDecimal[] division = precio.divideAndRemainder(new BigDecimal("10000.00"));
        if (division[1].compareTo(BigDecimal.ZERO) != 0) {
            throw new ValidationException("El precio base de vuelo debe ser multiplo de $10.000");
        }
    }

    private VueloDTO enriquecerConPlazas(Vuelo v) {
        if (v == null) {
            return null;
        }
        long resTurista = reservaRepository.countReservasPorVueloYClase(v.getId(), ClaseVuelo.TURISTA);
        long resPrimera = reservaRepository.countReservasPorVueloYClase(v.getId(), ClaseVuelo.PRIMERA);

        long dispTurista = v.getCupoTurista() - resTurista;
        long dispPrimera = v.getCupoPrimera() - resPrimera;

        return new VueloDTO(
                v.getId(),
                v.getNumero(),
                v.getFechaHora(),
                v.getOrigen(),
                v.getDestino(),
                v.getAsientosTotales(),
                v.getCupoTurista(),
                v.getCupoPrimera(),
                v.getPrecioBase(),
                dispTurista < 0 ? 0L : dispTurista,
                dispPrimera < 0 ? 0L : dispPrimera
        );
    }
}

