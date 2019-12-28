package hashmap_yiwa;

import java.util.Collection;
import java.util.Set;

public interface My_Map<K,V> {
    int size();
    boolean isEmpty();
    boolean containsKey(Object key);
    boolean containsValue(Object value);
    V get(Object key);
    V put(K key, V value);
    V remove(Object key);
    void putAll(My_Map<? extends K, ? extends V> m);
    void clear();
    interface Entry<K,V>{
        K getKey();
        V getValue();
        V setValue(V value);
        boolean equals(Object o);
        int hashCode();
    }
    Set<K> keySet();
    Collection<V> values();
    Set<Entry<K, V>> entrySet();
    boolean equals(Object o);
    int hashCode();
}
