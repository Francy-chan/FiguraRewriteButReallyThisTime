package net.blancworks.figura.avatar.model;

import com.google.common.collect.ImmutableList;

import java.util.Locale;

public class ParentType {

    private static final ImmutableList<String> parentKeywords = new ImmutableList.Builder<String>().add(
            "head",
            "body",
            "leftarm",
            "rightarm",
            "leftleg",
            "rightleg"
        ).build();

    public static String getTypeFromKeyword(String key) {
        String keyLower = key.toLowerCase(Locale.ROOT);

        for (String keyword : parentKeywords)
            if (keyLower.startsWith(keyword))
                return keyword;

        return "root";
    }
}
