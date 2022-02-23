package net.blancworks.figura.utils;

import java.util.ArrayList;

/**
 * A stack which caches values as it grows, modifying them instead of allocating new ones.
 */
public abstract class CacheStack<T, S> {

    private int index = -1;
    private T defaultVal = getNew();
    private final ArrayList<T> values = new ArrayList<>();

    /**
     * Gets a fresh instance of T to use.
     */
    protected abstract T getNew();

    /**
     * Should modify the first argument according to the second argument.
     */
    protected abstract void modify(T valueToModify, S modifierArg);

    /**
     * Should copy the information from the first item to the second item.
     */
    protected abstract void copy(T from, T to);

    /**
     * Called when an item is completely gone, when calling fullClear()
     * @param item
     */
    protected abstract void release(T item);

    /**
     * Fully clears the stack, removing any cached elements as well.
     */
    public void fullClear() {
        for(T val : values)
            release(val);
        values.clear();
        index = -1;
    }

    /**
     * Pushes a copy of the previous item on the top of the stack.
     * Then, modifies the item we just pushed according to modifierArg.
     * @param modifierArg
     */
    public void push(S modifierArg) {
        if (++index == values.size())
            values.add(getNew());
        if (index > 0)
            copy(values.get(index-1), values.get(index));
        modify(values.get(index), modifierArg);
    }

    public T peek() {
        if (index > 0)
            return values.get(index);
        return defaultVal;
    }

    public T pop() {
        return values.get(index--);
    }

}
