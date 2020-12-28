package com.github.lbovolini.notify.validation;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;

public abstract class AbstractValidationHandler<T, U extends Validator> {

    private final Class<T> validationClass;

    private final U validator;

    protected AbstractValidationHandler(Class<T> clazz, U validator) {
        this.validationClass = clazz;
        this.validator = validator;
    }

    public void validate(T t) {
        // !todo verificar Default.class
        Set<ConstraintViolation<T>> constraintViolations = this.validator.validate(t, Default.class);

        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}