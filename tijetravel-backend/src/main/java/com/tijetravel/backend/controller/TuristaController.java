package com.tijetravel.backend.controller;

import com.tijetravel.backend.dto.TuristaDTO;
import com.tijetravel.backend.exception.ForbiddenException;
import com.tijetravel.backend.model.Rol;
import com.tijetravel.backend.model.Usuario;
import com.tijetravel.backend.repository.UsuarioRepository;
import com.tijetravel.backend.service.TuristaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/turistas")
public class TuristaController {

    private final TuristaService turistaService;
    private final UsuarioRepository usuarioRepository;

    public TuristaController(TuristaService turistaService, UsuarioRepository usuarioRepository) {
        this.turistaService = turistaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<Page<TuristaDTO>> buscar(
            @RequestParam(name = "codigo", required = false) String codigo,
            @RequestParam(name = "dni", required = false) String dni,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication auth) {

        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        if (usuario.getRol() == Rol.CLIENTE) {
            throw new ForbiddenException("Los clientes no tienen permiso para listar turistas globales");
        }

        Page<TuristaDTO> resultado = turistaService.buscarTuristas(codigo, dni, nombre, page, size);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<TuristaDTO> obtenerPorDni(@PathVariable("dni") String dni, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        if (usuario.getRol() == Rol.CLIENTE) {
            // Verificar si pertenece a su grupo familiar
            List<TuristaDTO> familia = turistaService.obtenerGrupoFamiliar(usuario.getDni());
            boolean pertenece = false;
            for (int i = 0; i < familia.size(); i++) {
                if (familia.get(i).getDni().equals(dni)) {
                    pertenece = true;
                    break;
                }
            }
            if (!pertenece) {
                throw new ForbiddenException("No tiene permisos para ver los datos de este turista");
            }
        }

        TuristaDTO turista = turistaService.obtenerPorDni(dni);
        return ResponseEntity.ok(turista);
    }

    @GetMapping("/grupo-familiar/{dni}")
    public ResponseEntity<List<TuristaDTO>> obtenerGrupoFamiliar(@PathVariable("dni") String dni, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        if (usuario.getRol() == Rol.CLIENTE) {
            // El cliente solo puede consultar su propio grupo familiar
            List<TuristaDTO> miFamilia = turistaService.obtenerGrupoFamiliar(usuario.getDni());
            boolean coincide = false;
            for (int i = 0; i < miFamilia.size(); i++) {
                if (miFamilia.get(i).getDni().equals(dni)) {
                    coincide = true;
                    break;
                }
            }
            if (!coincide) {
                throw new ForbiddenException("No tiene permisos para consultar un grupo familiar ajeno");
            }
            return ResponseEntity.ok(miFamilia);
        }

        List<TuristaDTO> familia = turistaService.obtenerGrupoFamiliar(dni);
        return ResponseEntity.ok(familia);
    }

    @PostMapping
    public ResponseEntity<TuristaDTO> crear(@Valid @RequestBody TuristaDTO dto, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();

        // Si es CLIENTE agregando familiar, el titularId debe ser el del propio cliente
        if (usuario.getRol() == Rol.CLIENTE) {
            TuristaDTO propio = turistaService.obtenerPorDni(usuario.getDni());
            Long titularId = propio.getTitularId() != null ? propio.getTitularId() : propio.getId();
            dto.setTitularId(titularId);
        }

        TuristaDTO creado = turistaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TuristaDTO> actualizar(@PathVariable("id") Long id, @Valid @RequestBody TuristaDTO dto, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        if (usuario.getRol() == Rol.CLIENTE) {
            // El cliente solo puede modificar datos de su grupo familiar
            List<TuristaDTO> familia = turistaService.obtenerGrupoFamiliar(usuario.getDni());
            boolean pertenece = false;
            for (int i = 0; i < familia.size(); i++) {
                if (familia.get(i).getId().equals(id)) {
                    pertenece = true;
                    break;
                }
            }
            if (!pertenece) {
                throw new ForbiddenException("No puede modificar un turista ajeno a su grupo familiar");
            }
        }

        TuristaDTO actualizado = turistaService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        if (usuario.getRol() == Rol.CLIENTE) {
            throw new ForbiddenException("Los clientes no tienen permiso para eliminar turistas");
        }

        turistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

