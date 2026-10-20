package com.tijetravel.backend.repository;

import com.tijetravel.backend.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    Optional<Sucursal> findByCodigo(String codigo);

    List<Sucursal> findByCiudadIgnoreCase(String ciudad);

    @Query("SELECT DISTINCT s.ciudad FROM Sucursal s ORDER BY s.ciudad ASC")
    List<String> findDistinctCiudades();
}

