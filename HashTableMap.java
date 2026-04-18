import java.util.LinkedList;
import java.util.NoSuchElementException;

public class HashTableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    
    LinkedList<Pair>[] table = null;

    @SuppressWarnings("unchecked")
    public HashTableMap(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least one");
        }

        table = (LinkedList<Pair>[]) new LinkedList[capacity];
    }

    @SuppressWarnings("unchecked")
    public HashTableMap() {
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
