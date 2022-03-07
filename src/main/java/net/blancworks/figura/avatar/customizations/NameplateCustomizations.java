package net.blancworks.figura.avatar.customizations;

import net.blancworks.figura.utils.TextUtils;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.minecraft.text.Text;

public class NameplateCustomizations {
    public final NameplateCustomization entityNameplate = new NameplateCustomization();
    public final NameplateCustomization tablistNameplate = new NameplateCustomization();
    public final NameplateCustomization chatNameplate = new NameplateCustomization();

    public static class NameplateCustomization {
        public String text;
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
