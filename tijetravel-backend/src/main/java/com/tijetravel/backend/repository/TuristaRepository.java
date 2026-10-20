package com.tijetravel.backend.repository;

import com.tijetravel.backend.model.Turista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TuristaRepository extends JpaRepository<Turista, Long> {

    Optional<Turista> findByCodigo(String codigo);

    Optional<Turista> findByDni(String dni);

    boolean existsByDni(String dni);

    boolean existsByCodigo(String codigo);

    List<Turista> findByTitularId(Long titularId);

    @Query("SELECT t FROM Turista t WHERE t.id = :titularId OR t.titular.id = :titularId ORDER BY t.apellido ASC, t.nombre ASC")
    List<Turista> findGrupoFamiliar(@Param("titularId") Long titularId);

    @Query("SELECT t FROM Turista t WHERE " +
           "(:codigo IS NULL OR LOWER(t.codigo) LIKE LOWER(CONCAT('%', :codigo, '%'))) AND " +
           "(:dni IS NULL OR t.dni LIKE CONCAT('%', :dni, '%')) AND " +
           "(:nombre IS NULL OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(t.apellido) LIKE LOWER(CONCAT('%', :nombre, '%')))")
    Page<Turista> buscarTuristas(@Param("codigo") String codigo,
                                @Param("dni") String dni,
                                @Param("nombre") String nombre,
                                Pageable pageable);
}

