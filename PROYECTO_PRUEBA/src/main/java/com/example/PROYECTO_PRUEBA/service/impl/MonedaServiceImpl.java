package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.model.Moneda;
import com.example.PROYECTO_PRUEBA.repository.IMonedaRepository;
import com.example.PROYECTO_PRUEBA.service.IMonedaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MonedaServiceImpl implements IMonedaService {

    private final IMonedaRepository iMonedaRepository;

    @Override
    public List<Moneda> listarTodos() {
        return iMonedaRepository.findAll();
    }

    @Override
    public Optional<Moneda> buscarPorId(Long id) {
        return iMonedaRepository.findById(id);
    }

    @Override
    public Optional<Moneda> buscarPorCodigo(String codigo) {
        return iMonedaRepository.buscarPorCodigo(codigo);
    }


    @Override
    public Moneda guardar(Moneda moneda) {
        return iMonedaRepository.save(moneda);
    }

    @Override
    public Moneda actualizar(Long id, Moneda moneda) {
        Moneda existente = iMonedaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moneda no encontrado"));

        existente.setNombre(moneda.getNombre());
        existente.setSimbolo(moneda.getSimbolo());
        existente.setSimbolo(moneda.getSimbolo());
        existente.setCodigo(moneda.getCodigo());

        return iMonedaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        iMonedaRepository.deleteById(id);
    }
}
