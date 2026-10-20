package com.tijetravel.backend.repository;

import com.tijetravel.backend.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Optional<Hotel> findByCodigo(String codigo);

    Optional<Hotel> findByNombreAndCiudad(String nombre, String ciudad);

    boolean existsByNombreAndCiudad(String nombre, String ciudad);

    @Query("SELECT DISTINCT h.nombre FROM Hotel h ORDER BY h.nombre ASC")
    List<String> findDistinctNombres();

    @Query("SELECT DISTINCT h.ciudad FROM Hotel h ORDER BY h.ciudad ASC")
    List<String> findDistinctCiudades();

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.hotel.id = :hotelId AND r.fechaLlegada <= :dia AND r.fechaPartida > :dia")
    long countOcupacionEnFecha(@Param("hotelId") Long hotelId, @Param("dia") LocalDate dia);

    @Query("SELECT h FROM Hotel h WHERE " +
           "(:ciudad IS NULL OR LOWER(h.ciudad) = LOWER(:ciudad)) AND " +
           "(:nombre IS NULL OR LOWER(h.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))")
    Page<Hotel> buscarHoteles(@Param("ciudad") String ciudad,
                             @Param("nombre") String nombre,
                             Pageable pageable);

    @Query("SELECT h FROM Hotel h WHERE LOWER(h.ciudad) = LOWER(:ciudad)")
    List<Hotel> findByCiudadIgnoreCase(@Param("ciudad") String ciudad);
}

