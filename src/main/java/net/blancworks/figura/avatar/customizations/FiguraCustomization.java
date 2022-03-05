package net.blancworks.figura.avatar.customizations;

public interface FiguraCustomization<T> {
    void apply(T target);
    void revert(T target);
    void clear();
}
