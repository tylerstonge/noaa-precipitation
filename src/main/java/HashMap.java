public class HashMap<K,V> {
    
    private final static int INITIAL_SIZE = 503;
    
    private Entry[] map = new Entry[INITIAL_SIZE];
    private int collisions = 0;
    
    private class Entry<K,V> {
        public K key;
        public V value;
        public Entry next;
        
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
        
        public V getValue() {
            return value;
        }
        
        public void setValue(V value) {
            this.value = value;
        }
        
        public K getKey() {
            return key;
        }
    }
    
    public HashMap() {
        
    }
    
    public int hash(int hash) {
        return 0;
    }
    
    public V get(K key) {
        // Determine the bucket index from hashcode
        int position = key.hashCode() % map.length;
        
        // Loop over the bucket until we find the desired key
        Entry<K,V> e;
        for (e = map[position]; e != null; e = e.next) {
            if (e.getKey() == key) {
                return e.getValue();
            }
        }
        
        // If the key was not found, return null
        return null;
    }
    
    public void put(K key, V value) {
        // Determine the bucket index from the hashcode
        int position = key.hashCode() % map.length;
        
        // If the bucket is not null, there is a collision
        if (map[position] != null) {
            collisions++;
        }
        
        // Get to the last entry at the desired bucket
        Entry<K,V> e;
        for (e = map[position]; e != null; e = e.next) {
            // If key already exists, update value and return
            if (e.getKey() == key) {
                e.setValue(value);
                return;
            }
        }
        // Reached the end of the bucket, so append the new entry
        e.next = new Entry(key, value);
    }
    
    public int getCollisions() {
        return this.collisions;
    }
    
}