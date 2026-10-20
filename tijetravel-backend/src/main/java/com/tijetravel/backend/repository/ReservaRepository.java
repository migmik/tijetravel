package com.tijetravel.backend.repository;

import com.tijetravel.backend.model.EstadoReserva;
import com.tijetravel.backend.model.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByTuristaIdAndVueloId(Long turistaId, Long vueloId);

    boolean existsByTuristaId(Long turistaId);

    boolean existsByVueloId(Long vueloId);

    boolean existsByHotelId(Long hotelId);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.vuelo.id = :vueloId AND r.claseVuelo = :clase")
    long countReservasPorVueloYClase(@Param("vueloId") Long vueloId, @Param("clase") com.tijetravel.backend.model.ClaseVuelo clase);

    @Query("SELECT r FROM Reserva r WHERE " +
           "(r.turista.id = :titularId OR r.turista.titular.id = :titularId) " +
           "ORDER BY r.id DESC")
    Page<Reserva> findReservasGrupoFamiliar(@Param("titularId") Long titularId, Pageable pageable);

    @Query("SELECT r FROM Reserva r WHERE " +
           "(:codigoTurista IS NULL OR LOWER(r.turista.codigo) LIKE LOWER(CONCAT('%', :codigoTurista, '%'))) AND " +
           "(:dni IS NULL OR r.turista.dni LIKE CONCAT('%', :dni, '%')) AND " +
           "(:numeroVuelo IS NULL OR LOWER(r.vuelo.numero) LIKE LOWER(CONCAT('%', :numeroVuelo, '%'))) AND " +
           "(:codigoHotel IS NULL OR LOWER(r.hotel.codigo) LIKE LOWER(CONCAT('%', :codigoHotel, '%'))) AND " +
           "(:fechaLlegadaDesde IS NULL OR r.fechaLlegada >= :fechaLlegadaDesde) AND " +
           "(:fechaLlegadaHasta IS NULL OR r.fechaLlegada <= :fechaLlegadaHasta) AND " +
           "(:estado IS NULL OR r.estado = :estado)")
    Page<Reserva> buscarReservas(@Param("codigoTurista") String codigoTurista,
                                 @Param("dni") String dni,
                                 @Param("numeroVuelo") String numeroVuelo,
                                 @Param("codigoHotel") String codigoHotel,
                                 @Param("fechaLlegadaDesde") LocalDate fechaLlegadaDesde,
                                 @Param("fechaLlegadaHasta") LocalDate fechaLlegadaHasta,
                                 @Param("estado") EstadoReserva estado,
                                 Pageable pageable);

    @Query("SELECT COUNT(r) AS cantidad, " +
           "COALESCE(SUM(r.precioTotal), 0) AS total, " +
           "COALESCE(SUM(r.precioVueloCapturado), 0) AS totalVuelo, " +
           "COALESCE(SUM(r.precioTotal - r.precioVueloCapturado), 0) AS totalHotel " +
           "FROM Reserva r WHERE r.estado = :estado")
    BalanceResumenProjection obtenerBalancePorEstado(@Param("estado") EstadoReserva estado);
}
