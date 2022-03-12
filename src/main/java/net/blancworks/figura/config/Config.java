package net.blancworks.figura.config;

import net.blancworks.figura.FiguraMod;
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
     */

    CategoryTest,

    BOOLEAN_TEST(false),
    ENUM_TEST(1, 3),
    INPUT_TEST("test", InputType.ANY),
    KEYBIND_TEST(26, new ConfigKeyBind("something", 1, "test")),

    Category2,

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
    public Text name;
    public Text tooltip;
    public final ConfigType type;

    //special properties
    public ArrayList<Text> enumList;
    public ConfigKeyBind keyBind;
    public final InputType inputType;

    //type constructors
    Config() {
        this(ConfigType.CATEGORY, null, null, null, null);
    }
    Config(Object defaultValue) {
        this(ConfigType.BOOLEAN, defaultValue, null, null, null);
    }
    Config(Object defaultValue, Integer length) {
        this(ConfigType.ENUM, defaultValue, length, null, null);
    }
    Config(Object defaultValue, InputType inputType) {
        this(ConfigType.INPUT, defaultValue, null, null, inputType);
    }
    Config(Object defaultValue, ConfigKeyBind keyBind) {
        this(ConfigType.KEYBIND, defaultValue, null, keyBind, null);
        keyBind.setConfig(this);
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
        InputType(Predicate<String> predicate) {
            this.validator = predicate;
        }
    }

    public static class ConfigKeyBind extends KeyBinding {
        private Config config;

        public ConfigKeyBind(String translationKey, int code, String category) {
            super(translationKey, code, category);
        }

        public ConfigKeyBind(String translationKey, int code, String category, Config config) {
            super(translationKey, code, category);
            setConfig(config);
        }

        public void setConfig(Config config) {
            this.config = config;
            config.keyBind = this;
        }

        @Override
        public void setBoundKey(InputUtil.Key boundKey) {
            super.setBoundKey(boundKey);
            config.value = boundKey.getCode();
            ConfigManager.saveConfig();
        }
    }
}