package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.model.Pago;
import com.example.PROYECTO_PRUEBA.repository.IFormasPagoRepository;
import com.example.PROYECTO_PRUEBA.repository.IPagoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PagoServiceImpl {

    @Autowired
    private IPagoRepository iPagoRepository;

    @Autowired
    private IFormasPagoRepository iFormasPagoRepository;


}
