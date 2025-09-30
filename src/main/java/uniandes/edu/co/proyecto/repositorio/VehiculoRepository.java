package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import uniandes.edu.co.proyecto.modelo.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

  @Query(value = "SELECT COUNT(1) FROM vehiculo WHERE placa = :placa", nativeQuery = true)
  int countByPlaca(@Param("placa") String placa);

  @Override
  Optional<Vehiculo> findById(Long id);

  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO vehiculo
        (id_vehiculo, tipo, marca, modelo, color, placa, capacidad, nivel,
         id_usuario_conductor, id_ciudad_expedicion)
      VALUES
        (:idVehiculo, :tipo, :marca, :modelo, :color, :placa, :capacidad, :nivel,
         :idUsuarioConductor, :idCiudadExpedicion)
      """, nativeQuery = true)
  void insertarVehiculo(@Param("idVehiculo") Long idVehiculo,
                        @Param("tipo") String tipo,
                        @Param("marca") String marca,
                        @Param("modelo") String modelo,
                        @Param("color") String color,
                        @Param("placa") String placa,
                        @Param("capacidad") Integer capacidad,
                        @Param("nivel") String nivel,
                        @Param("idUsuarioConductor") Long idUsuarioConductor,
                        @Param("idCiudadExpedicion") Long idCiudadExpedicion);
}
