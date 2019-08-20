package com.walktour.gui.newmap2.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhicheng.chen
 * @date 2019/3/15
 */
public class FilterMap<K, V> {
    public Map<K, V> map = new HashMap<>();

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public V getOrDefault(K key, V defaultValue) {
        V v;
        return (((v = get(key)) != null) || map.containsKey(key))
                ? v
                : defaultValue;
    }

    public void clear() {
        map.clear();
    }
}
