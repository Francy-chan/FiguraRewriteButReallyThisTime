package net.blancworks.figura.avatar.trust;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TrustContainer {

    //fields :p
    public String name;
    public boolean locked = false;
    public boolean expanded = true;
    private Identifier parentID;

    //trust -> value map
    private final Map<Trust, Integer> trustSettings;

    //the trust themselves
    public enum Trust {
        //trust list
        INIT_INST(0, 32768, 256),
        TICK_INST(0, 16384, 256),
        RENDER_INST(0, 16384, 256),
        COMPLEXITY(0, 12288, 192),
        PARTICLES(0, 64, 1),
        SOUNDS(0, 64, 1),
        BB_ANIMATIONS(0, 256, 16),
        VANILLA_MODEL_EDIT,
        NAMEPLATE_EDIT,
        OFFSCREEN_RENDERING,
        CUSTOM_RENDER_LAYER,
        CUSTOM_SOUNDS;

        //toggle check
        public final boolean isToggle;

        //used only for sliders
        public Integer min;
        public Integer max;
        public Integer step;

        //toggle constructor
        Trust() {
            this.isToggle = true;
        }

        //slider constructor
        Trust(Integer min, Integer max, Integer step) {
            this.isToggle = false;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        //infinity check :p
        public String checkInfinity(int value) {
            if (max != null && value >= max)
                return "INFINITY";

            return String.format("%d", value);
        }
    }

    // constructors //

    public TrustContainer(String name, Identifier parentID, NbtCompound nbt) {
        this.name = name;
        this.parentID = parentID;

        this.trustSettings = new HashMap<>();
        setTrustFromNbt(nbt);
    }

    public TrustContainer(String name, Identifier parentID, Map<Trust, Integer> trust) {
        this.name = name;
        this.parentID = parentID;
        this.trustSettings = new HashMap<>(trust);
    }

    // functions //

    //read nbt
    private void setTrustFromNbt(NbtCompound nbt) {
        for (Trust setting : Trust.values()) {
            String trustName = setting.name();

            if (nbt.contains(trustName))
                trustSettings.put(setting, nbt.getInt(trustName));
        }
    }

    //write nbt
    public void writeNbt(NbtCompound nbt) {
        //container properties
        nbt.put("name", NbtString.of(this.name));
        nbt.put("locked", NbtByte.of(this.locked));
        nbt.put("expanded", NbtByte.of(this.expanded));
        nbt.put("parent", NbtString.of(this.parentID.toString()));

        //trust values
        NbtCompound trust = new NbtCompound();
        this.trustSettings.forEach((key, value) -> trust.put(key.name(), NbtInt.of(value)));

        //add to nbt
        nbt.put("trust", trust);
    }

    //get value from trust
    public int get(Trust trust) {
        //get setting
        Integer setting = this.trustSettings.get(trust);
        if (setting != null)
            return setting;

        //if not, then get from parent
        if (parentID != null && TrustManager.get(parentID) != null)
            return TrustManager.get(parentID).get(trust);

        //if no trust found, return -1
        return -1;
    }

    // getters //

    public Map<Trust, Integer> getSettings() {
        return this.trustSettings;
    }

    public Identifier getParent() {
        return this.parentID;
    }

    // setters //

    public void setParent(Identifier parent) {
        this.parentID = parent;
    }
}
