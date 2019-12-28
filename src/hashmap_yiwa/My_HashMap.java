package hashmap_yiwa;

import java.io.Serializable;
import java.util.*;

public  class My_HashMap<K, V> extends My_AbstractMap<K,V> implements My_Map<K,V>,Cloneable, Serializable {
    static final int  DEFAULT_INITIAL_CAPACITY = 1 << 4;//处理容量，2的4次方，16，扩容后的容量必须是2的次方
    static final int MAXIMUM_CAPACITY = 1 << 30;//最大容量，2的30次方
    static final float DEFAULT_LOAD_FACTOR = 0.75f;//默认负载因子，0.75f
    static final  Entry<?,?>[] EMPTY_TABLE={};
    transient  Entry<K,V>[] table= (Entry<K, V>[]) EMPTY_TABLE;
    transient int size;
    int threshold;//扩容阀值
    final float loadFactor;
    transient int modCount;
    static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = Integer.MAX_VALUE;
    transient int hashSeed = 0;
    public  My_HashMap(int initialCapacity,float loadFactor){
        if (initialCapacity<0)
            throw new IllegalArgumentException("Illegal initial capacity: "+initialCapacity);
        if (initialCapacity>MAXIMUM_CAPACITY)
            initialCapacity=MAXIMUM_CAPACITY;
        if (loadFactor<=0||Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor :"+loadFactor);
        this.loadFactor=loadFactor;
        threshold=initialCapacity;
        init();
    }
    //My_HashMap初始化=====================================================
    public My_HashMap(int initialCapacity){
        this(initialCapacity,DEFAULT_LOAD_FACTOR);
    }
    public My_HashMap(){
        this(DEFAULT_INITIAL_CAPACITY,DEFAULT_LOAD_FACTOR);
    }
    public My_HashMap(My_Map<? extends K,? extends V> m){
        this(Math.max((int) (m.size()/DEFAULT_LOAD_FACTOR)+1,DEFAULT_INITIAL_CAPACITY),DEFAULT_LOAD_FACTOR);
        inflateTable(threshold);
        putAllForCreate(m);
    }
    //My_HashMap初始化=====================================================
    void init() {
    }
    private void putAllForCreate(My_Map<? extends K, ? extends V> m) {
        for (My_Map.Entry<? extends K, ? extends V> e : m.entrySet())
            putForCreate(e.getKey(), e.getValue());
    }
    private void putForCreate(K key, V value) {
        int hash = null == key ? 0 : hash(key);
        int i = indexFor(hash, table.length);

        /**
         * Look for preexisting entry for key.  This will never happen for
         * clone or deserialize.  It will only happen for construction if the
         * input Map is a sorted map whose ordering is inconsistent w/ equals.
         */
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k)))) {
                e.value = value;
                return;
            }
        }

        createEntry(hash, key, value, i);
    }
    final int hash(Object k){
        int h=hashSeed;
        if (0!=h&& k instanceof String){
            return sun.misc.Hashing.stringHash32((String)k);
        }
        h^=k.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }
    static int indexFor(int h,int length){
        return h&(length-1);
    }
    final Entry<K,V> getEntry(Object key){
        if (size==0) return null;
        int hash=(key==null)?0:hash(key);
        for (Entry<K,V> e=table[indexFor(hash,table.length)];e!=null;e=e.next){
            Object k;
            if (e.hash==hash && ((k=e.key)==key||(key!=null&&key.equals(k))))
                return e;
        }
        return null;
    }
    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    final Entry<K,V> removeEntryForKey(Object key){
        if (size==0) return null;
        int hash=(key==null)?0:hash(key);
        int i=indexFor(hash,table.length);
        Entry<K,V> prev=table[i];
        Entry<K,V> e=prev;
        while (e!=null){
            Entry<K,V> next = e.next;
            Object k;
            if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k)))){
                modCount++;
                size--;
                if (prev==e){
                    table[i]=next;
                }else {
                    prev.next=next;
                }
                e.recordRemoval(this);
                return e;
            }
            prev=e;
            e=next;
        }
        return e;
    }
    //entry对象==============================================
    static class Entry<K,V> implements My_Map.Entry<K,V>{
        final K key;
        V value;
        Entry<K,V> next;
        int hash;
        Entry(int h,K k,V v,Entry<K,V> n){
            this.hash=h;
            this.key=k;
            this.value=v;
            this.next=n;
        }
        public K getKey() {
            return key;
        }
        public V getValue() {
            return value;
        }
        public V setValue(V newValue) {
            V oldValue=this.value;
            this.value=newValue;
            return oldValue;
        }
        public final boolean equals(Object o){
            if (!(o instanceof Entry))
                return false;
            Entry e= (Entry) o;
            Object k1=getKey();
            Object k2=e.getKey();
            if (k1==k2||k1!=null&&k1.equals(k2)){
                Object v1=getValue();
                Object v2=e.getValue();
                if (v1==v2||(v1!=null&&v1.equals(v2)))
                    return true;
            }
            return false;
        }
        public final int hashCode(){
            return Objects.hashCode(getKey())^Objects.hashCode(getValue());
        }
        public final String toString(){
            return getKey()+"="+getValue();
        }
        void recordAccess(My_HashMap<K,V> m){}
        void recordRemoval(My_HashMap<K,V> m) {
        }
    }
    //entry对象==============================================

     //put插入键值对模块=========================================
    public V put(K key,V value){
        if (table==EMPTY_TABLE)
            inflateTable(threshold);
        if (key == null)
            return putForNullKey(value);
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        //插入的index位置已经存在value 若key相同hash相同 则用新值换旧值
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        modCount++;
        //index位置为空
        addEntry(hash, key, value, i);
        return null;
    }
    void addEntry(int hash, K key, V value, int bucketIndex) {
        if ((size >= threshold) && (null != table[bucketIndex])){
            resize(2 * table.length);
            hash = (null != key) ? hash(key) : 0;
            bucketIndex = indexFor(hash, table.length);
        }
        createEntry(hash, key, value, bucketIndex);
    }
    void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K,V> e = table[bucketIndex];
        table[bucketIndex] = new Entry<>(hash, key, value, e);
        size++;
    }
    void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable, initHashSeedAsNeeded(newCapacity));
        table = newTable;
        threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
    }
    //从旧table复制到新table
    void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        for (Entry<K,V> e : table) {
            while(null != e) {
                Entry<K,V> next = e.next;
                if (rehash) {
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
    }
    private V putForNullKey(V value) {
        for (Entry<K,V> e = table[0]; e != null; e = e.next) {
            if (e.key == null) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        modCount++;
        //如果键值对key为null一定插入到table[0]的数组位
        addEntry(0, null, value, 0);
        return null;
    }
    private void inflateTable(int toSize){
        int capacity = roundUpToPowerOf2(toSize);
        threshold=(int) Math.min(capacity*loadFactor,MAXIMUM_CAPACITY+1);
        table=new Entry[capacity];
        initHashSeedAsNeeded(capacity);

    }

    final boolean initHashSeedAsNeeded(int capacity) {
        boolean currentAltHashing = hashSeed != 0;
        boolean useAltHashing = sun.misc.VM.isBooted() &&
                (capacity >= Holder.ALTERNATIVE_HASHING_THRESHOLD);
        boolean switching = currentAltHashing ^ useAltHashing;
        if (switching) {
            hashSeed = useAltHashing
                    ? sun.misc.Hashing.randomHashSeed(this)
                    : 0;
        }
        return switching;
    }
    private static class Holder {

        /**
         * Table capacity above which to switch to use alternative hashing.
         */
        static final int ALTERNATIVE_HASHING_THRESHOLD;

        static {
            String altThreshold = java.security.AccessController.doPrivileged(
                    new sun.security.action.GetPropertyAction(
                            "jdk.map.althashing.threshold"));

            int threshold;
            try {
                threshold = (null != altThreshold)
                        ? Integer.parseInt(altThreshold)
                        : ALTERNATIVE_HASHING_THRESHOLD_DEFAULT;

                // disable alternative hashing if -1
                if (threshold == -1) {
                    threshold = Integer.MAX_VALUE;
                }

                if (threshold < 0) {
                    throw new IllegalArgumentException("value must be positive integer.");
                }
            } catch(IllegalArgumentException failed) {
                throw new Error("Illegal value for 'jdk.map.althashing.threshold'", failed);
            }

            ALTERNATIVE_HASHING_THRESHOLD = threshold;
        }
    }
    private static int roundUpToPowerOf2(int number){
        //返回number的2倍
        return number>MAXIMUM_CAPACITY?MAXIMUM_CAPACITY:(number>1)?Integer.highestOneBit((number-1)<<1):1;
    }

    public void putAll(My_Map<? extends K, ? extends V> m){
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0)
            return;
        if (table==EMPTY_TABLE)
            inflateTable((int) Math.max(numKeysToBeAdded * loadFactor, threshold));
        if (numKeysToBeAdded>threshold){
            int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > table.length)
                resize(newCapacity);
        }
        for (My_Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }
    //put插入键值对模块=========================================

    //get模块========================================================
    public V get(Object key) {
        if (key==null){
            return getForNullKey();
        }
        Entry<K,V> entry = getEntry(key);
        return null == entry ? null : entry.getValue();
    }
    private V getForNullKey() {
        if (size == 0) {
            return null;
        }
        //插入值的时候null一定插在table[0]数组位
        for (Entry<K,V> e = table[0]; e != null; e = e.next) {
            if (e.key == null)
                return e.value;
        }
        return null;
    }
    //get模块========================================================

    //删除模块
    public void clear() {
        modCount++;
        Arrays.fill(table, null);
        size = 0;
    }
    //删除模块


    //=========================将table用set属性封装=====用set属性访问hashMap的Entry[] table数组============
    private transient Set<My_Map.Entry<K,V>> entrySet=null;

    public Set<My_Map.Entry<K,V>> entrySet(){
        return entrySet0();
    }
    private Set<My_Map.Entry<K,V>> entrySet0(){
        Set<My_Map.Entry<K,V>> es=entrySet;
        return es!=null? es:(entrySet=new EntrySet());
    }
    private final class EntrySet extends AbstractSet<My_Map.Entry<K,V>>{

        public Iterator<My_Map.Entry<K,V>> iterator(){
            return newEntryIterator();
        }
        public boolean contains(Object o){
            if (!(o instanceof My_Map.Entry))
                return false;
            My_Map.Entry<K,V> e= (My_Map.Entry<K, V>) o;
            Entry<K,V> candidate=getEntry(e.getKey());
            return candidate!=null&& candidate.equals(e);
        }
        public int size() {
            return size;
        }
        public void clear(){
            My_HashMap.this.clear();
        }
    }
    Iterator<My_Map.Entry<K,V>> newEntryIterator(){
        return new EntryIterator();
    }
    private final class EntryIterator extends HashIterator<My_Map.Entry<K,V>>{
        public My_Map.Entry<K,V> next(){return nextEntry();}
    }
    private abstract class HashIterator<E> implements Iterator<E>{
        Entry<K,V> next;
        int expectedModCount;
        int index;
        Entry<K,V> current;
        HashIterator(){
            expectedModCount = modCount;
            if (size>0){
                Entry[] t = table;
                while (index<t.length&&(next=t[index++])==null)
                    ;
            }
        }
        public final boolean hasNext(){return next!=null;}
        final Entry<K,V> nextEntry(){
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Entry<K,V> e = next;
            if (e==null)
                throw new NoSuchElementException();
            if ((next=e.next)==null){
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
            current=e;
            return e;
        }
        public void remove(){
            if (current==null)
                throw new IllegalStateException();
            if (modCount!=expectedModCount)
                throw new ConcurrentModificationException();
            Object k=current.key;
            current=null;
            My_HashMap.this.removeEntryForKey(k);
            expectedModCount=modCount;
        }
    }
    //=========================将table用set属性封装=========================================================

    //===================将Entry<K,V> []table中的元素k用set属性分装==可用set属性访问元素k====================
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }
    private final class KeySet extends AbstractSet<K>{
        public Iterator<K> iterator() {
            return newKeyIterator();
        }
        public int size(){
            return size;
        }
        public boolean contains(Object o) {
            return containsKey(o);
        }
        public boolean remove(Object o) {
            return My_HashMap.this.removeEntryForKey(o) != null;
        }
        public void clear() {
            My_HashMap.this.clear();
        }
    }
    Iterator<K> newKeyIterator()   {
        return new KeyIterator();
    }
    private final class KeyIterator extends HashIterator<K> {
        public K next() {
            return nextEntry().getKey();
        }
    }
    //===================将Entry<K,V> []table中的元素k用set属性分装==可用set属性访问元素k====================

    //=====================将map中的value用映射为collection返回 ====================================================
    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null ? vs : (values = new Values()));
    }
    private final class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return newValueIterator();
        }
        public int size() {
            return size;
        }
        public boolean contains(Object o) {
            return containsValue(o);
        }
        public void clear() {
            My_HashMap.this.clear();
        }
    }
    Iterator<V> newValueIterator()   {
        return new ValueIterator();
    }
    private final class ValueIterator extends HashIterator<V> {
        public V next() {
            return nextEntry().value;
        }
    }
    //=====================将map中的value用collection返回 ====================================================
    int   capacity()     { return table.length; }
    float loadFactor()   { return loadFactor;   }
}
