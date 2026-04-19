import java.util.LinkedList;
import java.util.NoSuchElementException;

public class HashTableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    
    // Array to store the table. Linked Lists of Pair objects used to handle collisions
    LinkedList<Pair>[] table = null;
    // Private helper field to store the load factor. Update it after every insertion and removal
    double lf = 0;
    // Private field to store the threshhold for resizing
    private double THRESHOLD = 0.75;

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
    }

    /**
     * This constructor takes no parameters and set the HashTable to an initial size of 8
     */
    @SuppressWarnings("unchecked")
    public HashTableMap() {
        // Cast the array to hold LinkedList<Pair> ojects since it can't be declared to do so directly.
        table = (LinkedList<Pair>[]) new LinkedList[8];
    }

    protected class Pair {
        public KeyType key;
        public ValueType value;

        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        // Check for other exception here
        Pair tuple = new Pair(key, value);
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        target.add(tuple);
        //Update lf here
        updateLF();
        // resize
    }

    @Override
    public boolean containsKey(KeyType key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        if (target.contains(key)) {
            return true;
        }
        return false;
    }

    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        return null;
    }

    /**
     * This helper method calculates what array index each key should be stored at by taking the absolute value of the key's hashCode and doing the modulus
     * operator with the capacity of the array.
     */
    private int hash(KeyType key) {
        int hc = key.hashCode();
        return (hc % table.length);
    }

    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        // Update lf here
        return null;
    } 

    @Override
    public void clear() {}

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
     * This helper method updates the load factor field by counting how many indices are in use and then dividing it by the capacity.
     * The load factor is updated every time a key-value pair is added or removed.
     */
    private void updateLF() {
        int inUse = 0;
        // For every LinkedList that is not empty, that index will be in use and we increment the counter.
        for (LinkedList<Pair> l : table) {
            if (!(l.isEmpty())) {
                inUse++;
            }
        }
        // Don't have to worry about a divide by zero error since capacity will always be at least one.
        lf = inUse/getCapacity();
    }

    /**
     * Getter method for the load factor field.
     */
    private double getLF() {
        return lf;
    }

    /**
     * Helper method to resize the array if the load factor gets too high
     */
    public void resize() {

    }

    @Override
    public LinkedList<KeyType> getKeys() {
        return null;
    }

    public static void main(String[] args) {
        String myStr = "Hello";
        System.out.println(myStr.hashCode());
        Integer myInt = new Integer(97);
        System.out.println(myInt.hashCode());
    }
}
