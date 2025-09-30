// src/main/java/uniandes/edu/co/proyecto/repositorio/ReseniaRepository.java
package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import uniandes.edu.co.proyecto.modelo.Resenia;

public interface ReseniaRepository extends JpaRepository<Resenia, Long> {

  @Query(value = "SELECT COUNT(1) FROM viaje WHERE id_viaje = :id", nativeQuery = true)
  int countViaje(@Param("id") Long idViaje);

  // Para evitar violar UQ_RES_UNICA (típicamente (id_viaje, autor_rol))
  @Query(value = "SELECT COUNT(1) FROM resenia WHERE id_viaje = :idViaje AND UPPER(autor_rol) = UPPER(:autorRol)", nativeQuery = true)
  int countPorViajeYRol(@Param("idViaje") Long idViaje, @Param("autorRol") String autorRol);

  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO resenia
        (id_resenia, calificacion, comentario, fecha, id_viaje, autor_rol)
      VALUES
        (:idResenia, :calificacion, :comentario,
         TO_DATE(:fecha,'YYYY-MM-DD'), :idViaje, :autorRol)
      """, nativeQuery = true)
  void insertarResenia(@Param("idResenia") Long idResenia,
                       @Param("calificacion") Integer calificacion,
                       @Param("comentario") String comentario,
                       @Param("fecha") String fecha,
                       @Param("idViaje") Long idViaje,
                       @Param("autorRol") String autorRol);
}
