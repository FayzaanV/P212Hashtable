import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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

    ////////////////////////////////////////////////////////// TESTER METHODS //////////////////////////////////////////////////////////
     
    @Test
    public void testPutAndResize() {
        HashTableMap<Integer, String> map = new HashTableMap<>(3);
        assertEquals(map.getCapacity(), 3);
        map.put(4, "Crow-Armstrong");
        assertEquals(map.getSize(), 1);
        assertEquals(map.getCapacity(), 3);
        map.put(27, "Suzuki");
        assertEquals(map.getSize(), 2);
        assertEquals(map.getCapacity(), 3);
        map.put(2, "Hoerner");
        assertEquals(map.getSize(), 3);
        assertEquals(map.getCapacity(), 6);
        map.put(29, "Busch");
        assertEquals(map.getSize(), 4);
        assertEquals(map.getCapacity(), 6);
        map.put(7, "Swanson");
        assertEquals(map.getSize(), 5);
        assertEquals(map.getCapacity(), 12);
    }

    @Test
    public void testContainsGetAndRemove() {
        HashTableMap<Integer, String> map = new HashTableMap<>();
        map.put(15,"Odunze");
        map.put(10, "Burden III");
        map.put(18, "Williams");
        map.put(84, "Loveland");
        map.put(85, "Kmet");
        map.put(4, "Swift");
        map.put(25, "Monongai");
        assertTrue(map.containsKey(15));
        assertTrue(map.containsKey(18));
        assertTrue(map.containsKey(85));
        assertTrue(!map.containsKey(9));
        assertTrue(!map.containsKey(17));
        assertTrue(!map.containsKey(58));
        assertEquals(map.get(10), "Burden III");
        assertEquals(map.get(85), "Kmet");
        assertEquals(map.getSize(), 7);
        assertEquals(map.remove(4), "Swift");
        assertEquals(map.remove(25), "Monongai");
        assertEquals(map.getSize(), 5);
    }

    @Test
    public void testErrorHandling() {
        try {
            HashTableMap<Integer, String> wrongMap = new HashTableMap<>(-17);
            assertTrue(false);
        } catch (IllegalArgumentException e) {

        } catch (Exception e) {
            assertTrue(false);
        }

        HashTableMap<Integer, String> map = new HashTableMap<>();
        map.put(4, "Swift");
        try {
            map.put(4, "Crow-Armstrong");
            assertTrue(false);
        } catch (IllegalArgumentException e) {

        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            map.get(70);
            assertTrue(false);
        } catch (NoSuchElementException e) {

        } catch (Exception e) {
            assertTrue(false);
        }
        try {
            map.remove(62);
            assertTrue(false);
        } catch (NoSuchElementException e) {

        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            map.put(null, "Zero");
            assertTrue(false);
        } catch (NullPointerException e) {

        } catch (Exception e) {
            assertTrue(false);
        }
        try {
            map.containsKey(null);
            assertTrue(false);
        } catch (NullPointerException e) {

        } catch (Exception e) {
            assertTrue(false);
        }
        try {
            map.get(null);
            assertTrue(false);
        } catch (NullPointerException e) {

        } catch (Exception e) {
            assertTrue(false);
        }
        try {
            map.remove(null);
            assertTrue(false);
        } catch (NullPointerException e) {

        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public static void main(String args[]) {
        HashTableMap<Integer, String> map = new HashTableMap<>();
        map.put(15,"Odunze");
        map.put(10, "Burden III");
        map.put(18, "Williams");
        map.put(84, "Loveland");
        map.put(85, "Kmet");
        map.put(4, "Swift");
        map.put(25, "Monongai");
        try {
            map.get(70);
            System.out.println("70TNE");
        } catch (NoSuchElementException e) {

        } catch (Exception e) {
            System.out.println("70TWE");
        }
        try {
            map.remove(62);
            System.out.println("62TNE");
            assertTrue(false);
        } catch (NoSuchElementException e) {

        } catch (Exception e) {
            System.out.println("62TWE");
        }
    }
}
