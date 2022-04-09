package net.blancworks.figura.avatar.customizations;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.config.Config;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.utils.TextUtils;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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

    public static Text fetchBadges(FiguraMetadata metadata) {
        //font
        Identifier font = (boolean) Config.BADGE_AS_ICONS.value ? TextUtils.FIGURA_FONT : Style.DEFAULT_FONT_ID;
        String badges = " ";

        //badges
        if (metadata.getAvatarFromSlot(0) != null) {
            if ((boolean) Config.EASTER_EGGS.value && FiguraMod.CHEESE_DAY)
                badges += "\uD83E\uDDC0";
            else
                badges += "△";
        }

        //return null if no badges
        if (badges.equals(" ")) return null;

        //return badges
        return new LiteralText(badges).setStyle(Style.EMPTY.withExclusiveFormatting(Formatting.WHITE).withFont(font));
    }
}
