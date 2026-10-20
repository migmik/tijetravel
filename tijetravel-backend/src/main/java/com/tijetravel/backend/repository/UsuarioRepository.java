package com.tijetravel.backend.repository;

import com.tijetravel.backend.model.Rol;
import com.tijetravel.backend.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByDni(String dni);

    boolean existsByUsername(String username);

    boolean existsByDni(String dni);

    @Query("SELECT u FROM Usuario u WHERE " +
           "(:codigo IS NULL OR LOWER(u.codigo) LIKE LOWER(CONCAT('%', :codigo, '%'))) AND " +
           "(:dni IS NULL OR u.dni LIKE CONCAT('%', :dni, '%')) AND " +
           "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:rol IS NULL OR u.rol = :rol)")
    Page<Usuario> buscarUsuarios(@Param("codigo") String codigo,
                                 @Param("dni") String dni,
                                 @Param("username") String username,
                                 @Param("rol") Rol rol,
                                 Pageable pageable);
}

