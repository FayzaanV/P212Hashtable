import java.util.LinkedList;
import java.util.NoSuchElementException;

public class HashTableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    
    // Array to store the table. Linked Lists of Pair objects used to handle collisions
    LinkedList<Pair>[] table = null;

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
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {}

    @Override
    public boolean containsKey(KeyType key) {
        return false;
    }

    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        return null;
    }

    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        return null;
    }

    @Override
    public void clear() {}

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public LinkedList<KeyType> getKeys() {
        return null;
    }
}
