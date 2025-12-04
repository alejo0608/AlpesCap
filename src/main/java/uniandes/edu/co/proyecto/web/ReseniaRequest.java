package uniandes.edu.co.proyecto.web;

public record ReseniaRequest(
    Long idViaje,
    Integer calificacion,
    String comentario) {}
