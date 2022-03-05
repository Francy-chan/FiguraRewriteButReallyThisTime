package net.blancworks.figura.avatar.script.lua.reflector.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapBackedWrapper<TSelf, TValues> extends ObjectWrapper<TSelf> implements Map<String, TValues> {

    protected Map<String, TValues> fallbackMap;

    @Override
    public Object getFallback(String key) {
        return fallbackMap.get(key);
    }

    @Override
    public void setFallback(String key, Object value) {
        fallbackMap.put(key, (TValues) value);
    }

    @Override
    public int size() {
        return fallbackMap.size();
    }

    @Override
    public boolean isEmpty() {
        return fallbackMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return fallbackMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return fallbackMap.containsKey(value);
    }

    @Override
    public TValues get(Object key) {
        return fallbackMap.get(key);
    }

    @Nullable
    @Override
    public TValues put(String key, TValues value) {
        return fallbackMap.put(key, value);
    }

    @Override
    public TValues remove(Object key) {
        return fallbackMap.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends TValues> m) {
        fallbackMap.putAll(m);
    }

    @Override
    public void clear() {
        fallbackMap.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return fallbackMap.keySet();
    }

    @NotNull
    @Override
    public Collection<TValues> values() {
        return fallbackMap.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, TValues>> entrySet() {
        return fallbackMap.entrySet();
    }
}
