package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import uniandes.edu.co.proyecto.modelo.UsuarioConductor;

public interface UsuarioConductorRepository extends JpaRepository<UsuarioConductor, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO usuario_conductor (id_usuario_conductor, nombre, correo, telefono, cedula, comision) " +
                   "VALUES (:idUsuarioConductor, :nombre, :correo, :telefono, :cedula, :comision)", nativeQuery = true)
    void insertarUsuarioConductor(
        @Param("idUsuarioConductor") Long idUsuarioConductor,
        @Param("nombre") String nombre,
        @Param("correo") String correo,
        @Param("telefono") String telefono,
        @Param("cedula") String cedula,
        @Param("comision") Double comision
    );
}