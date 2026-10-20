package com.tijetravel.backend.service;

import com.tijetravel.backend.dto.HotelDTO;
import com.tijetravel.backend.exception.ConflictException;
import com.tijetravel.backend.exception.ResourceNotFoundException;
import com.tijetravel.backend.exception.ValidationException;
import com.tijetravel.backend.model.Hotel;
import com.tijetravel.backend.repository.HotelRepository;
import com.tijetravel.backend.repository.ReservaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final ReservaRepository reservaRepository;

    public HotelService(HotelRepository hotelRepository, ReservaRepository reservaRepository) {
        this.hotelRepository = hotelRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public List<String> obtenerCiudades() {
        return hotelRepository.findDistinctCiudades();
    }

    @Transactional(readOnly = true)
    public List<String> obtenerNombres() {
        return hotelRepository.findDistinctNombres();
    }

    @Transactional(readOnly = true)
    public HotelDTO obtenerPorCodigo(String codigo) {
        Hotel hotel = hotelRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel no encontrado con codigo: " + codigo));
        return convertirADTO(hotel, null);
    }

    @Transactional(readOnly = true)
    public HotelDTO obtenerPorId(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel no encontrado con id: " + id));
        return convertirADTO(hotel, null);
    }

    @Transactional(readOnly = true)
    public Page<HotelDTO> buscarHoteles(String ciudad, String nombre, int page, int size, String ordenPrecio) {
        Sort sort = Sort.by("precioBaseNoche").ascending();
        if ("DESC".equalsIgnoreCase(ordenPrecio)) {
            sort = Sort.by("precioBaseNoche").descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Hotel> pagina = hotelRepository.buscarHoteles(
                ciudad != null && !ciudad.trim().isEmpty() ? ciudad.trim() : null,
                nombre != null && !nombre.trim().isEmpty() ? nombre.trim() : null,
                pageable
        );

        return pagina.map(h -> convertirADTO(h, null));
    }

    @Transactional(readOnly = true)
    public long calcularPlazasDisponibles(Long hotelId, LocalDate llegada, LocalDate partida) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel no encontrado"));

        int capacidad = hotel.getCapacidadTotal();
        long minimaDisponibilidad = capacidad;

        // Recorremos cada noche en el intervalo [llegada, partida)
        for (LocalDate dia = llegada; dia.isBefore(partida); dia = dia.plusDays(1)) {
            long ocupacion = hotelRepository.countOcupacionEnFecha(hotelId, dia);
            long disponibles = capacidad - ocupacion;
            if (disponibles < minimaDisponibilidad) {
                minimaDisponibilidad = disponibles;
            }
        }

        if (minimaDisponibilidad < 0) {
            return 0;
        }
        return minimaDisponibilidad;
    }

    @Transactional(readOnly = true)
    public boolean hayDisponibilidad(Long hotelId, LocalDate llegada, LocalDate partida, int personas) {
        long disponibles = calcularPlazasDisponibles(hotelId, llegada, partida);
        return disponibles >= personas;
    }

    @Transactional
    public HotelDTO crear(HotelDTO dto) {
        validarReglasPrecio(dto.getPrecioBaseNoche());

        if (hotelRepository.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new ConflictException("Ya existe un hotel con el codigo: " + dto.getCodigo());
        }
        if (hotelRepository.existsByNombreAndCiudad(dto.getNombre().trim(), dto.getCiudad().trim())) {
            throw new ConflictException("Ya existe un hotel con el nombre '" + dto.getNombre() + "' en la ciudad '" + dto.getCiudad() + "'");
        }

        Hotel hotel = new Hotel();
        hotel.setCodigo(dto.getCodigo().trim());
        hotel.setNombre(dto.getNombre().trim());
        hotel.setCiudad(dto.getCiudad().trim());
        hotel.setDireccion(dto.getDireccion().trim());
        hotel.setTelefono(dto.getTelefono().trim());
        hotel.setCapacidadTotal(dto.getCapacidadTotal());
        hotel.setPrecioBaseNoche(dto.getPrecioBaseNoche());

        Hotel guardado = hotelRepository.save(hotel);
        return convertirADTO(guardado, null);
    }

    @Transactional
    public HotelDTO actualizar(Long id, HotelDTO dto) {
        validarReglasPrecio(dto.getPrecioBaseNoche());

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel no encontrado con id: " + id));

        // Verificar unicidad de nombre + ciudad si cambiaron
        if (!hotel.getNombre().equalsIgnoreCase(dto.getNombre().trim()) || !hotel.getCiudad().equalsIgnoreCase(dto.getCiudad().trim())) {
            if (hotelRepository.existsByNombreAndCiudad(dto.getNombre().trim(), dto.getCiudad().trim())) {
                throw new ConflictException("Ya existe un hotel con el nombre '" + dto.getNombre() + "' en la ciudad '" + dto.getCiudad() + "'");
            }
        }

        hotel.setNombre(dto.getNombre().trim());
        hotel.setCiudad(dto.getCiudad().trim());
        hotel.setDireccion(dto.getDireccion().trim());
        hotel.setTelefono(dto.getTelefono().trim());
        hotel.setCapacidadTotal(dto.getCapacidadTotal());
        hotel.setPrecioBaseNoche(dto.getPrecioBaseNoche());

        Hotel guardado = hotelRepository.save(hotel);
        return convertirADTO(guardado, null);
    }

    @Transactional
    public void eliminar(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel no encontrado con id: " + id));

        if (reservaRepository.existsByHotelId(id)) {
            throw new ConflictException("No se puede eliminar el hotel porque tiene reservas asociadas.");
        }

        hotelRepository.delete(hotel);
    }

    private void validarReglasPrecio(BigDecimal precio) {
        if (precio == null) {
            throw new ValidationException("El precio base es obligatorio");
        }
        BigDecimal min = new BigDecimal("25000.00");
        BigDecimal max = new BigDecimal("55000.00");
        if (precio.compareTo(min) < 0 || precio.compareTo(max) > 0) {
            throw new ValidationException("El precio base de hotel debe estar entre $25.000 y $55.000");
        }
        // Validar que sea multiplo de 1.000
        BigDecimal[] division = precio.divideAndRemainder(new BigDecimal("1000.00"));
        if (division[1].compareTo(BigDecimal.ZERO) != 0) {
            throw new ValidationException("El precio base de hotel debe ser multiplo de $1.000");
        }
    }

    public HotelDTO convertirADTO(Hotel h, Long plazasDisponibles) {
        if (h == null) {
            return null;
        }
        return new HotelDTO(
                h.getId(),
                h.getCodigo(),
                h.getNombre(),
                h.getCiudad(),
                h.getDireccion(),
                h.getTelefono(),
                h.getCapacidadTotal(),
                h.getPrecioBaseNoche(),
                plazasDisponibles
        );
    }
}

