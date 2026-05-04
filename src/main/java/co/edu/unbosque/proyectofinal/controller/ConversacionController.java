package co.edu.unbosque.proyectofinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.proyectofinal.dto.ConversacionDTO;
import co.edu.unbosque.proyectofinal.dto.CrearConversacionDTO;
import co.edu.unbosque.proyectofinal.service.ConversacionService;


@RestController
@RequestMapping("/conversaciones")
@CrossOrigin(origins = {"http://localhost:8081", "*"})
public class ConversacionController {

	


	    @Autowired
	    private ConversacionService conversacionService;

	    @PostMapping
	    public ResponseEntity<String> crear(@RequestBody CrearConversacionDTO dto) {

	        int status = conversacionService.create(dto);

	        if (status == 0) {
	            return new ResponseEntity<>("Conversación creada correctamente", HttpStatus.CREATED);
	        } else {
	            return new ResponseEntity<>("Error al crear la conversación", HttpStatus.BAD_REQUEST);
	        }
	    }

	    @GetMapping
	    public ResponseEntity<List<ConversacionDTO>> getAll() {
	        return new ResponseEntity<>(conversacionService.getAll(), HttpStatus.OK);
	    }

	    @GetMapping("/{id}")
	    public ResponseEntity<ConversacionDTO> getById(@PathVariable Long id) {

	        ConversacionDTO dto = conversacionService.getById(id);

	        if (dto != null) {
	            return new ResponseEntity<>(dto, HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }

	    @DeleteMapping("/{id}")
	    public ResponseEntity<String> delete(@PathVariable Long id) {

	        int status = conversacionService.deleteById(id);

	        if (status == 0) {
	            return new ResponseEntity<>("Conversación eliminada", HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>("No se encontró la conversación", HttpStatus.NOT_FOUND);
	        }
	    }
	
	
}
