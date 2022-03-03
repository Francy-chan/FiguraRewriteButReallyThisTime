package net.blancworks.figura.external;

import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Base class used to give Figura access to alternate APIs and features provided by other mods.
 */
public class FiguraExtension {

    public HashMap<Class<?>, Supplier<ObjectWrapper<?>>> customWrappers = new HashMap<>();
    public HashMap<String, APIFactory> apiFactories = new HashMap<>();

    /**
     * Called when Figura loads the extension via an entrypoint.
     */
    public void initialize() {

    }

    /**
     * Adds a custom API object that will be put into the `extensions.extensionName` global in lua, under the name provided.
     *
     * @param name    The name of the value to add
     * @param factory The factory that will produce the API required for the given name
     */
    public void addCustomAPI(String name, APIFactory factory) {
        apiFactories.put(name, factory);
    }

    /**
     * Adds a custom ObjectWrapper that will handle a given class.
     *
     * @param clazz   The class to handle.
     * @param wrapper The wrapper for the class.
     */
    public void addWrapper(Class<?> clazz, Supplier<ObjectWrapper<?>> wrapper) {
        customWrappers.put(clazz, wrapper);
    }
}
