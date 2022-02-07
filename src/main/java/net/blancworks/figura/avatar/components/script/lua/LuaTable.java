package net.blancworks.figura.avatar.components.script.lua;

import org.terasology.jnlua.util.AbstractTableMap;

/**
 * Just a helper class that gives us some easy-to-access options for elements in the table.
 * @param <T>
 */
public abstract class LuaTable<T> extends AbstractTableMap<T> {


    /**
     * Returns the value at a key, converted to a LuaFunction (or null if it's not a LuaFunction)
     */
    public LuaFunction getLuaFunction(Object key){
        Object ret = get(key);

        if(ret instanceof LuaFunction){
            return (LuaFunction) ret;
        }

        return null;
    }
}
