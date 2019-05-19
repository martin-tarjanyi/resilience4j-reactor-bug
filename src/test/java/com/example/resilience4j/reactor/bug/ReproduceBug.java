package com.example.resilience4j.reactor.bug;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

public class ReproduceBug
{
    @Test
    public void reactorContextShouldBeAvailable() // this test passes
    {
        Mono<String> stringMono = Mono.just("myData")
                                      .doOnEach(signal -> System.out.println(signal.getContext().<String>get("key")))
                                      .subscriberContext(Context.of("key", "value"));

        StepVerifier.create(stringMono)
                    .expectNext("myData")
                    .verifyComplete();
    }

    @Test
    public void reactorContextShouldBeAvailableWithResilience4jCircuitBreaker() // this test fails with java.util.NoSuchElementException: Context is empty
    {
        Mono<String> stringMono = Mono.just("myData")
                                      .doOnEach(signal -> System.out.println(signal.getContext().<String>get("key")))
                                      .compose(CircuitBreakerOperator.of(CircuitBreaker.ofDefaults("myRequest")))
                                      .subscriberContext(Context.of("key", "value"));

        StepVerifier.create(stringMono)
                    .expectNext("myData")
                    .verifyComplete();
    }
}
