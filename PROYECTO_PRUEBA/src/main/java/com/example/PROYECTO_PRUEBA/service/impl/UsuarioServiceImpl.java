package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import com.example.PROYECTO_PRUEBA.model.Productos;
import com.example.PROYECTO_PRUEBA.model.RolUsuario;
import com.example.PROYECTO_PRUEBA.model.Usuario;
import com.example.PROYECTO_PRUEBA.repository.IUsuarioRepository;
import com.example.PROYECTO_PRUEBA.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository iUsuarioRepository;

    @Override
    public List<Usuario> listarTodos() {
        return iUsuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return iUsuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> listarPorEstado(EstadoGeneral estado) {
        return iUsuarioRepository.busqestado(estado);
    }

    @Override
    public List<Usuario> listarPorRol(RolUsuario rol) {
        return iUsuarioRepository.busqrol(rol);
    }

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return iUsuarioRepository.busqusuario(nombreUsuario);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return iUsuarioRepository.busqemail(email);
    }

    @Override
    public Usuario guardarusuario(Usuario usuario) {
        return iUsuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizarusuario(Long id, Usuario usuario) {
        // Verificar si existe el usuario
        Optional<Usuario> usuarioExistente = iUsuarioRepository.findById(id);

        if (usuarioExistente.isPresent()) {
            Usuario usuarioActualizado = usuarioExistente.get();

            // Actualizar los campos
            usuarioActualizado.setNombreUsuario(usuario.getNombreUsuario());
            usuarioActualizado.setClave(usuario.getClave());
            usuarioActualizado.setNombreCompleto(usuario.getNombreCompleto());
            usuarioActualizado.setEmail(usuario.getEmail());
            usuarioActualizado.setRol(usuario.getRol());
            usuarioActualizado.setEstado(usuario.getEstado());

            // Guardar en BD
            return iUsuarioRepository.save(usuarioActualizado);
        }

        return null; // Si no existe, retorna null
    }

    @Override
    public void eliminarusuario(long id) {
        iUsuarioRepository.deleteById(id);
    }


}