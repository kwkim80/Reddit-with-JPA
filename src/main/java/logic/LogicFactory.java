package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//TODO this class is just a skeleton it must be completed
public abstract class LogicFactory {

    private final static String PACKAGE = "logic.";
    private final static String SUFFIX = "Logic";

    private LogicFactory() {

    }

    public static <T> T getFor(String entityName) {
        try {
            T newInstance = getFor((Class<T>) Class.forName(PACKAGE + entityName + SUFFIX));
            return newInstance;
        } catch (ClassNotFoundException e) {

            System.out.println(e.getMessage());
            return null;
        }

    }

    public static <T> T getFor(Class<T> type) {
        try {
            Constructor<T> declaredConstructor = type.getDeclaredConstructor();
            T newInstance = declaredConstructor.newInstance();
            return newInstance;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
