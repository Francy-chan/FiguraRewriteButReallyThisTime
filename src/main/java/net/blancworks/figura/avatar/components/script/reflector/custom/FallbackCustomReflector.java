package net.blancworks.figura.avatar.components.script.reflector.custom;

import net.blancworks.figura.avatar.components.script.api.models.ModelPartAPI;
import org.terasology.jnlua.LuaState;

public class FallbackCustomReflector<T extends FallbackAPI> extends TypedCustomReflector<T>{

    @Override
    public int customIndexType(LuaState state, T instance, String accessName) {
        int retCount = super.customIndexType(state, instance, accessName);

        //If existing access returns SOMETHING, return that instead.
        if(retCount != 0) return retCount;

        //If not, check for fallback value.
        Object fallbackValue = instance.getFallback(accessName);

        //If there is a fallback, return it.
        if(fallbackValue != null){
            //Return this part
            state.pushJavaObject(fallbackValue);
            return 1;
        }

        return 0;
    }
}
