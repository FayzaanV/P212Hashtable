import java.util.List;
import java.util.NoSuchElementException;

public class HashTableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    
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
    public List<KeyType> getKeys() {
        return null;
    }
}
