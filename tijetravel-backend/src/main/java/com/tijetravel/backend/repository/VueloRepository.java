package com.tijetravel.backend.repository;

import com.tijetravel.backend.model.ClaseVuelo;
import com.tijetravel.backend.model.Vuelo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VueloRepository extends JpaRepository<Vuelo, Long> {

    Optional<Vuelo> findByNumero(String numero);

    boolean existsByNumero(String numero);

    @Query("SELECT DISTINCT v.origen FROM Vuelo v ORDER BY v.origen ASC")
    List<String> findDistinctOrigenes();

    @Query("SELECT DISTINCT v.destino FROM Vuelo v ORDER BY v.destino ASC")
    List<String> findDistinctDestinos();

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.vuelo.id = :vueloId AND r.claseVuelo = :clase")
    long countReservasPorVueloYClase(@Param("vueloId") Long vueloId, @Param("clase") ClaseVuelo clase);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.vuelo.id = :vueloId")
    long countReservasTotalesPorVuelo(@Param("vueloId") Long vueloId);

    @Query("SELECT v FROM Vuelo v WHERE " +
           "LOWER(v.origen) = LOWER(:origen) AND " +
           "LOWER(v.destino) = LOWER(:destino) AND " +
           "v.fechaHora >= :desde AND v.fechaHora <= :hasta " +
           "ORDER BY v.precioBase ASC")
    List<Vuelo> buscarVuelosParaFecha(@Param("origen") String origen,
                                     @Param("destino") String destino,
                                     @Param("desde") LocalDateTime desde,
                                     @Param("hasta") LocalDateTime hasta);

    @Query("SELECT v FROM Vuelo v WHERE " +
           "LOWER(v.destino) = LOWER(:destino) AND " +
           "v.fechaHora >= :desde AND v.fechaHora <= :hasta " +
           "ORDER BY v.fechaHora ASC")
    List<Vuelo> sugerirVuelosDestino(@Param("destino") String destino,
                                    @Param("desde") LocalDateTime desde,
                                    @Param("hasta") LocalDateTime hasta);

    @Query("SELECT v FROM Vuelo v WHERE " +
           "(:origen IS NULL OR LOWER(v.origen) = LOWER(:origen)) AND " +
           "(:destino IS NULL OR LOWER(v.destino) = LOWER(:destino)) AND " +
           "(:desde IS NULL OR v.fechaHora >= :desde) AND " +
           "(:hasta IS NULL OR v.fechaHora <= :hasta)")
    Page<Vuelo> buscarVuelosFiltro(@Param("origen") String origen,
                                  @Param("destino") String destino,
                                  @Param("desde") LocalDateTime desde,
                                  @Param("hasta") LocalDateTime hasta,
                                  Pageable pageable);
}

