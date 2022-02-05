package net.blancworks.figura.avatar.components.script.reflector;

import net.blancworks.figura.avatar.components.script.api.FiguraAPI;
import net.blancworks.figura.avatar.components.script.api.models.ModelPartAPI;
import net.blancworks.figura.avatar.components.script.api.models.ModelsAPI;
import net.blancworks.figura.avatar.components.script.reflector.custom.CustomReflector;
import net.blancworks.figura.avatar.components.script.reflector.custom.FallbackCustomReflector;
import org.jetbrains.annotations.NotNull;
import org.terasology.jnlua.DefaultJavaReflector;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.JavaReflector;
import org.terasology.jnlua.LuaState;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FiguraJavaReflector  implements JavaReflector {

    // -- Variables -- //
    public static final FiguraJavaReflector instance = new FiguraJavaReflector();


    // - Metamethod instances -
    private final JavaFunction indexFunction = this::Index;

    // - Default metamethods (cached for perfies) -
    public static final JavaFunction defaultIndex = DefaultJavaReflector.getInstance().getMetamethod(Metamethod.INDEX);

    // -- Functions -- //
    public void init(){
        registerDefaultCustomReflectors();
    }

    @Override
    public JavaFunction getMetamethod(Metamethod metamethod) {
        switch (metamethod) {
            case INDEX:
                return indexFunction;
        }

        //Return default if other thing isn't found.
        return DefaultJavaReflector.getInstance().getMetamethod(metamethod);
    }


    // -- Custom Reflectors -- //
    // Custom reflectors for types which allow custom behaviour
    private final HashMap<Class<?>, CustomReflector> customReflectors = new HashMap<>();

    //Registers all the default types that we want to include for reflection and such
    private void registerDefaultCustomReflectors(){

        //Figura API
        registerCustomReflector(FiguraAPI.class);

        registerCustomReflector(ModelsAPI.class, new FallbackCustomReflector<ModelsAPI>());
        registerCustomReflector(ModelPartAPI.class, new FallbackCustomReflector<ModelPartAPI>());
    }

    //Registers a custom reflector with the default whitelist attribute finding system.
    public void registerCustomReflector(Class<?> type){
        registerCustomReflector(type, new CustomReflector());
    }

    //Registers a custom reflector to be used later. Will find and add whitelisted values.
    public void registerCustomReflector(Class<?> type, @NotNull CustomReflector reflector){
        //Whitelist methods with attribute.
        for (Method method : type.getMethods()) {
            //Find attribute. If none is found, skip.
            LuaWhitelist whitelist = method.getAnnotation(LuaWhitelist.class);
            if(whitelist == null) continue;

            reflector.whitelistAccessor(method.getName());
        }

        //Whitelist fields with attribute.
        for (Field field : type.getFields()) {
            LuaWhitelist whitelist = field.getAnnotation(LuaWhitelist.class);
            if(whitelist == null) continue;

            reflector.whitelistAccessor(field.getName());
        }

        //Build whitelist now that we've found everything
        reflector.buildWhitelist();
        customReflectors.put(type, reflector);
    }

    // -- Metamethods -- //

    //Helper function
    private Class<?> getObjectClass(Object object) {
        return object instanceof Class<?> ? (Class<?>) object : object
                .getClass();
    }

    public int Index(LuaState luaState) {
        //Object on the bottom of the stack (whatever called this function)
        Object object = luaState.toJavaObject(1, Object.class);
        //Class of the object
        Class<?> objectClass = getObjectClass(object);

        //Attempt to get the custom reflector
        CustomReflector reflector = customReflectors.get(objectClass);

        //If there's no custom reflector, just return the default.
        if(reflector == null) return defaultIndex.invoke(luaState);

        //There is a custom reflector, return the value from it
        return reflector.index(luaState, object);
    }
}