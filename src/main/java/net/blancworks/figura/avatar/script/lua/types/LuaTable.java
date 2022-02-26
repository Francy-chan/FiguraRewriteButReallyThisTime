package net.blancworks.figura.avatar.script.lua.types;

import org.terasology.jnlua.util.AbstractTableMap;

/**
 * Just a helper class that gives us some easy-to-access options for elements in the table.
 */
public abstract class LuaTable extends AbstractTableMap<Object> {


    /**
     * Returns the value at a key, converted to a LuaFunction (or null if it's not a LuaFunction)
     */
    public LuaFunction getLuaFunction(Object key){
        Object ret = get(key);

        if(ret instanceof LuaFunction f){
            return f;
        }

        return null;
    }
}
