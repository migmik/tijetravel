package com.tijetravel.backend.service;

import com.tijetravel.backend.dto.BusquedaViajeRequestDTO;
import com.tijetravel.backend.dto.BusquedaViajeResponseDTO;
import com.tijetravel.backend.dto.HotelDTO;
import com.tijetravel.backend.dto.PaqueteViajeDTO;
import com.tijetravel.backend.dto.VueloDTO;
import com.tijetravel.backend.exception.ValidationException;
import com.tijetravel.backend.model.ClaseVuelo;
import com.tijetravel.backend.model.Hotel;
import com.tijetravel.backend.repository.HotelRepository;
import com.tijetravel.backend.util.NormalizadorTexto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class BusquedaService {

    private final VueloService vueloService;
    private final HotelService hotelService;
    private final HotelRepository hotelRepository;

    public BusquedaService(VueloService vueloService,
                           HotelService hotelService,
                           HotelRepository hotelRepository) {
        this.vueloService = vueloService;
        this.hotelService = hotelService;
        this.hotelRepository = hotelRepository;
    }

    @Transactional(readOnly = true)
    public BusquedaViajeResponseDTO buscarViajes(BusquedaViajeRequestDTO request) {
        validarParametrosBusqueda(request);

        String origen = request.getOrigen().trim();
        String destino = request.getDestino().trim();
        LocalDate llegada = request.getFechaLlegada();
        LocalDate partida = request.getFechaPartida();
        int personas = request.getPersonas() != null ? request.getPersonas() : 1;
        long noches = ChronoUnit.DAYS.between(llegada, partida);

        BusquedaViajeResponseDTO response = new BusquedaViajeResponseDTO();

        // 1. Buscar vuelos que cumplan origen, destino y fecha exacta de llegada
        List<VueloDTO> vuelosCandidatos = vueloService.buscarVuelosParaFechaYRuta(origen, destino, llegada);
        List<VueloDTO> vuelosDisponibles = new ArrayList<>();

        for (int i = 0; i < vuelosCandidatos.size(); i++) {
            VueloDTO v = vuelosCandidatos.get(i);
            // Comprobar que haya al menos capacidad de cupo turista o primera para las personas solicitadas
            if (v.getPlazasTuristaDisponibles() >= personas || v.getPlazasPrimeraDisponibles() >= personas) {
                vuelosDisponibles.add(v);
            }
        }
        response.setVuelos(vuelosDisponibles);

        // Si no hay vuelos para la fecha indicada, buscar sugerencias de vuelos cercanos
        if (vuelosDisponibles.isEmpty()) {
            List<VueloDTO> sugerencias = vueloService.sugerirVuelosDestino(destino, llegada);
            response.setSugerenciasVuelos(sugerencias);
        }

        // 2. Buscar hoteles disponibles en la ciudad de destino para las fechas requeridas
        List<Hotel> hotelesCandidatos = hotelRepository.findByCiudadIgnoreCase(destino);
        List<HotelDTO> hotelesDisponibles = new ArrayList<>();

        for (int i = 0; i < hotelesCandidatos.size(); i++) {
            Hotel h = hotelesCandidatos.get(i);
            long plazasDisponibles = hotelService.calcularPlazasDisponibles(h.getId(), llegada, partida);
            if (plazasDisponibles >= personas) {
                hotelesDisponibles.add(hotelService.convertirADTO(h, plazasDisponibles));
            }
        }

        // Ordenar hoteles por precio
        if ("DESC".equalsIgnoreCase(request.getOrdenPrecio())) {
            Collections.sort(hotelesDisponibles, new Comparator<HotelDTO>() {
                @Override
                public int compare(HotelDTO o1, HotelDTO o2) {
                    return o2.getPrecioBaseNoche().compareTo(o1.getPrecioBaseNoche());
                }
            });
        } else {
            Collections.sort(hotelesDisponibles, new Comparator<HotelDTO>() {
                @Override
                public int compare(HotelDTO o1, HotelDTO o2) {
                    return o1.getPrecioBaseNoche().compareTo(o2.getPrecioBaseNoche());
                }
            });
        }
        response.setHoteles(hotelesDisponibles);

        // 3. Generar combinaciones paquete (Vuelo + Hotel)
        List<PaqueteViajeDTO> paquetes = new ArrayList<>();
        BigDecimal cantPersonasBD = BigDecimal.valueOf(personas);
        BigDecimal cantNochesBD = BigDecimal.valueOf(noches);

        for (int i = 0; i < vuelosDisponibles.size(); i++) {
            VueloDTO v = vuelosDisponibles.get(i);
            for (int j = 0; j < hotelesDisponibles.size(); j++) {
                HotelDTO h = hotelesDisponibles.get(j);

                BigDecimal precioVueloPorPersona = v.getPrecioBase();
                BigDecimal precioHotelPorPersona = h.getPrecioBaseNoche().multiply(cantNochesBD);
                BigDecimal precioTotalPorPersona = precioVueloPorPersona.add(precioHotelPorPersona);
                BigDecimal precioTotalGrupo = precioTotalPorPersona.multiply(cantPersonasBD);

                PaqueteViajeDTO paquete = new PaqueteViajeDTO(
                        v,
                        h,
                        noches,
                        precioVueloPorPersona,
                        precioHotelPorPersona,
                        precioTotalPorPersona,
                        precioTotalGrupo
                );
                paquetes.add(paquete);
            }
        }

        // Ordenar paquetes por precio orientativo por persona
        if ("DESC".equalsIgnoreCase(request.getOrdenPrecio())) {
            Collections.sort(paquetes, new Comparator<PaqueteViajeDTO>() {
                @Override
                public int compare(PaqueteViajeDTO o1, PaqueteViajeDTO o2) {
                    return o2.getPrecioPorPersona().compareTo(o1.getPrecioPorPersona());
                }
            });
        } else {
            Collections.sort(paquetes, new Comparator<PaqueteViajeDTO>() {
                @Override
                public int compare(PaqueteViajeDTO o1, PaqueteViajeDTO o2) {
                    return o1.getPrecioPorPersona().compareTo(o2.getPrecioPorPersona());
                }
            });
        }
        response.setPaquetes(paquetes);

        return response;
    }

    private void validarParametrosBusqueda(BusquedaViajeRequestDTO request) {
        if (request.getOrigen() == null || request.getOrigen().trim().isEmpty()) {
            throw new ValidationException("La ciudad de origen es obligatoria");
        }
        if (request.getDestino() == null || request.getDestino().trim().isEmpty()) {
            throw new ValidationException("La ciudad de destino es obligatoria");
        }
        if (NormalizadorTexto.sonIguales(request.getOrigen(), request.getDestino())) {
            throw new ValidationException("El origen y el destino deben ser ciudades distintas");
        }
        if (request.getFechaLlegada() == null) {
            throw new ValidationException("La fecha de llegada es obligatoria");
        }
        if (request.getFechaPartida() == null) {
            throw new ValidationException("La fecha de partida es obligatoria");
        }
        if (!request.getFechaPartida().isAfter(request.getFechaLlegada())) {
            throw new ValidationException("La fecha de partida debe ser posterior a la fecha de llegada");
        }
        if (request.getPersonas() == null || request.getPersonas() < 1 || request.getPersonas() > 20) {
            throw new ValidationException("La cantidad de personas debe ser entre 1 y 20");
        }
    }
}

