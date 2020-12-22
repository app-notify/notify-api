package com.github.lbovolini.notify.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudController<T> {

    Mono<ResponseEntity<?>> delete(@PathVariable(name = "id") String id);

    ResponseEntity<Mono<T>> find(@PathVariable(name = "id") String id);

    ResponseEntity<Flux<T>> findAll();

    Mono<ResponseEntity<String>> save(T t);

    Mono<ResponseEntity<String>> update(T t);
}
