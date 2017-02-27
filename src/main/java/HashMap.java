public class HashMap<K,V> {

    private final static int INITIAL_SIZE = 512;

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

    public HashMap() {

    }

    // http://burtleburtle.net/bob/hash/doobs.html
    public int hash(K key, int prevHash) {
        // Get the key as byte array
        byte[] bytes = Utilities.toByteArray(key);
        
        int len = bytes.length;
        int a = 0x9e3779b9;
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

    public V get(K key) {
        // Determine the bucket index from hashcode
        int position = Math.abs(hash(key, 0) % map.length);

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
        int position = Math.abs(hash(key, 0) % map.length);

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
        if (getLoadFactor() > 0.5) {
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

    public int getCollisions() {
        return this.collisions;
    }

    public double getLoadFactor() {
        return (double) stored / (double) map.length;
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
