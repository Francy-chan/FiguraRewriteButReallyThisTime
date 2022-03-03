package net.blancworks.figura.external;

import net.blancworks.figura.avatar.FiguraAvatar;

@FunctionalInterface
public interface APIFactory {
    Object run(FiguraAvatar avatar);
}