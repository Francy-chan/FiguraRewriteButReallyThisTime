package net.blancworks.figura.avatar.script.lua.modules;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.script.lua.FiguraLuaState;
import net.blancworks.figura.avatar.script.lua.types.LuaFunction;
import net.blancworks.figura.avatar.script.lua.types.LuaTable;
import net.blancworks.figura.trust.TrustContainer;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.LuaState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FiguraLuaModuleManager {
    // -- Variables -- //
    private final FiguraLuaState state;
    private final HashMap<String, LuaTable> loadedModules = new HashMap<>();
    private final HashMap<String, LuaEventGroup> eventGroups = new HashMap<>();
    private final Function<String, String> scriptFactory;

    private final LuaFunction sandboxRunFunction;

    private LuaFunction setInstructionWatcher;
    private LuaFunction resetInstructionWatcher;

    // -- Constructors -- //
    public FiguraLuaModuleManager(FiguraLuaState state, FiguraAvatar avatar, Function<String, String> scriptFactory) {
        this.state = state;
        this.scriptFactory = scriptFactory;

        //Custom require function
        state.pushJavaFunction(this::require);
        state.setGlobal("require");

        String quota = Integer.toString(avatar.trustContainer == null ? 2048 : avatar.trustContainer.get(TrustContainer.Trust.INIT_INST));

        //I really just don't wanna write out this function in java, so.
        state.load(
                String.format("local sandbox = sandbox return function(source, chunkName) return sandbox.run(source, { env = scriptSandbox, name = chunkName, quota = %s }) end", quota),
                "sbx"
        );
        state.call(0, 1);
        sandboxRunFunction = state.toJavaObject(-1, LuaFunction.class);
        state.pop(1);
    }

    // -- Functions -- //

    public void setupInstructionLimitFunctions(FiguraLuaState state) {
        setInstructionWatcher = state.avatarModuleTable.getLuaFunction("setInstructionWatcher");
        resetInstructionWatcher = state.avatarModuleTable.getLuaFunction("resetInstructionWatcher");
    }

    private int require(LuaState state) {
        //First argument is a string
        String moduleName = state.checkString(1);
        state.pop(1);

        var cachedModule = loadedModules.computeIfAbsent(moduleName, (name) -> {
            String source = scriptFactory.apply(name);

            if (source == null) return null;

            var retModule = sandboxRunFunction.call(LuaTable.class, source, name + ".lua");

            //Add to events
            for (Map.Entry<String, LuaEventGroup> entry : eventGroups.entrySet())
                entry.getValue().addLuaFunction(retModule, retModule.getLuaFunction(entry.getKey()));

            return retModule;
        });

        //Return cached module, if it exists.
        state.pushJavaObject(cachedModule);
        return 1;
    }


    // Events //

    private LuaEventGroup computeGroup(String eventName) {
        LuaEventGroup impl = new LuaEventGroup(eventName, setInstructionWatcher, resetInstructionWatcher);

        //Add existing events from tables.
        for (LuaTable table : loadedModules.values())
            if (table != null) impl.addLuaFunction(table, table.getLuaFunction(eventName));

        return impl;
    }

    public LuaEventGroup getEvent(String eventName) {
        return eventGroups.computeIfAbsent(eventName, this::computeGroup);
    }

    public static class LuaEventGroup {
        private final List<Entry> functions = new ArrayList<>();

        private final String name;
        private final LuaFunction setInstructionWatcher;
        private final LuaFunction resetInstructionWatcher;

        public int instructionLimit = 2048;

        public LuaEventGroup(String name, LuaFunction setInstructionWatcher, LuaFunction resetInstructionWatcher) {
            this.name = name;
            this.setInstructionWatcher = setInstructionWatcher;
            this.resetInstructionWatcher = resetInstructionWatcher;
        }

        public void addLuaFunction(LuaTable table, LuaFunction function) {
            if (function != null) functions.add(new Entry(table, function));
        }

        protected int instructionLimitHitCallback(LuaState state) {
            state.pushString(name);
            return 1;
        }


        public void run(Object... args) {
            setInstructionWatcher.call((JavaFunction) this::instructionLimitHitCallback, instructionLimit);

            Object[] totalArgs = new Object[args.length + 1];
            System.arraycopy(args, 0, totalArgs, 1, args.length);

            for (Entry entry : functions) {
                totalArgs[0] = entry.table;
                entry.function.call(totalArgs);
            }

            resetInstructionWatcher.call();
        }

        private static class Entry {
            public final LuaTable table;
            public final LuaFunction function;

            private Entry(LuaTable table, LuaFunction function) {
                this.table = table;
                this.function = function;
            }
        }
    }
}
