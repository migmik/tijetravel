package com.tijetravel.backend.service;

import com.tijetravel.backend.dto.TuristaDTO;
import com.tijetravel.backend.exception.ConflictException;
import com.tijetravel.backend.exception.ResourceNotFoundException;
import com.tijetravel.backend.exception.ValidationException;
import com.tijetravel.backend.model.Sucursal;
import com.tijetravel.backend.model.Turista;
import com.tijetravel.backend.repository.ReservaRepository;
import com.tijetravel.backend.repository.SucursalRepository;
import com.tijetravel.backend.repository.TuristaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TuristaService {

    private final TuristaRepository turistaRepository;
    private final SucursalRepository sucursalRepository;
    private final ReservaRepository reservaRepository;

    public TuristaService(TuristaRepository turistaRepository,
                          SucursalRepository sucursalRepository,
                          ReservaRepository reservaRepository) {
        this.turistaRepository = turistaRepository;
        this.sucursalRepository = sucursalRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public Page<TuristaDTO> buscarTuristas(String codigo, String dni, String nombre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
        Page<Turista> pagina = turistaRepository.buscarTuristas(
                codigo != null && !codigo.trim().isEmpty() ? codigo.trim() : null,
                dni != null && !dni.trim().isEmpty() ? dni.trim() : null,
                nombre != null && !nombre.trim().isEmpty() ? nombre.trim() : null,
                pageable
        );
        return pagina.map(this::convertirADTO);
    }

    @Transactional(readOnly = true)
    public TuristaDTO obtenerPorDni(String dni) {
        Turista turista = turistaRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Turista no encontrado con DNI: " + dni));
        return convertirADTO(turista);
    }

    @Transactional(readOnly = true)
    public TuristaDTO obtenerPorId(Long id) {
        Turista turista = turistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turista no encontrado con id: " + id));
        return convertirADTO(turista);
    }

    @Transactional(readOnly = true)
    public List<TuristaDTO> obtenerGrupoFamiliar(String dni) {
        Turista turista = turistaRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Turista no encontrado con DNI: " + dni));

        // Determinar el titular del grupo familiar
        Long titularId = turista.getTitular() != null ? turista.getTitular().getId() : turista.getId();
        List<Turista> familiares = turistaRepository.findGrupoFamiliar(titularId);

        List<TuristaDTO> resultado = new ArrayList<>();
        for (int i = 0; i < familiares.size(); i++) {
            resultado.add(convertirADTO(familiares.get(i)));
        }
        return resultado;
    }

    @Transactional
    public TuristaDTO crear(TuristaDTO dto) {
        validarDni(dto.getDni());

        if (turistaRepository.existsByDni(dto.getDni().trim())) {
            throw new ConflictException("Ya existe un turista registrado con el DNI: " + dto.getDni());
        }

        Sucursal sucursal;
        Turista titular = null;

        if (dto.getTitularId() != null) {
            titular = turistaRepository.findById(dto.getTitularId())
                    .orElseThrow(() -> new ResourceNotFoundException("Titular familiar no encontrado con id: " + dto.getTitularId()));
            // Los familiares comparten la sucursal del titular
            sucursal = titular.getSucursal();
        } else {
            if (dto.getSucursalId() == null) {
                throw new ValidationException("La sucursal es obligatoria");
            }
            sucursal = sucursalRepository.findById(dto.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + dto.getSucursalId()));
        }

        String codigo = dto.getCodigo();
        if (codigo == null || codigo.trim().isEmpty()) {
            codigo = "TUR-" + dto.getDni().trim();
        }

        Turista turista = new Turista();
        turista.setCodigo(codigo.trim());
        turista.setDni(dto.getDni().trim());
        turista.setNombre(dto.getNombre().trim());
        turista.setApellido(dto.getApellido().trim());
        turista.setDireccion(dto.getDireccion().trim());
        turista.setEmail(dto.getEmail().trim());
        turista.setTelefonoFijo(dto.getTelefonoFijo() != null ? dto.getTelefonoFijo().trim() : null);
        turista.setTelefonoCelular(dto.getTelefonoCelular().trim());
        turista.setSucursal(sucursal);
        turista.setTitular(titular);

        Turista guardado = turistaRepository.save(turista);
        return convertirADTO(guardado);
    }

    @Transactional
    public TuristaDTO actualizar(Long id, TuristaDTO dto) {
        validarDni(dto.getDni());

        Turista turista = turistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turista no encontrado con id: " + id));

        if (!turista.getDni().equalsIgnoreCase(dto.getDni().trim())) {
            if (turistaRepository.existsByDni(dto.getDni().trim())) {
                throw new ConflictException("Ya existe otro turista registrado con el DNI: " + dto.getDni());
            }
        }

        if (dto.getTitularId() != null) {
            if (dto.getTitularId().equals(id)) {
                throw new ValidationException("Un turista no puede ser titular de si mismo");
            }
            Turista titular = turistaRepository.findById(dto.getTitularId())
                    .orElseThrow(() -> new ResourceNotFoundException("Titular familiar no encontrado con id: " + dto.getTitularId()));
            turista.setTitular(titular);
            turista.setSucursal(titular.getSucursal());
        } else if (dto.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + dto.getSucursalId()));
            turista.setSucursal(sucursal);
            turista.setTitular(null);
        }

        turista.setDni(dto.getDni().trim());
        turista.setNombre(dto.getNombre().trim());
        turista.setApellido(dto.getApellido().trim());
        turista.setDireccion(dto.getDireccion().trim());
        turista.setEmail(dto.getEmail().trim());
        turista.setTelefonoFijo(dto.getTelefonoFijo() != null ? dto.getTelefonoFijo().trim() : null);
        turista.setTelefonoCelular(dto.getTelefonoCelular().trim());

        Turista guardado = turistaRepository.save(turista);
        return convertirADTO(guardado);
    }

    @Transactional
    public void eliminar(Long id) {
        Turista turista = turistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turista no encontrado con id: " + id));

        if (reservaRepository.existsByTuristaId(id)) {
            throw new ConflictException("No se puede eliminar el turista porque tiene reservas asociadas.");
        }

        List<Turista> familiares = turistaRepository.findByTitularId(id);
        if (!familiares.isEmpty()) {
            throw new ConflictException("No se puede eliminar el turista porque es titular de un grupo familiar activo.");
        }

        turistaRepository.delete(turista);
    }

    private void validarDni(String dni) {
        if (dni == null || !dni.trim().matches("^[0-9]{7,8}$")) {
            throw new ValidationException("El DNI debe tener 7 u 8 digitos numericos");
        }
    }

    public TuristaDTO convertirADTO(Turista t) {
        if (t == null) {
            return null;
        }
        TuristaDTO dto = new TuristaDTO();
        dto.setId(t.getId());
        dto.setCodigo(t.getCodigo());
        dto.setDni(t.getDni());
        dto.setNombre(t.getNombre());
        dto.setApellido(t.getApellido());
        dto.setDireccion(t.getDireccion());
        dto.setEmail(t.getEmail());
        dto.setTelefonoFijo(t.getTelefonoFijo());
        dto.setTelefonoCelular(t.getTelefonoCelular());

        if (t.getSucursal() != null) {
            dto.setSucursalId(t.getSucursal().getId());
            dto.setSucursalCodigo(t.getSucursal().getCodigo());
            dto.setSucursalCiudad(t.getSucursal().getCiudad());
        }

        if (t.getTitular() != null) {
            dto.setTitularId(t.getTitular().getId());
            dto.setTitularNombreCompleto(t.getTitular().getNombre() + " " + t.getTitular().getApellido());
        }

        return dto;
    }
}

