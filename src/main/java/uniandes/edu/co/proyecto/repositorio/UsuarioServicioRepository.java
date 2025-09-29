package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import uniandes.edu.co.proyecto.modelo.UsuarioServicio;

public interface UsuarioServicioRepository extends JpaRepository<UsuarioServicio, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO usuario_servicio (id_usuario_servicio, nombre, correo, telefono, cedula) " +
                   "VALUES (:idUsuarioServicio, :nombre, :correo, :telefono, :cedula)", nativeQuery = true)
    void insertarUsuarioServicio(
        @Param("idUsuarioServicio") Long idUsuarioServicio,
        @Param("nombre") String nombre,
        @Param("correo") String correo,
        @Param("telefono") String telefono,
        @Param("cedula") String cedula
    );
}