package net.blancworks.figura.config;

import net.blancworks.figura.FiguraMod;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public enum Config {

    /**
     * Config Here!!!1!
     * (order matters)
     */

    RAW_CONFIG(false),

    CategoryTest,
    Category2,
    Category3,
    Category4,

    BOOLEAN_TEST(false),
    ENUM_TEST(1, 3),
    INPUT_TEST("test", InputType.ANY),
    COLOUR_TEST(0x72FFB7, InputType.HEX_COLOR),
    INT_TEST(420, InputType.INT),
    FLOAT_TEST(6.9, InputType.FLOAT),
    FOLDER_TEST("", InputType.FOLDER_PATH),
    KEYBIND_TEST("key.keyboard.f25", "test"),

    Category10,
    Category11,
    Category12,
    Category13,
    Category14,
    Category15,
    Category16,

    BOOL2(true);


    /**
     * Static Properties
     */


    //mod name
    public static final String MOD_NAME = FiguraMod.MOD_ID;

    //mod config version
    //only change this if you rename old configs
    public static final int CONFIG_VERSION = 1;

    //config update hashmap; <version number, <actual config, old config name>>
    public static final HashMap<Integer, HashMap<Config, String>> CONFIG_UPDATES = new HashMap<>();


    /**
     * do not edit below this line :p
     */


    //values
    public Object value;
    public Object configValue;
    public final Object defaultValue;

    //metadata
    public final Text name;
    public final Text tooltip;
    public final ConfigType type;

    //special properties
    public ArrayList<Text> enumList;
    public ConfigKeyBind keyBind;
    public final InputType inputType;

    //type constructors
    Config() {
        this(ConfigType.CATEGORY, null, null, null, null);
    }
    Config(boolean defaultValue) {
        this(ConfigType.BOOLEAN, defaultValue, null, null, null);
    }
    Config(int defaultValue, Integer length) {
        this(ConfigType.ENUM, defaultValue, length, null, null);
    }
    Config(Object defaultValue, InputType inputType) {
        this(ConfigType.INPUT, defaultValue, null, null, inputType);
    }
    Config(String key, String category) {
        this(ConfigType.KEYBIND, key, null, null, null);
        this.keyBind = new ConfigKeyBind(this.name.getString(), InputUtil.fromTranslationKey(key), category, this);
    }

    //global constructor
    Config(ConfigType type, Object value, Integer length, ConfigKeyBind keyBind, InputType inputType) {
        //set values
        this.type = type;
        this.value = value;
        this.defaultValue = value;
        this.configValue = value;
        this.keyBind = keyBind;
        this.inputType = inputType;

        //generate names
        String name = MOD_NAME + ".config." + this.name().toLowerCase();
        this.name = new TranslatableText(name);
        this.tooltip = new TranslatableText(name + ".tooltip");

        //generate enum list
        if (length != null) {
            ArrayList<Text> enumList = new ArrayList<>();
            for (int i = 1; i <= length; i++)
                enumList.add(new TranslatableText(name + "." + i));
            this.enumList = enumList;
        }
    }

    public void setValue(String text) {
        boolean change = value.equals(configValue);

        try {
            if (value instanceof String)
                value = text;
            else if (value instanceof Boolean)
                value = Boolean.valueOf(text);
            else if (value instanceof Integer)
                value = Integer.valueOf(text);
            else if (value instanceof Float)
                value = Float.valueOf(text);
            else if (value instanceof Long)
                value = Long.valueOf(text);
            else if (value instanceof Double)
                value = Double.valueOf(text);
            else if (value instanceof Byte)
                value = Byte.valueOf(text);
            else if (value instanceof Short)
                value = Short.valueOf(text);

            if (enumList != null) {
                int length = enumList.size();
                value = ((Integer.parseInt(text) % length) + length) % length;
            }
        } catch (Exception e) {
            value = defaultValue;
        }

        configValue = value;
        if (change) runOnChange();
    }

    public void runOnChange() {}

    public enum ConfigType {
        CATEGORY,
        BOOLEAN,
        ENUM,
        INPUT,
        KEYBIND
    }

    public enum InputType {
        ANY(s -> true),
        INT(s -> s.matches("^[\\-+]?[0-9]*$")),
        FLOAT(s -> s.matches("[\\-+]?[0-9]*(\\.[0-9]+)?") || s.endsWith(".") || s.isEmpty()),
        HEX_COLOR(s -> s.matches("^[#]?[0-9A-Fa-f]{0,6}$")),
        FOLDER_PATH(s -> {
            if (!s.isBlank()) {
                try {
                    return Path.of(s.trim()).toFile().isDirectory();
                } catch (Exception ignored) {
                    return false;
                }
            }

            return true;
        });

        public final Predicate<String> validator;
        public final Text hint;
        InputType(Predicate<String> predicate) {
            this.validator = predicate;
            this.hint = new TranslatableText(MOD_NAME + ".config.input." + this.name().toLowerCase());
        }
    }

    public static class ConfigKeyBind extends KeyBinding {
        private final Config config;

        public ConfigKeyBind(String translationKey, InputUtil.Key key, String category, Config config) {
            super(translationKey, key.getCategory(), key.getCode(), category);
            this.config = config;
            KeyBindingRegistryImpl.registerKeyBinding(this);
        }

        @Override
        public void setBoundKey(InputUtil.Key boundKey) {
            super.setBoundKey(boundKey);

            config.setValue(this.getBoundKeyTranslationKey());
            ConfigManager.saveConfig();

            GameOptions options = MinecraftClient.getInstance().options;
            if (options != null) options.write();
            KeyBinding.updateKeysByCode();
        }
    }
}