// src/main/java/uniandes/edu/co/proyecto/repositorio/VehiculoRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uniandes.edu.co.proyecto.modelo.Vehiculo;

import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

  // Unicidad de placa (útil para RF de registro de vehículo)
  boolean existsByPlacaIgnoreCase(String placa);

  // Listar vehículos de un conductor (útil para verificaciones/administración)
  List<Vehiculo> findByUsuarioConductor_IdUsuarioConductor(Long idUsuarioConductor);

  // (Opcional) Conteo por placa con SQL nativa si prefieres mantener estilo
  @Query(value = "SELECT COUNT(1) FROM VEHICULO WHERE UPPER(PLACA) = UPPER(:placa)", nativeQuery = true)
  int countByPlaca(@Param("placa") String placa);
}
