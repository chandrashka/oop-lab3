package org.fpm.di.example;
import org.fpm.di.Binder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DummyBinder implements Binder {

    private final DummyContainer container = new DummyContainer();
    private Map<Class, Object> classes;
    @Override
    public <T> void bind(Class<T> clazz) {
        classes = container.getClasses();
        if(!classes.containsKey(clazz)) {
            container.setClasses(clazz, container.getComponent(clazz));
        }
    }


    @Override
    public <T> void bind(Class<T> clazz, Class<? extends T> implementation) {
        if(implementation.getSuperclass() == clazz){
            Constructor<?> constructor = Arrays.stream(implementation.getDeclaredConstructors())
                    .filter(c -> c.getParameterTypes().length == 0)
                    .findFirst().orElseThrow(RuntimeException::new);
            try {
                classes = container.getClasses();
                if(classes.containsKey(implementation)){
                    container.setClasses(clazz, classes.get(implementation));
                }
                Object instance = constructor.newInstance();
                container.setClasses(clazz, instance);
                container.setClasses(implementation, instance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public <T> void bind(Class<T> clazz, T instance) {
        classes = container.getClasses();
        if(!classes.containsKey(clazz)){
            container.setClasses(clazz,instance);
        }
    }

    public <T> void unBind(Class<T> clazz, Class<? extends T> implementation){
        classes = container.getClasses();
        if(classes.containsKey(clazz) == classes.containsKey(implementation)){
            classes.remove(clazz);
        }
    }
    public <T> void unBind(Class<T> clazz){
        container.setClasses(clazz, null);
    }
}
