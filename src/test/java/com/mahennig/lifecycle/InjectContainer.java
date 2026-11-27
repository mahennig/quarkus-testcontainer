package com.mahennig.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test field that a {@link io.quarkus.test.common.QuarkusTestResourceLifecycleManager}
 * fills with the container it started (see {@code inject(TestInjector)}).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectContainer {
}
