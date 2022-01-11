package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void testNamesFluxWithSize() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier
                .create(namesFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testNamesFluxWithData() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier
                .create(namesFlux)
                .expectNext("Alex", "Ben", "Chloe")
                .verifyComplete();
    }

    @Test
    void testNamesFluxWithCountAndData() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier
                .create(namesFlux)
                .expectNext("Alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testNamesFluxMap() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxMap();
        StepVerifier
                .create(namesFlux)
                .expectNext("ALEX")
                .expectNext("BEN")
                .expectNextCount(1)
                .verifyComplete();
    }
}