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
        hash ^= 0xdeadbeef ^ (hash >>> 20);
        return hash ^ 0xBAAAAAAD;
    }

    public V get(K key) {
        // Determine the bucket index from hashcode
        int position = Math.abs(hash(key.hashCode()) % map.length);

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
        int position = Math.abs(hash(key.hashCode()) % map.length);

        // Create entry
        Entry<K, V> newEntry = new Entry(key, value);

        // If the position is empty, add it and return
        if (map[position] == null) {
            map[position] = newEntry;
            return;
        }

        // Since bucket was not null, must have collision
        collisions++;

        // Get to the last entry at the desired bucket
        Entry<K, V> e = map[position];
        while (e.next != null) {
            // If key already exists, update value and return
            if (e.getKey() == key) {
                e.setValue(value);
                return;
            }
            e = e.next;
        }

        // Reached the end of the bucket, so append the new entry
        e.next = newEntry;
    }

    public int getCollisions() {
        return this.collisions;
    }

    public void print() {
        for (int i = 0; i < map.length; i++) {
            if (map[i] != null) {
                System.out.println("-- Bucket " + i + " --");
                Entry<K,V> e = map[i];
                int c = 1;
                while (e != null) {
                    c++;
                    e = e.next;
                }
                System.out.println("size:" + c);
            }
        }
    }
}
