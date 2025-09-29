package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import uniandes.edu.co.proyecto.modelo.PuntoGeografico;

public interface PuntoGeograficoRepository extends JpaRepository<PuntoGeografico, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO punto_geografico " +
                   "(id_punto, nombre, latitud, longitud, direccion, id_ciudad) " +
                   "VALUES (:idPunto, :nombre, :latitud, :longitud, :direccion, :idCiudad)",
           nativeQuery = true)
    void insertarPunto(
        @Param("idPunto") Long idPunto,
        @Param("nombre") String nombre,
        @Param("latitud") Double latitud,
        @Param("longitud") Double longitud,
        @Param("direccion") String direccion,
        @Param("idCiudad") Long idCiudad
    );
}