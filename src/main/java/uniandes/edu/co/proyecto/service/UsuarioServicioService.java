// src/main/java/uniandes/edu/co/proyecto/service/UsuarioServicioService.java
package uniandes.edu.co.proyecto.service;

import uniandes.edu.co.proyecto.modelo.UsuarioServicio;

public interface UsuarioServicioService {
  UsuarioServicio registrar(Long id, String nombre, String correo, String telefono, String cedula);
}
