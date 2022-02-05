package net.blancworks.figura.avatar.components.script.reflector.custom;

import com.google.common.collect.ImmutableSet;
import net.blancworks.figura.avatar.components.script.reflector.FiguraJavaReflector;
import org.terasology.jnlua.LuaState;

/**
 * Customized reflector used for whitelisting/blacklisting reflections, as well as other things.
 */
public class CustomReflector {

    // -- Variables -- //
    private ImmutableSet<String> accessorWhitelist = null;
    private ImmutableSet.Builder<String> whitelistBuilder = new ImmutableSet.Builder<>();

    // -- Constructor -- //
    public CustomReflector(){
    }

    public CustomReflector(String... whitelist){
        whitelistBuilder.add(whitelist);
    }

    // -- Functions -- //

    public void whitelistAccessor(String accessorName){
        whitelistBuilder.add(accessorName);
    }

    public void buildWhitelist(){
        if(whitelistBuilder == null) return;

        accessorWhitelist = whitelistBuilder.build();
        whitelistBuilder = null;
    }

    // -- Metamethods -- //

    /**
     * Called when is reflector is being accessed
     */
    public int index(LuaState state, Object instance){
        //If no whitelist exists, just return default.
        if(accessorWhitelist == null) return FiguraJavaReflector.defaultIndex.invoke(state);

        //The name of the field we want to access, which is on the top of the stack.
        String accessName = state.checkString(-1);

        //If whitelist contains the name of this accessor, return default value.
        if(accessorWhitelist.contains(accessName)) return FiguraJavaReflector.defaultIndex.invoke(state);

        //Whitelist exists, but doesn't contain value- Return nothing.
        return customIndex(state, instance, accessName);
    }

    public int customIndex(LuaState state, Object instance, String accessName){
        return 0;
    }

}
