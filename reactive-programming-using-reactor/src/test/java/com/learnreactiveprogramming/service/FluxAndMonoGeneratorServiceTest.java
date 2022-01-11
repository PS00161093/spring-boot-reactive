package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @Test
    void testNamesFluxFilter() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFilter(4);
        StepVerifier
                .create(namesFlux)
                .expectNext("Alex")
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5})
    void testNamesFluxFilterWithSize(int nameLength) {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFilter(nameLength);
        StepVerifier
                .create(namesFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

}