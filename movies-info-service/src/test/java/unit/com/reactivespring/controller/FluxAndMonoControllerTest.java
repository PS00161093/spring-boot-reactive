package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux() {
        webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(3);
    }

    @Test
    void fluxResponse() {
        var fluxResponse = webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier
                .create(fluxResponse)
                .expectNext(1)
                .expectNext(2)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void fluxResponseWithConsume() {
        webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(fluxResponse -> {
                    var response = fluxResponse.getResponseBody();
                    assertNotNull(response);
                    assertEquals(3, response.size());
                    assertEquals(1, response.get(0));
                    assertEquals(2, response.get(1));
                    assertEquals(3, response.get(2));
                });

    }

    @Test
    void testMono() {
        webTestClient
                .get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(fluxResponse -> {
                    var response = fluxResponse.getResponseBody();
                    assertEquals("hello-world", response);
                });

    }

    @Test
    void testStream() {
        var infiniteFlux =
                webTestClient
                        .get()
                        .uri("/stream")
                        .exchange()
                        .expectStatus()
                        .is2xxSuccessful()
                        .returnResult(Long.class)
                        .getResponseBody();

        StepVerifier
                .create(infiniteFlux)
                .expectNext(0L, 1L, 2L)
                .thenCancel()
                .verify();
    }

}