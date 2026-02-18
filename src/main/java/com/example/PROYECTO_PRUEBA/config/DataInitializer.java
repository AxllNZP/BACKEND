package com.example.PROYECTO_PRUEBA.config;

import com.example.PROYECTO_PRUEBA.model.FormaPago;
import com.example.PROYECTO_PRUEBA.model.FormasPagos;
import com.example.PROYECTO_PRUEBA.repository.IFormasPagoRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IFormasPagoRepository formaPagoRepository;

    @Override
    public void run(String @NonNull ... args) {

        if (formaPagoRepository.count() == 0) {

            for (FormasPagos formaEnum : FormasPagos.values()) {

                FormaPago formaPago = new FormaPago();
                formaPago.setNombre(formaEnum);
                formaPago.setDescripcion("Forma de pago: " + formaEnum.name());

                formaPagoRepository.save(formaPago);
            }

            System.out.println("✔ Formas de pago inicializadas correctamente");
        }
    }
}
