package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.model.FormaPago;
import com.example.PROYECTO_PRUEBA.model.FormasPagos;
import com.example.PROYECTO_PRUEBA.repository.IFormasPagoRepository;
import com.example.PROYECTO_PRUEBA.service.IFormaPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FormaPagoServiceImpl implements IFormaPagoService {

    private final IFormasPagoRepository iFormasPagoRepository;

    @Override
    public List<FormaPago> listarTodos() {
        return iFormasPagoRepository.findAll();
    }

    @Override
    public Optional<FormaPago> buscarPorId(Long id) {
        return iFormasPagoRepository.findById(id);
    }

    @Override
    public FormaPago guardar(FormaPago formaPago) {
        return iFormasPagoRepository.save(formaPago);
    }

    @Override
    public FormaPago actualizar(Long id, FormaPago formaPago) {
        FormaPago existente = iFormasPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        existente.setNombre(formaPago.getNombre());
        existente.setDescripcion(formaPago.getDescripcion());

        return iFormasPagoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        iFormasPagoRepository.deleteById(id);
    }
}
