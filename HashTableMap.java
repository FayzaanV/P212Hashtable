import java.util.LinkedList;
import java.util.NoSuchElementException;

public class HashTableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    
    // Array to store the table. Linked Lists of Pair objects used to handle collisions
    protected LinkedList<Pair>[] table = null;
    // Private helper field to store the load factor. Update it after every insertion and removal
    private double lf = 0;
    // Private field to store the threshhold for resizing
    private final double THRESHOLD = 0.75;

    /**
     * This constructor takes one parameter and sets the initial size of the HashTable to that
     * @param capacity is the initial size
     * @throws IllegalArgumentException if the capacity parameter is zero or negative
     */
    @SuppressWarnings("unchecked")
    public HashTableMap(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least one");
        }

        // Cast the array to hold LinkedList<Pair> ojects since it can't be declared to do so directly.
        table = (LinkedList<Pair>[]) new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
    }

    /**
     * This constructor takes no parameters and set the HashTable to an initial size of 8
     */
    public HashTableMap() {
        this(8);
    }

    protected class Pair {
        public KeyType key;
        public ValueType value;

        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * This helper method calculates what array index each key should be stored at by taking the absolute value of the key's hashCode and doing the modulus
     * operator with the capacity of the array.
     */
    private int hash(KeyType key) {
        int hc = key.hashCode();
        return (Math.abs(hc) % table.length);
    }

    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (containsKey(key)) {
            throw new IllegalArgumentException("Key is already stored");
        }
        Pair tuple = new Pair(key, value);
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        target.add(tuple);
        //Update lf and resize
        updateLF();
        if (lf >= THRESHOLD) {
            resize();
        }
    }

    @Override
    public boolean containsKey(KeyType key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        for(Pair p : target) {
            if (p.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        for(Pair p : target) {
            if (p.key.equals(key)) {
                return p.value;
            }
        }
        throw new NoSuchElementException("Key not found in the HashTableMap");
    }

    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        for(Pair p : target) {
            if (p.key.equals(key)) {
                target.remove(p);
                updateLF();
                return p.value;
            }
        }
        throw new NoSuchElementException("Key not found in the HashTableMap");
    } 

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        int capacity = getCapacity();
        // By creating a new list, we erase all reference to the old list and every pair it stored. They will be picked up by the garbage collector.
        table = (LinkedList<Pair>[]) new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
    }

    /**
     * For each LinkedList in table, add the length of it to a cumulative total. After iterating through all of the array, return the total. This will
     * be the number of keys in the entire collection.
     */
    @Override
    public int getSize() {
        int size = 0;
        for (LinkedList<Pair> l : table) {
            size += l.size();
        }
        return size;
    }

    /**
     * This method returns the size of the array holding the collection, since that is the capacity.
     */
    @Override
    public int getCapacity() {
        return table.length;
    }

    /**
     * This helper method updates the load factor field by dividing the keys in the collection by how many keys it can store.
     */
    private void updateLF() {
        lf = (double)getSize()/getCapacity();
    }

    /**
     * Getter method for the load factor field.
     */
    private double getLF() {
        return lf;
    }

    @Override
    public LinkedList<KeyType> getKeys() {
        LinkedList<KeyType> allKeys = new LinkedList<>();
        for(LinkedList<Pair> l : table) {
            for(Pair p : l) {
                allKeys.add(p.key);
            }
        }
        return allKeys;
    }

    /**
     * Helper method to resize the array if the load factor gets too high
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int origCapacity = getCapacity();
        LinkedList<Pair> allPairs = new LinkedList<>();
        for(LinkedList<Pair> l : table) {
            allPairs.addAll(l);
        }
        table = (LinkedList<Pair>[]) new LinkedList[origCapacity * 2];
        for (int i = 0; i < origCapacity * 2; i++) {
            table[i] = new LinkedList<>();
        }
        for (Pair p : allPairs) {
            put(p.key, p.value);
        }
    }

    public static void main(String[] args) {
        String myStr = "Hello";
        System.out.println(myStr.hashCode());
        Integer myInt = new Integer(97);
        System.out.println(myInt.hashCode());
    }
}
