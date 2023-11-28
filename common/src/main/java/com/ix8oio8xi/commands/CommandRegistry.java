package com.ix8oio8xi.commands;

import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Registry of command processors located in the specified package.
 * Commands are searched based on the {@link CommandProcessor} annotation.
 */
public class CommandRegistry<T> {
    private final Map<Byte, T> processors = new HashMap<>();
    private final Map<Byte, String> stringCodes = new TreeMap<>();
    private final String pkge;
    private boolean initialized = false;

    public CommandRegistry(String pkge) {
        this.pkge = pkge;
    }

    /**
     * Find all processors annotated with {@link CommandProcessor} in given package and add to registry.
     *
     * @throws IllegalStateException if init() method called more than once,
     *                               if find processor with 0x0 opCode or
     *                               if two processors have the same opCode
     */
    public void init() {
        if (initialized) {
            // The registry cannot be rebuilt
            throw new IllegalStateException("The command registry has already been assembled.");
        }
        initialized = true;

        Reflections reflections = new Reflections(pkge);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CommandProcessor.class);

        for (Class<?> clazz : annotatedClasses) {
            addProcessor(clazz);
        }
    }

    /**
     * Returns immutable Map<Byte, String> with names of processors found.
     * Can be used for debugging purposes.
     */
    public Map<Byte, String> getStringCodes() {
        return Collections.unmodifiableMap(stringCodes);
    }

    public T getProcessor(byte code) {
        return processors.get(code);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Registry package:").append(pkge).append(System.lineSeparator());
        stringCodes.forEach((k, v) -> sb.append(k).append(" : ").append(v).append(System.lineSeparator()));
        return sb.toString();
    }

    private void addProcessor(Class<?> clazz) {
        CommandProcessor annotation = clazz.getAnnotation(CommandProcessor.class);
        byte code = annotation.opCode();
        String className = clazz.getCanonicalName();

        if (code == 0x0) {
            throw new IllegalStateException("Processor " + className + " has 0x0 opCode");
        }

        String check = stringCodes.get(code);
        if (check != null) {
            throw new IllegalStateException("Processors " + className + " and " + check
                    + " both have the same code: " + code);
        }

        tryConstructProcessor(clazz, code);
        stringCodes.put(code, className);
    }

    private void tryConstructProcessor(Class<?> clazz, byte code) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            @SuppressWarnings("unchecked")
            T newProcessor = (T) constructor.newInstance();
            processors.put(code, newProcessor);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(clazz.getCanonicalName() +
                    " processor should have accessible public default constructor: " + e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Error while invoke of Processor constructor: " + e);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Annotated class should implement processor interface");
        }
    }
}
