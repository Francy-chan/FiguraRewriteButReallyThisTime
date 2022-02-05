package net.blancworks.figura.avatar.components.script;


import org.terasology.jnlua.DefaultJavaReflector;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.JavaReflector;

public class FiguraJavaReflector implements JavaReflector {


    @Override
    public JavaFunction getMetamethod(Metamethod metamethod) {
        return DefaultJavaReflector.getInstance().getMetamethod(metamethod);
    }
}
