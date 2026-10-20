package com.tijetravel.backend.service;

import com.tijetravel.backend.dto.CrearUsuarioAdminDTO;
import com.tijetravel.backend.dto.RegistroClienteDTO;
import com.tijetravel.backend.dto.UsuarioDTO;
import com.tijetravel.backend.exception.ConflictException;
import com.tijetravel.backend.exception.ResourceNotFoundException;
import com.tijetravel.backend.exception.ValidationException;
import com.tijetravel.backend.model.Rol;
import com.tijetravel.backend.model.Sucursal;
import com.tijetravel.backend.model.Turista;
import com.tijetravel.backend.model.Usuario;
import com.tijetravel.backend.repository.SucursalRepository;
import com.tijetravel.backend.repository.TuristaRepository;
import com.tijetravel.backend.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TuristaRepository turistaRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          TuristaRepository turistaRepository,
                          SucursalRepository sucursalRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.turistaRepository = turistaRepository;
        this.sucursalRepository = sucursalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UsuarioDTO> buscarUsuarios(String codigo, String dni, String username, Rol rol, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Usuario> pagina = usuarioRepository.buscarUsuarios(
                codigo != null && !codigo.trim().isEmpty() ? codigo.trim() : null,
                dni != null && !dni.trim().isEmpty() ? dni.trim() : null,
                username != null && !username.trim().isEmpty() ? username.trim() : null,
                rol,
                pageable
        );
        return pagina.map(this::convertirADTO);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        return convertirADTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorDni(String dni) {
        Usuario usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con DNI: " + dni));
        return convertirADTO(usuario);
    }

    @Transactional
    public UsuarioDTO registrarCliente(RegistroClienteDTO dto) {
        // Validar username único
        if (usuarioRepository.existsByUsername(dto.getUsername().trim())) {
            throw new ConflictException("El nombre de usuario ya se encuentra registrado: " + dto.getUsername());
        }

        // Validar DNI: no duplicar usuario para el mismo DNI
        if (usuarioRepository.existsByDni(dto.getDni().trim())) {
            throw new ConflictException("Ya existe una cuenta de usuario vinculada al DNI: " + dto.getDni());
        }

        // Verificar o crear el Turista correspondiente
        Turista turista = turistaRepository.findByDni(dto.getDni().trim()).orElse(null);
        if (turista == null) {
            Sucursal sucursal = sucursalRepository.findByCodigo(dto.getSucursalCodigo().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + dto.getSucursalCodigo()));

            turista = new Turista();
            turista.setCodigo("TUR-" + dto.getDni().trim());
            turista.setDni(dto.getDni().trim());
            turista.setNombre(dto.getNombre().trim());
            turista.setApellido(dto.getApellido().trim());
            turista.setDireccion(dto.getDireccion().trim());
            turista.setEmail(dto.getEmail().trim());
            turista.setTelefonoFijo(dto.getTelefonoFijo() != null ? dto.getTelefonoFijo().trim() : null);
            turista.setTelefonoCelular(dto.getTelefonoCelular().trim());
            turista.setSucursal(sucursal);
            turista.setTitular(null);
            turistaRepository.save(turista);
        }

        // Crear el usuario con rol CLIENTE (el autorregistro nunca elige privilegios de personal)
        Usuario usuario = new Usuario();
        usuario.setCodigo("USR-" + dto.getUsername().trim().toUpperCase());
        usuario.setUsername(dto.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(Rol.CLIENTE);
        usuario.setDni(dto.getDni().trim());

        Usuario guardado = usuarioRepository.save(usuario);
        return convertirADTO(guardado);
    }

    @Transactional
    public UsuarioDTO crearUsuarioPorAdmin(CrearUsuarioAdminDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername().trim())) {
            throw new ConflictException("El nombre de usuario ya se encuentra registrado: " + dto.getUsername());
        }
        if (usuarioRepository.existsByDni(dto.getDni().trim())) {
            throw new ConflictException("Ya existe un usuario con el DNI: " + dto.getDni());
        }

        // Si es cliente, debe existir un turista con ese DNI o se debe vincular
        if (dto.getRol() == Rol.CLIENTE && !turistaRepository.existsByDni(dto.getDni().trim())) {
            throw new ValidationException("Para crear un usuario CLIENTE, primero debe existir el turista con DNI: " + dto.getDni());
        }

        Usuario usuario = new Usuario();
        usuario.setCodigo("USR-" + dto.getUsername().trim().toUpperCase());
        usuario.setUsername(dto.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol());
        usuario.setDni(dto.getDni().trim());

        Usuario guardado = usuarioRepository.save(usuario);
        return convertirADTO(guardado);
    }

    @Transactional
    public UsuarioDTO cambiarRol(Long id, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (nuevoRol == Rol.CLIENTE && !turistaRepository.existsByDni(usuario.getDni())) {
            throw new ValidationException("No se puede asignar rol CLIENTE si no existe un turista vinculado con DNI: " + usuario.getDni());
        }

        usuario.setRol(nuevoRol);
        Usuario guardado = usuarioRepository.save(usuario);
        return convertirADTO(guardado);
    }

    public UsuarioDTO convertirADTO(Usuario u) {
        if (u == null) {
            return null;
        }
        return new UsuarioDTO(
                u.getId(),
                u.getCodigo(),
                u.getUsername(),
                u.getRol().name(),
                u.getDni()
        );
    }
}

