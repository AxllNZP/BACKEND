// src/main/java/com/example/PROYECTO_PRUEBA/service/impl/ClienteServiceImpl.java
package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.model.Cliente;
import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import com.example.PROYECTO_PRUEBA.repository.IClienteRepository;
import com.example.PROYECTO_PRUEBA.service.IClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements IClienteService {

    private final IClienteRepository clienteRepository;

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<Cliente> buscarPorNumeroDocumento(String numeroDocumento) {
        return clienteRepository.buscarPorNumeroDocumento(numeroDocumento);
    }

    @Override
    public List<Cliente> buscarPorEstado(EstadoGeneral estado) {
        return clienteRepository.buscarPorEstado(estado);
    }

    @Override
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.buscarPorNombre(nombre);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente actualizar(Long id, Cliente cliente) {

        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // VALIDAR DOCUMENTO SOLO SI CAMBIA
        if (!existente.getNumeroDocumento().equals(cliente.getNumeroDocumento())) {
            clienteRepository.buscarPorNumeroDocumento(cliente.getNumeroDocumento())
                    .ifPresent(c -> {
                        throw new RuntimeException("El número de documento ya existe");
                    });
        }

        existente.setTipoDocumento(cliente.getTipoDocumento());
        existente.setNumeroDocumento(cliente.getNumeroDocumento());
        existente.setNombreRazonSocial(cliente.getNombreRazonSocial());
        existente.setDireccion(cliente.getDireccion());
        existente.setTelefono(cliente.getTelefono());
        existente.setEmail(cliente.getEmail());
        existente.setEstado(cliente.getEstado());

        return clienteRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}