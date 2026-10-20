package com.tijetravel.backend.service;

import com.tijetravel.backend.dto.SucursalDTO;
import com.tijetravel.backend.exception.ConflictException;
import com.tijetravel.backend.exception.ResourceNotFoundException;
import com.tijetravel.backend.model.Sucursal;
import com.tijetravel.backend.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    public SucursalService(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    @Transactional(readOnly = true)
    public List<SucursalDTO> obtenerTodas() {
        List<Sucursal> lista = sucursalRepository.findAll();
        List<SucursalDTO> resultado = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            resultado.add(convertirADTO(lista.get(i)));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<String> obtenerCiudades() {
        return sucursalRepository.findDistinctCiudades();
    }

    @Transactional(readOnly = true)
    public SucursalDTO obtenerPorCodigo(String codigo) {
        Sucursal sucursal = sucursalRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con codigo: " + codigo));
        return convertirADTO(sucursal);
    }

    @Transactional(readOnly = true)
    public SucursalDTO obtenerPorId(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
        return convertirADTO(sucursal);
    }

    @Transactional
    public SucursalDTO crear(SucursalDTO dto) {
        if (sucursalRepository.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new ConflictException("Ya existe una sucursal con el codigo: " + dto.getCodigo());
        }
        Sucursal sucursal = new Sucursal();
        sucursal.setCodigo(dto.getCodigo().trim());
        sucursal.setCiudad(dto.getCiudad().trim());
        sucursal.setDireccion(dto.getDireccion().trim());
        sucursal.setTelefono(dto.getTelefono().trim());
        Sucursal guardada = sucursalRepository.save(sucursal);
        return convertirADTO(guardada);
    }

    @Transactional
    public SucursalDTO actualizar(Long id, SucursalDTO dto) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
        sucursal.setCiudad(dto.getCiudad().trim());
        sucursal.setDireccion(dto.getDireccion().trim());
        sucursal.setTelefono(dto.getTelefono().trim());
        Sucursal guardada = sucursalRepository.save(sucursal);
        return convertirADTO(guardada);
    }

    @Transactional
    public void eliminar(Long id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
        try {
            sucursalRepository.delete(sucursal);
            sucursalRepository.flush();
        } catch (Exception e) {
            throw new ConflictException("No se puede eliminar la sucursal porque tiene turistas o registros vinculados.");
        }
    }

    public SucursalDTO convertirADTO(Sucursal s) {
        if (s == null) {
            return null;
        }
        return new SucursalDTO(
                s.getId(),
                s.getCodigo(),
                s.getCiudad(),
                s.getDireccion(),
                s.getTelefono()
        );
    }
}

