package net.arcadiasedge.riftseeker.managers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClassManager<T> extends Manager<Class<T>> {
    public T create(String name, Object... args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var clazz = entries.get(name);
        var objectClasses = new Class<?>[args.length];

        for (int i = 0; i < args.length; i++) {
            objectClasses[i] = args[i].getClass();
        }

        // now this is what i call evil
        Constructor<T> constructor = clazz.getDeclaredConstructor(objectClasses);
        return constructor.newInstance(args);
    }
}
