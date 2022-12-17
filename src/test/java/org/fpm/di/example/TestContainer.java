package org.fpm.di.example;

import org.fpm.di.Configuration;
import org.fpm.di.Container;
import org.fpm.di.Environment;
import org.junit.Before;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestContainer {

    private Container container;
    private DummyBinder binder;

    @Before
    public void setUp() {
        Environment env = new DummyEnvironment();
        Configuration configuration = new MyConfiguration();
        container = env.configure(configuration);
        binder = new DummyBinder();
        configuration.configure(binder);
    }
    @Test
    public void shouldInjectSingleton() {
        assertSame(container.getComponent(MySingleton.class), container.getComponent(MySingleton.class));
    }

    @Test
    public void shouldInjectPrototype() {
        assertNotSame(container.getComponent(MyPrototype.class), container.getComponent(MyPrototype.class));
    }

    @Test
    public void shouldBuildInjectionGraph() {
        final B bAsSingleton = container.getComponent(B.class);
        assertSame(container.getComponent(A.class), bAsSingleton);
        assertSame(container.getComponent(B.class), bAsSingleton);
    }

    @Test
    public void shouldBuildInjectDependencies() {

        final UseA hasADependency = container.getComponent(UseA.class);
        assertSame(hasADependency.getDependency(), container.getComponent(B.class));

    }
    @Test
    public void bindAAndCheck() {
        binder.unBind(A.class, B.class);
        binder.bind(A.class);
        A aAsSingleton = container.getComponent(A.class);
        assertSame(container.getComponent(A.class), aAsSingleton);
        assertNotSame(container.getComponent(A.class), container.getComponent(B.class));
    }

    @Test
    public void bindAndCheckDependency() {
        binder.bind(A.class, new A());
        UseA useA = container.getComponent(UseA.class);
        assertSame(container.getComponent(A.class), useA.getDependency());
        binder.unBind(UseA.class);
    }
}
