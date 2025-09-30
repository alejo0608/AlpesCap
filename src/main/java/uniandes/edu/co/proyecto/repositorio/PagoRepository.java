package uniandes.edu.co.proyecto.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import uniandes.edu.co.proyecto.modelo.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

  /* Validaciones previas */
  @Query(value = "SELECT COUNT(1) FROM viaje WHERE id_viaje = :id", nativeQuery = true)
  int countViaje(@Param("id") Long idViaje);

  @Query(value = "SELECT COUNT(1) FROM pago WHERE id_viaje = :id", nativeQuery = true)
  int countPagoPorViaje(@Param("id") Long idViaje);

  /* Inserción nativa */
  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO pago
        (id_pago, monto, fecha, estado, id_viaje)
      VALUES
        (:idPago, :monto, TO_DATE(:fecha,'YYYY-MM-DD'), :estado, :idViaje)
      """, nativeQuery = true)
  void insertarPago(@Param("idPago") Long idPago,
                    @Param("monto") Double monto,
                    @Param("fecha") String fecha,     // "YYYY-MM-DD"
                    @Param("estado") String estado,   // deja la BD validar el CHECK de estado
                    @Param("idViaje") Long idViaje);
}
