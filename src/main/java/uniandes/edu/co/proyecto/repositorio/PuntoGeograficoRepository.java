// src/main/java/uniandes/edu/co/proyecto/repositorio/PuntoGeograficoRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import uniandes.edu.co.proyecto.modelo.PuntoGeografico;

public interface PuntoGeograficoRepository extends JpaRepository<PuntoGeografico, Long> {

  @Query(value = """
      SELECT COUNT(1)
      FROM PUNTO_GEOGRAFICO
      WHERE UPPER(NOMBRE) = UPPER(:nombre)
        AND UPPER(DIRECCION) = UPPER(:direccion)
        AND ID_CIUDAD = :idCiudad
      """, nativeQuery = true)
  int countByNombreDireccionCiudad(@Param("nombre") String nombre,
                                   @Param("direccion") String direccion,
                                   @Param("idCiudad") Long idCiudad);
}
