import java.util.Collection;
import java.util.ArrayList;

public class HashMap<K,V> {

    private final static int INITIAL_SIZE = 512;
    private final static int REHASH_COUNT = 1;

    private Entry[] map = new Entry[INITIAL_SIZE];
    private int collisions = 0;
    private int stored = 0;

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

    // http://burtleburtle.net/bob/hash/doobs.html
    public int hash(K key) {
        int hash = 0;
        for (int i = 0; i < REHASH_COUNT; i++) {
            hash = doHash(key, hash);
        }
        return hash;
    }

    /*
    public int hash(K key) {
        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        h ^= (h >>> 7) ^ (h >>> 4);
        return h;
    }
    */

    public int doHash(K key, int prevHash) {
        // Get the key as byte array
        byte[] bytes = Utilities.toByteArray(key);

        int len = bytes.length;
        int a = 0xdeadbeef;
        int b = 0x9e3779b9;
        int c = prevHash;
        int k = 0;

        while (len >= 12) {
            // Loop through all but the last 11 bytes of the key
            a += (bytes[0 + k] + (bytes[1 + k] << 8)) + ((bytes[2 + k] << 16) + (bytes[3 + k] << 24));
            b += (bytes[4 + k] + (bytes[5 + k] << 8)) + ((bytes[6 + k] << 16) + (bytes[7 + k] << 24));
            c += (bytes[8 + k] + (bytes[9 + k] << 8)) + ((bytes[10 + k] << 16) + (bytes[11 + k] << 24));
            int[] abc = mix(a, b, c);
            a = abc[0];
            b = abc[1];
            c = abc[2];
            len -= 12;
            k += 12;
        }

        c += bytes.length;
        switch(len) {
            case 11: c += (bytes[10 + k] << 24);
            case 10: c += (bytes[9 + k] << 16);
            case 9: c += (bytes[8 + k] << 8);
            case 8: b += (bytes[7 + k] << 24);
            case 7: b += (bytes[6 + k] << 16);
            case 6: b += (bytes[5 + k] << 8);
            case 5: b += (bytes[4 + k]);
            case 4: a += (bytes[3 + k] << 24);
            case 3: a += (bytes[2 + k] << 16);
            case 2: a += (bytes[1 + k] << 8);
            case 1: a += (bytes[k]);
        }
        int[] abc = mix(a, b, c);
        return abc[2];
    }

    public int[]  mix(int a, int b, int c) {
        a -= b; a -= c; a ^= (c >> 13);
        b -= c; b -= a; b ^= (a << 8);
        c -= a; c -= b; c ^= (b >> 13);
        a -= b; a -= c; a ^= (c >> 12);
        b -= c; b -= a; b ^= (a << 16);
        c -= a; c -= b; c ^= (b >> 5);
        a -= b; a -= c; a ^= (c >> 3);
        b -= c; b -= a; b ^= (a << 10);
        c -= a; c -= b; c ^= (b >> 15);
        return new int[] {a, b, c};
    }

    /**
    * Get the value associated with the key from the hashmap.
    */
    public V get(K key) {
        // Determine the bucket index from hashcode
        int position = hash(key) & (map.length - 1);

        // Loop over the bucket until we find the desired key
        Entry<K,V> e;
        for (e = map[position]; e != null; e = e.next) {
            System.out.println("KEY: " + key);
            if (e.getKey() == key || e.getKey().equals(key)) {
                return e.getValue();
            }
        }

        // If the key was not found, return null
        return null;
    }

    /**
    * Return the nth value stored in the hashmap.
    */
    public V getAtIndex(int index) {
        int position = 0;
        for (int i = 0; i < map.length; i++) {
            if (map[i] != null) {
                // Loop through bucket to get length;
                Entry<K,V> e = map[i];
                while (e != null) {
                    // This is the nth value stored
                    if (position == index)
                        return e.getValue();
                    position++;
                    e = e.next;
                }
            }
        }
        return null;
    }

    /**
    * Store a key value pair in the hashmap.
    */
    public void put(K key, V value) {
        // Determine the bucket index from the hashcode
        int position = hash(key) & (map.length - 1);

        // Create entry
        Entry<K, V> newEntry = new Entry(key, value);

        // If the position is empty, add it and return
        if (map[position] == null) {
            stored++;
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
        stored++;
        e.next = newEntry;

        // Check if a rehash is needed
        if (getLoadFactor() > 0.75) {
            rehash();
        }
    }

    /**
    * Increase the size of the table to the next power of two and reinsert
    * all elements.
    */
    public void rehash() {
        System.out.println("Load factor: " + getLoadFactor() + "; Collisions: " + collisions + "; Rehashing...");
        collisions = 0;
        // new table size is next power of 2
        Entry[] old = map.clone();
        map = new Entry[map.length * 2];

        for (int i = 0; i < old.length; i++) {
            if (old[i] != null) {
                // Bucket is not empty, traverse
                Entry<K, V> e = old[i];
                do {
                    put(e.getKey(), e.getValue());
                    e = e.next;
                } while (e != null);
            }
        }
    }

    /**
    * Returns the number of collisions that have occured so far,
    * this is reset when rehashing.
    */
    public int getCollisions() {
        return this.collisions;
    }

    /**
    * Return load factor, between 0 and 1.
    */
    public double getLoadFactor() {
        return (double) stored / (double) map.length;
    }

    /**
    * Return the amount of key-value pairs.
    */
    public int size() {
        int size = 0;
        for (int i = 0; i < map.length; i++) {
            if (map[i] != null) {
                // Loop through bucket to get length;
                Entry<K,V> e = map[i];
                while (e != null) {
                    size++;
                    e = e.next;
                }
            }
        }
        return size;
    }

    public Collection<V> values() {
        Collection<V> values = new ArrayList<V>();
        for (int i = 0; i < map.length; i++) {
            if (map[i] != null) {
                // Loop through bucket to get length;
                Entry<K,V> e = map[i];
                while (e != null) {
                    values.add(e.getValue());
                    e = e.next;
                }
            }
        }
        return values;
    }

    /**
    * Print statistics about this hashmap.
    */
    public void printStats() {
        double averageBucket = 0.0;
        int emptyBuckets = 0;
        int maxBucket = 0;
        for (int i = 0; i < map.length; i++) {
            if (map[i] == null) {
                emptyBuckets++;
            } else {
                // Loop through bucket to get length;
                Entry<K,V> e = map[i];
                int c = 1;
                while (e != null) {
                    c++;
                    e = e.next;
                }
                averageBucket += (double) c;

                // Set new max size
                if (c > maxBucket) {
                    maxBucket = c;
                }
            }
        }
        System.out.println("-- HASHMAP STATS --\nCollisions: " + collisions + "\nSize: " + map.length + "\nEmpty Buckets: " + emptyBuckets + "\nMax Bucket Depth: " + maxBucket + "\nAverage Bucket Depth: " + averageBucket / (double) map.length);
    }
}
