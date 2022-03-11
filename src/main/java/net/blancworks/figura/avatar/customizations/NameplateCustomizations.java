package net.blancworks.figura.avatar.customizations;

import net.blancworks.figura.utils.TextUtils;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.minecraft.text.Text;

public class NameplateCustomizations {
    public final NameplateCustomization entityNameplate = new NameplateCustomization();
    public final NameplateCustomization tablistNameplate = new NameplateCustomization();
    public final NameplateCustomization chatNameplate = new NameplateCustomization();

    public static class NameplateCustomization {
        public String text = "[{\"text\":\"test...\n\",\"color\":\"#FFADAD\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"click me for free v-bucks\"}]},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/say oh-no\"}},{\"text\":\"tes\\nt...\",\"color\":\"light_purple\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"ok\"}]},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/say ok\"}}]";
        public FiguraVec3 position;
        public FiguraVec3 scale;
        public Boolean enabled;
    }

    public static Text applyNameplateCustomizations(String text) {
        text = TextUtils.noBadges4U(text);
        Text ret = TextUtils.tryParseJson(text);
        return TextUtils.removeClickableObjects(ret);
    }
}
