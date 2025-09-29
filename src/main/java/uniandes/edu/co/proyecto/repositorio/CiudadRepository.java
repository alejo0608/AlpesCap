package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import uniandes.edu.co.proyecto.modelo.Ciudad;

public interface CiudadRepository extends JpaRepository<Ciudad, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO ciudad (id_ciudad, nombre) VALUES (:idCiudad, :nombre)", nativeQuery = true)
    void insertarCiudad(
        @Param("idCiudad") Long idCiudad,
        @Param("nombre") String nombre
    );
}