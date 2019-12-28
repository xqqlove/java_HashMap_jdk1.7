package hashmap_yiwa;

import java.io.Serializable;
import java.util.*;

public abstract class My_AbstractMap<K,V> implements My_Map<K,V>  {

    transient volatile Set<K> keySet = null;
    public Set<K> keySet(){
        if (keySet==null){
            keySet=new AbstractSet<K>() {
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        private Iterator<My_Map.Entry<K,V>> i=entrySet().iterator();
                        public boolean hasNext() {
                            return i.hasNext();
                        }
                        public K next() {
                            return i.next().getKey();
                        }
                        public void remove() {
                          i.remove();
                        }
                    };
                }
                public int size() {
                    return My_AbstractMap.this.size();
                }
                public boolean isEmpty(){
                    return My_AbstractMap.this.isEmpty();
                }
                public void clear(){
                    My_AbstractMap.this.clear();
                }
                public boolean contains(Object k){
                    return My_AbstractMap.this.containsKey(k);
                }
            };
        }
        return keySet;
    }

    transient volatile Collection<V> values = null;
    public Collection<V> values(){
        if (values==null){
            values=new AbstractCollection<V>() {
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        private Iterator<My_Map.Entry<K,V>> i=entrySet().iterator();
                        public boolean hasNext() {
                            return i.hasNext();
                        }
                        public V next() {
                            return i.next().getValue();
                        }
                        public void remove() {
                            i.remove();
                        }
                    };
                }
                public int size() {
                    return My_AbstractMap.this.size();
                }
                public boolean isEmpty(){
                    return My_AbstractMap.this.isEmpty();
                }
                public void clear(){
                    My_AbstractMap.this.clear();
                }
                public boolean contains(Object v){
                    return My_AbstractMap.this.containsValue(v);
                }
            };
        }
        return values;
    }

    public abstract Set<My_Map.Entry<K,V>> entrySet();

    public static class SimpleEntry<K, V> implements My_Map.Entry<K, V>, Serializable {
        private final K key;
        private V value;

        public SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public SimpleEntry(Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    public My_AbstractMap(){}

    public int size() {
        return entrySet().size();
    }
    public boolean isEmpty() {
        return size() == 0;
    }
    public boolean containsValue(Object value) {
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (value == null) {
            while (i.hasNext()) {
                Entry<K, V> e = i.next();
                if (e.getValue() == null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                Entry<K, V> e = i.next();
                if (value.equals(e.getValue()))
                    return true;
            }
        }
        return false;
    }
    public boolean containsKey(Object key){
        Iterator<My_Map.Entry<K,V>> i=entrySet().iterator();
        if (key==null){
            while (i.hasNext()){
                My_Map.Entry<K,V> e=i.next();
                if (e.getKey()==null)
                    return true;
            }
        }else {
            while (i.hasNext()){
                My_Map.Entry<K,V> e=i.next();
                if (e.getKey().equals(key))
                    return true;
            }
        }
        return false;
    }
    public V get(Object key){
        Iterator<My_Map.Entry<K,V>> i=entrySet().iterator();
        if (key==null){
            while (i.hasNext()){
                My_Map.Entry<K,V> e=i.next();
                if (e.getKey()==null)
                    return e.getValue();
            }
        }else {
            while (i.hasNext()){
                My_Map.Entry<K,V> e=i.next();
                if (key.equals(e.getKey()))
                    return e.getValue();
            }
        }
        return null;
    }
    public V put(K key,V value){
        throw  new UnsupportedOperationException();
    }
    public V remove(Object key){
        Iterator<My_Map.Entry<K,V>> i=entrySet().iterator();
        My_Map.Entry<K,V> correctEntry =null;
        if (key==null){
            while (correctEntry==null&&i.hasNext()){
                My_Map.Entry<K,V> e=i.next();
                if (e.getKey()==null)
                    correctEntry=e;
            }
        }else {
            while (correctEntry==null&&i.hasNext()){
                My_Map.Entry<K,V> e=i.next();
                if (key.equals(e.getKey()))
                    correctEntry=e;
            }
        }
        V oldValue=null;
        if (correctEntry!=null){
            oldValue = correctEntry.getValue();
            i.remove();
        }
        return oldValue;
    }
    public void putAll(My_Map<? extends K, ? extends V> m) {
        for (My_Map.Entry<? extends K,? extends V> e:m.entrySet()){
            put(e.getKey(),e.getValue());
        }
    }
    public void clear(){
        entrySet().clear();
    }
    public boolean equals(Object o){
        if (o==this)
            return true;
        if (!(o instanceof My_Map))
            return false;
        My_Map<K,V> m= (My_Map<K, V>) o;
        if (m.size()!=size())
            return false;
        try {
            Iterator<Entry<K,V>> i=entrySet().iterator();
            while (i.hasNext()){
                Entry<K,V> e=i.next();
                K key=e.getKey();
                V value=e.getValue();
                if (value==null){
                    if (!(m.get(key)==null&&m.containsKey(key)))
                        return false;
                }else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException e) {
            return false;
        }catch (NullPointerException e){
            return false;
        }
        return true;
    }
    public int hashCode(){
       int h=0;
       Iterator<My_Map.Entry<K,V>> i=entrySet().iterator();
       while (i.hasNext())
           h+=i.next().hashCode();
       return h;
    }
    public String toString(){
        Iterator<My_Map.Entry<K,V>> i=entrySet().iterator();
        if (!i.hasNext())
            return "{}";
        StringBuilder sb=new StringBuilder();
        sb.append("{");
        for (;;){
            My_Map.Entry<K,V> e=i.next();
            K key=e.getKey();
            V value=e.getValue();
            sb.append(key ==this?"this Map":key);
            sb.append('=');
            sb.append(value==this?"this Map":value);
            if (!i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }
    protected Object clone() throws CloneNotSupportedException{
        My_AbstractMap<K,V> result=(My_AbstractMap<K, V>) super.clone();
        result.keySet=null;
        result.values=null;
        return result;
    }
    private static boolean eq(Object o1,Object o2){
        return o1==null?o2==null:o1.equals(o2);
    }
    public static class SimpleImmutableEntry<K,V> implements My_Map.Entry<K,V>, Serializable {
        private final K key;
        private final V value;

        public SimpleImmutableEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public SimpleImmutableEntry(My_Map.Entry<? extends K, ? extends V> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry) o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        public String toString() {
            return key + "=" + value;
        }
    }
}
