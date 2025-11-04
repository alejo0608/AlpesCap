// src/main/java/uniandes/edu/co/proyecto/repositorio/VehiculoRepository.java
package uniandes.edu.co.proyecto.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uniandes.edu.co.proyecto.modelo.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

  @Query(value = "SELECT COUNT(1) FROM VEHICULO WHERE PLACA = :placa", nativeQuery = true)
  int countByPlaca(@Param("placa") String placa);

  @Query(value = "SELECT COUNT(1) FROM USUARIO_CONDUCTOR WHERE ID_USUARIO_CONDUCTOR = :id", nativeQuery = true)
  int countConductor(@Param("id") Long idConductor);

  @Query(value = "SELECT COUNT(1) FROM CIUDAD WHERE ID_CIUDAD = :id", nativeQuery = true)
  int countCiudad(@Param("id") Long idCiudad);

  @Override
  Optional<Vehiculo> findById(Long id);

  @Modifying @Transactional
  @Query(value = """
      INSERT INTO VEHICULO
        (ID_VEHICULO, TIPO, MARCA, MODELO, COLOR, PLACA, CAPACIDAD, ID_USUARIO_CONDUCTOR, ID_CIUDAD_EXPEDICION)
      VALUES
        (:idVehiculo, :tipo, :marca, :modelo, :color, :placa, :capacidad, :idUsuarioConductor, :idCiudadExpedicion)
      """, nativeQuery = true)
  void insertarVehiculo(@Param("idVehiculo") Long idVehiculo,
                        @Param("tipo") String tipo,
                        @Param("marca") String marca,
                        @Param("modelo") String modelo,
                        @Param("color") String color,
                        @Param("placa") String placa,
                        @Param("capacidad") Integer capacidad,
                        @Param("idUsuarioConductor") Long idUsuarioConductor,
                        @Param("idCiudadExpedicion") Long idCiudadExpedicion);
}
