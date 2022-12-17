package org.fpm.di.example;

import org.fpm.di.Container;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class DummyContainer implements Container {
     private static final Map<Class, Object> classes = new HashMap<>();

    public Map<Class, Object> getClasses(){
        return classes;
    }
    public void setClasses(Class clazz, Object instance){
        if(instance == null){
            classes.remove(clazz);
            return;
        }
        classes.put(clazz,instance);
    }
    @Override
    public <T> T getComponent(Class<T> clazz) {
        for (Constructor constr: clazz.getDeclaredConstructors()) {
            if(constr.isAnnotationPresent(Inject.class)){
                for (Class paramentr: constr.getParameterTypes()) {
                    if (!classes.containsKey(paramentr)){
                        classes.put(paramentr, getComponent(paramentr));
                    }
                    try {
                        if(!classes.containsKey(clazz)){
                        classes.put(clazz, constr.newInstance(classes.get(paramentr)));
                        }
                        return (T) classes.get(clazz);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
        if(clazz.equals(MyPrototype.class)){
            return (T) new MyPrototype();
        }
        else if(classes.containsKey(clazz)){
            return (T) classes.get(clazz);
        }
        else {
            Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
                    .filter(c -> c.getParameterTypes().length == 0)
                    .findFirst().orElseThrow(RuntimeException::new);
            try {
                classes.put(clazz, constructor.newInstance());
                return (T) constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
