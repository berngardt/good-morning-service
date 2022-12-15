package com.example.demo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Some examples of using Project reactor's Flux and Mono
 */
public class ReactTest {

    @Test
    public void empty() {
        Flux<String> flux = Flux.empty();

        StepVerifier.create(flux)
                .expectComplete()
                .verify();
    }

    @Test
    public void stringStream() {
        Flux<String> flux = Flux.fromIterable(Arrays.asList("abc", "qwe", "asd"));

        StepVerifier.create(flux)
                .expectNext("abc")
                .expectNext("qwe")
                .expectNext("asd")
                .verifyComplete();
    }

    @Test
    public void testError() {
        Flux<String> flux = thisIsRealGoodPublisher();

        StepVerifier.create(flux)
                .expectError(IllegalStateException.class)
                .verify();
    }

    public Flux<String> thisIsRealGoodPublisher() {
        return Flux.error(new IllegalStateException("test error"));
    }

    @Test
    public void testTake() {
        Flux<String> flux = Flux.fromStream(Stream.of("abc", "qwe", "asd", "zxc", "123")).take(2);

        StepVerifier.create(flux)
                .expectNext("abc")
                .expectNext("qwe")
                .verifyComplete();
    }

    @Test
    public void concat() {
        Flux<String> flux1 = Flux.fromStream(Stream.of("abc", "qwe", "asd", "zxc", "123")).take(2);
        Flux<String> flux2 = Flux.fromStream(Stream.of("abc", "qwe", "asd", "zxc", "123")).takeLast(1);

        Flux<String> flux = Flux.concat(flux1, flux2);

        StepVerifier.create(flux)
                .expectNext("abc")
                .expectNext("qwe")
                .expectNext("123")
                .verifyComplete();
    }

    @Test
    public void mono() {
        Mono<String> mono = Mono.just("foo");

        StepVerifier.create(mono)
                .expectNext("foo")
                .expectComplete()
                .verify();
    }

    @Test
    public void monoConcat() {
        Mono<String> fooMono = Mono.just("foo");
        Mono<String> emptyMono = Mono.empty();
        Flux<String> mono = Flux.concat(fooMono, emptyMono);

        StepVerifier.create(mono)
                .expectNext("foo")
                .expectComplete()
                .verify();
    }

    @Test
    public void monoWithEmpty() {
        Mono<String> fooMono = Mono.just("foo");
        Mono<String> emptyMono = Mono.empty();
        Flux<String> mono = Flux.concat(fooMono, emptyMono);

        StepVerifier.create(mono)
                .expectNext("foo")
                .expectComplete()
                .verify();
    }

    @Test
    public void monoWithError() {
        Mono<String> fooMono = Mono.just("foo");
        Mono<String> errorMono = Mono.error(new IllegalStateException("mono error"));
        Flux<String> mono = Flux.concat(fooMono, errorMono);

        StepVerifier.create(mono)
                .expectNext("foo")
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    public void testing() {
        //use next matcher and next count
        Flux<String> flux = Flux.just("foo", "bar", "max", "mus", "ter");

        StepVerifier.create(flux)
                .expectNextMatches(str -> str.equals("foo"))
                .expectNextCount(4)
                .expectComplete()
                .verify();
    }

    @Test
    public void testing_v2() {
        //request 1 el, then use thenRequest, then cancel
        //also log() can be used
        Flux<String> flux = Flux.just("foo", "bar", "max", "mus", "ter").log();

        StepVerifier.create(flux, 1)
                .expectNextMatches(str -> str.equals("foo"))
                .thenRequest(1)
                .expectNextMatches(str -> str.equals("bar"))
                .thenCancel()
                .verify();
    }

    @Test
    public void map() {
        Flux<Foo> flux = Flux.just(new Foo(1), new Foo(2));

        Flux<Bar> barFlux = flux.map(foo -> new Bar(foo.id.toString()));

        StepVerifier.create(barFlux)
                .expectNextMatches(bar -> bar.id.equals("1"))
                .expectNextMatches(bar -> bar.id.equals("2"))
                .expectComplete()
                .verify();
    }

    @Test
    public void flatMap() {
        //create a new method which returns publisher
        Flux<Foo> flux = Flux.just(new Foo(1), new Foo(2));

        Flux<Bar> barFlux = flux.flatMap(foo -> getBarMono(foo));

        StepVerifier.create(barFlux)
                .expectNextMatches(bar -> bar.id.equals("1"))
                .expectNextMatches(bar -> bar.id.equals("2"))
                .expectComplete()
                .verify();
    }

    public Mono<Bar> getBarMono(Foo foo) {
        return Mono.just(new Bar(foo.id.toString()));
    }

    @Test
    public void zip() {
        Flux<String> flux1 = Flux.just("a");
        Flux<String> flux2 = Flux.just("b");
        Flux<String> flux3 = Flux.just("c");

        Flux<Bar> barFlux = Flux.zip(flux1, flux2, flux3)
                .flatMap( tuple -> Flux.just(new Bar(tuple.getT1()), new Bar(tuple.getT2()), new Bar(tuple.getT3())));

        StepVerifier.create(barFlux)
                .expectNextMatches(bar -> bar.id.equals("a"))
                .expectNextMatches(bar -> bar.id.equals("b"))
                .expectNextMatches(bar -> bar.id.equals("c"))
                .expectComplete()
                .verify();
    }

    @Test
    public void doOn() {
        Flux<String> flux = Flux.fromIterable(Arrays.asList("abc", "qwe", "asd"))
                .doOnSubscribe(s -> System.out.println("doOnSubscribe"))
                .doOnNext(el -> System.out.println("onNext: " + el))
                .doOnComplete(() -> System.out.println("doOnComplete"));

        StepVerifier.create(flux)
                .expectNext("abc")
                .expectNext("qwe")
                .expectNext("asd")
                .verifyComplete();
    }

    @Test
    public void defaultIf() {
        Mono<String> emptyMono = Mono.empty();

        Mono<Bar> mono = emptyMono
                .map( value -> new Bar(value))
                .defaultIfEmpty(new Bar("default Bar"));

        StepVerifier.create(mono)
                .expectNextMatches(bar -> bar.id.equals("default Bar"))
                .verifyComplete();
    }

    @Test
    public void switchIf() {
        Mono<String> emptyMono = Mono.empty();

        Mono<Bar> mono = emptyMono
                .map( value -> new Bar(value))
                .switchIfEmpty(Mono.just(new Bar("default Bar")));

        StepVerifier.create(mono)
                .expectNextMatches(bar -> bar.id.equals("default Bar"))
                .verifyComplete();
    }
}
