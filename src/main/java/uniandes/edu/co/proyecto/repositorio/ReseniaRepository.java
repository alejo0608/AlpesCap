package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Resenia;

public interface ReseniaRepository extends JpaRepository<Resenia, Long> {

  @Query(value = "SELECT COUNT(1) FROM viaje WHERE id_viaje = :id", nativeQuery = true)
  int countViaje(@Param("id") Long idViaje);

  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO resenia
        (id_resenia, calificacion, comentario, fecha, id_viaje)
      VALUES
        (:idResenia, :calificacion, :comentario, TO_DATE(:fecha,'YYYY-MM-DD'), :idViaje)
      """, nativeQuery = true)
  void insertarResenia(@Param("idResenia") Long idResenia,
                       @Param("calificacion") Integer calificacion,
                       @Param("comentario") String comentario,   // puede ser null
                       @Param("fecha") String fecha,             // "YYYY-MM-DD"
                       @Param("idViaje") Long idViaje);
}
