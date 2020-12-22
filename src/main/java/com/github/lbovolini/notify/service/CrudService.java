package com.github.lbovolini.notify.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudService<T> {

    Mono<?> delete(String id);

    Mono<T> find(String id);

    Flux<T> findAll();

    Mono<T> save(T t);

    Mono<T> update(T t);
}
