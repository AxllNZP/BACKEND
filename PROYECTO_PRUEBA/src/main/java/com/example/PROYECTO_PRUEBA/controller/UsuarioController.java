package com.example.PROYECTO_PRUEBA.controller;

import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import com.example.PROYECTO_PRUEBA.model.Productos;
import com.example.PROYECTO_PRUEBA.model.RolUsuario;
import com.example.PROYECTO_PRUEBA.model.Usuario;
import com.example.PROYECTO_PRUEBA.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final IUsuarioService iUsuarioService;

    // GET: Listar todos
    // URL: GET http://localhost:8080/api/usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(iUsuarioService.listarTodos());
    }

    // GET: Buscar por ID
    // http://localhost:8080/api/usuarios/1
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return iUsuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Listar por estado
    // http://localhost:8080/api/usuarios/estado/activo
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Usuario>> listarPorEstado(@PathVariable EstadoGeneral estado) {
        return ResponseEntity.ok(iUsuarioService.listarPorEstado(estado));
    }

    // GET: Listar por rol
    // http://localhost:8080/api/usuarios/rol/ADMIN
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<Usuario>> listarPorRol(@PathVariable RolUsuario rol) {
        return ResponseEntity.ok(iUsuarioService.listarPorRol(rol));
    }

    // GET: Buscar por nombre de usuario
    // http://localhost:8080/api/usuarios/username/juan123
    @GetMapping("/username/{nombreUsuario}")
    public ResponseEntity<Usuario> buscarPorNombreUsuario(@PathVariable String nombreUsuario) {
        return iUsuarioService.buscarPorNombreUsuario(nombreUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Buscar por email
    // http://localhost:8080/api/usuarios/email/{email}
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        return iUsuarioService.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    //POST: GUARDAR USUARIO
    @PostMapping
    public ResponseEntity<Usuario> guardarusuario(@RequestBody Usuario usuario) {
        Usuario guardarusuario = iUsuarioService.guardarusuario(usuario);
        return ResponseEntity.status(201).body(guardarusuario);
    }

    // PUT: Actualizar usuario
// URL: PUT http://localhost:8080/api/usuarios/1
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = iUsuarioService.actualizarusuario(id, usuario);

        if (usuarioActualizado != null) {
            return ResponseEntity.ok(usuarioActualizado);
        }

        return ResponseEntity.notFound().build();
    }


    //DELETE http://localhost:8080/api/usuarios/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarusuario(@PathVariable long id){
        Optional<Usuario> usuario =iUsuarioService.buscarPorId(id);
        if(usuario.isPresent()){
            iUsuarioService.eliminarusuario(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}