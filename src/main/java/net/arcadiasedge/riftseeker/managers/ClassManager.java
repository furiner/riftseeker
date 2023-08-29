package net.arcadiasedge.riftseeker.managers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ClassManager<T> extends Manager<Class<T>> {
    public T create(String name, Object... args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var clazz = entries.get(name);
        var objectClasses = new Class<?>[args.length];

        for (int i = 0; i < args.length; i++) {
            objectClasses[i] = getBaseClass(args[i].getClass());
        }

        for (Constructor<?> c : clazz.getConstructors()) {
            System.out.println(c);
        }

        // now this is what i call evil
        var baseClass = getBaseClass(clazz);
        Constructor<?> constructor = baseClass.getConstructor(objectClasses);
        return (T)constructor.newInstance(args);
    }

    private Class<?> getBaseClass(Class<?> clazz) {
        if (clazz.getSuperclass() == null || clazz.getSuperclass().equals(Object.class)) {
            return clazz;
        } else {
            return getBaseClass(clazz.getSuperclass());
        }
    }
}
