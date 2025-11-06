// src/main/java/uniandes/edu/co/proyecto/service/impl/PuntoGeograficoServiceImpl.java
package uniandes.edu.co.proyecto.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
  

import uniandes.edu.co.proyecto.modelo.Ciudad;
import uniandes.edu.co.proyecto.modelo.PuntoGeografico;
import uniandes.edu.co.proyecto.repositorio.CiudadRepository;
import uniandes.edu.co.proyecto.repositorio.PuntoGeograficoRepository;
import uniandes.edu.co.proyecto.service.PuntoGeograficoService;

@Service
public class PuntoGeograficoServiceImpl implements PuntoGeograficoService {

  private final PuntoGeograficoRepository repo;
  private final CiudadRepository ciudadRepo;

  public PuntoGeograficoServiceImpl(PuntoGeograficoRepository repo, CiudadRepository ciudadRepo) {
    this.repo = repo;
    this.ciudadRepo = ciudadRepo;
  }

  @Override
  @Transactional
  public PuntoGeografico registrar(Long idPunto, String nombre, Double latitud, Double longitud,
      String direccion, Long idCiudad) {

    // 1) Validaciones de dominio (400)
    if (idPunto == null || idPunto <= 0)
      throw new IllegalArgumentException("idPunto requerido y positivo");
    if (nombre == null || nombre.trim().isEmpty())
      throw new IllegalArgumentException("nombre requerido");
    if (direccion == null || direccion.trim().isEmpty())
      throw new IllegalArgumentException("direccion requerida");
    if (latitud == null || latitud < -90.0 || latitud > 90.0)
      throw new IllegalArgumentException("latitud inválida [-90..90]");
    if (longitud == null || longitud < -180.0 || longitud > 180.0)
      throw new IllegalArgumentException("longitud inválida [-180..180]");
    if (idCiudad == null || idCiudad <= 0)
      throw new IllegalArgumentException("idCiudad requerido y positivo");

    String nom = nombre.trim();
    String dir = direccion.trim();

    // 2) Conflictos (409)
    if (repo.existsById(idPunto))
      throw new IllegalStateException("idPunto ya existe");
    // Si tu BD tiene UNIQUE sobre (NOMBRE,DIRECCION,ID_CIUDAD), prevenimos
    // violación:
    if (repo.countByNombreDireccionCiudad(nom, dir, idCiudad) > 0)
      throw new IllegalStateException("Ya existe un punto con el mismo nombre y dirección en esa ciudad");
    // Si tu BD tiene UNIQUE sobre (LATITUD,LONGITUD,ID_CIUDAD), prevenimos
    // violación:
    if (repo.countByCoordenadasCiudad(latitud, longitud, idCiudad) > 0)
      throw new IllegalStateException("Ya existe un punto con las mismas coordenadas en esa ciudad");

    // 3) FKs (404)
    Ciudad ciudad = ciudadRepo.findById(idCiudad)
        .orElseThrow(() -> new RuntimeException("Ciudad no existe: " + idCiudad));

    // 4) Persistencia
    PuntoGeografico p = new PuntoGeografico();
    p.setId(idPunto);
    p.setNombre(nom);
    p.setLatitud(latitud);
    p.setLongitud(longitud);
    p.setDireccion(dir);
    p.setCiudad(ciudad);

    return repo.save(p);
  }


  @Override
  @Transactional
  public PuntoGeografico registrarAuto(String nombre, Double lat, Double lon, String direccion, Long idCiudad) {
  
    if (nombre == null || nombre.trim().isEmpty())     throw new IllegalArgumentException("nombre requerido");
    if (direccion == null || direccion.trim().isEmpty()) throw new IllegalArgumentException("direccion requerida");
    if (lat == null || lat < -90.0 || lat > 90.0)      throw new IllegalArgumentException("latitud inválida [-90,90]");
    if (lon == null || lon < -180.0 || lon > 180.0)    throw new IllegalArgumentException("longitud inválida [-180,180]");
    if (idCiudad == null || idCiudad <= 0)             throw new IllegalArgumentException("idCiudad requerido y positivo");
  
    String nom = nombre.trim();
    String dir = direccion.trim();
  
    // Duplicado lógico por (nombre + dirección + ciudad)
    if (repo.countByNombreDireccionCiudad(nom, dir, idCiudad) > 0) {
      throw new IllegalStateException("Ya existe un punto con el mismo nombre y dirección en esa ciudad");
    }
    // Duplicado exacto de coordenadas en la misma ciudad
    if (repo.countByCoordenadasCiudad(lat, lon, idCiudad) > 0) {
      throw new IllegalStateException("Ya existe un punto con las mismas coordenadas en esa ciudad");
    }
  
    var ciudad = ciudadRepo.findById(idCiudad)
        .orElseThrow(() -> new RuntimeException("Ciudad no existe: " + idCiudad));
  
    var p = new PuntoGeografico();
    p.setNombre(nom);
    p.setLatitud(lat);
    p.setLongitud(lon);
    p.setDireccion(dir);
    p.setCiudad(ciudad);
  
    try {
      return repo.save(p); // ID se autogenera por SEQUENCE
    } catch (DataIntegrityViolationException ex) {
      throw new IllegalStateException("Punto duplicado (constraint DB): " + ex.getMostSpecificCause().getMessage());
    }
  }
  

}
