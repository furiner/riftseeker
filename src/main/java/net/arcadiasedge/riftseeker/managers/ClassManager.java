package net.arcadiasedge.riftseeker.managers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ClassManager<T> extends Manager<Class<T>> {
    public T create(String name, Object... args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var clazz = entries.get(name);
        var objectClasses = new Class<?>[args.length];

        System.out.println("Creating " + name + " with " + args.length + " args");
        System.out.println("Class: " + clazz);

        if (clazz == null) {
            return null;
        }

        for (int i = 0; i < args.length; i++) {
            objectClasses[i] = getBaseClass(args[i].getClass());
        }

        try {
            var constructor = clazz.getConstructor(objectClasses);
            return (T)constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);

            /*// no constructor
            // now this is what i call evil
            var baseClass = getBaseClass(clazz);
            Constructor<?> constructor = baseClass.getConstructor(objectClasses);

            try {
                return (T)constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }*/
        }
    }

    public static Class<?> getBaseClass(Class<?> clazz) {
        if (clazz.getSuperclass() == null || clazz.getSuperclass().equals(Object.class)) {
            return clazz;
        } else {
            return getBaseClass(clazz.getSuperclass());
        }
    }
}
