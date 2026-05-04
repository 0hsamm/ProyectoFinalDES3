package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.proyectofinal.dto.CrearMensajeDTO;
import co.edu.unbosque.proyectofinal.dto.MensajeDTO;
import co.edu.unbosque.proyectofinal.service.MensajeService;

@RestController
@RequestMapping("/mensajes")
@CrossOrigin(origins = {"http://localhost:8081", "*"})
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    // Enviar mensaje
    @PostMapping
    public int enviarMensaje(@RequestBody CrearMensajeDTO dto) {
    	return mensajeService.create(dto);
    }

    // Obtener todos los mensajes
    @GetMapping
    public List<MensajeDTO> obtenerTodos() {
        return mensajeService.getAll();
    }

    // Obtener mensajes por conversación
    @GetMapping("/conversacion/{id}")
    public List<MensajeDTO> obtenerPorConversacion(@PathVariable Long id) {
    	return mensajeService.getMensajesPorConversacion(id);
    }

    // Obtener último historial (ej: últimos 20)
    @GetMapping("/conversacion/{id}/recientes")
    public List<MensajeDTO> obtenerRecientes(@PathVariable Long id) {
    	return mensajeService.getUltimosMensajes(id);
    }

    // Eliminar mensaje
    @DeleteMapping("/{id}")
    public int eliminar(@PathVariable Long id) {
        return mensajeService.deleteById(id);
    }
}