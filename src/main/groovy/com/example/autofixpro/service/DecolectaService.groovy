
package com.example.autofixpro.service

import com.example.autofixpro.dto.VehiculoDTO
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class DecolectaService {

    @Value('''${decolecta.api.token}''')
    private String apiToken

    private final RestTemplate restTemplate = new RestTemplate()

    VehiculoDTO consultarPlaca(String placa) {
        def url = "https://api.decolecta.com/sunarp/placa?placa=$placa"
        def headers = new HttpHeaders()
        headers.set('''Authorization''', "'''Bearer $apiToken'''")
        def entity = new HttpEntity<>(headers)

        def response = restTemplate.exchange(url, HttpMethod.GET, entity, String)
        def json = new JsonSlurper().parseText(response.body)

        if (json.success) {
            def data = json.data
            return new VehiculoDTO(
                placa: data.placa,
                marca: data.marca,
                modelo: data.modelo,
                color: data.color,
                anio_fabricacion: data.anio_fabricacion,
                serie: data.serie,
                vin: data.vin,
                motor: data.motor
            )
        } else {
            // Manejar el caso de error, por ejemplo, lanzando una excepción
            throw new RuntimeException("Error al consultar la placa: ${json.message}")
        }
    }
}
