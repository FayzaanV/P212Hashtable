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
        // Initialize each element of the array to hold an empty LinkedList
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

    /**
     * Inner class to store key-value pair for our hashmap. Each element of the array will be a LinkedList of Pair objects
     */
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
     * No need to check for nullity. The method which call this helper already ensure the key is not null
     * @return the index of the array which the key should be stored at
     * @param key is the key we're hashing.
     */
    private int hash(KeyType key) {
        int hc = key.hashCode();
        return (Math.abs(hc) % table.length);
    }

    /**
     * This method insets a key-value Pair into the array
     * @param key is the key
     * @param value is its corresponding value. These two valuew will be used to create a Pair object
     * @throws NullPointerException if the key parameter is null
     * @throws IllegalArgumentException if the key is already contained in the hash table map
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        // Check exception conditions
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (containsKey(key)) {
            throw new IllegalArgumentException("Key is already stored");
        }
        // Create Pair object
        Pair tuple = new Pair(key, value);
        // Find correct index and insert Pair to the LinkedList at that array index
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        target.add(tuple);
        //Update lf and resize
        updateLF();
        if (lf >= THRESHOLD) {
            resize();
        }
  }

    /**
     * This method checks if a key is currently stored in the hash table map
     * @param key is the key we're looking for
     * @returns true if the key found, false if not
     * @throws NullPointerException if the key parameter is null
     */
    @Override
    public boolean containsKey(KeyType key) {
        // Null check
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        // Find what index key should be at. This way, we don't have to search every LinkedList
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        // Search the LinkedList at that element for a Pair object with that key
        for(Pair p : target) {
            if (p.key.equals(key)) {
                return true;
            }
        }
        // If we get through the entire LinkedList, the key is not in the array and return false
        return false;
    }

    /**
     * This method returns the value associated with a certain key
     * @param key is the key we're looking for
     * @returns the value corresponding to that key
     * @throws NullPointerException if the key parameter is null
     * @throws NoSuchElementException if the key cannot be found in the array
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        // Null check
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        // Perform our own lookup instead of using the containsKey() method. This way, we only have to traverse the LinkedList once
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        for(Pair p : target) {
            if (p.key.equals(key)) {
                return p.value;
            }
        }
        // If the key is not in this LinkedList, it doesn't exist in the hash table map.
        throw new NoSuchElementException("Key not found in the HashTableMap");
    }

    /**
     * This method removes the key-value pair associated with a certain key
     * @param key is the key we're looking for
     * @returns the value corresponding to that key
     * @throws NullPointerException if the key parameter is null
     * @throws NoSuchElementException if the key cannot be found in the array
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        // Null check
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        // Perform our own lookup instead of using the containsKey() method. This way, we only have to traverse the LinkedList once
        int i = hash(key);
        LinkedList<Pair> target = table[i];
        for(Pair p : target) {
            if (p.key.equals(key)) {
                target.remove(p);
                // Update the load factor after removing the element
                updateLF();
                return p.value;
            }
        }
        // If the key is not in this LinkedList, it doesn't exist in the hash table map.
        throw new NoSuchElementException("Key not found in the HashTableMap");
    } 

    /**
     * This method removes all key-value pairs from the array
     * I do this by instantiating a new array of the same size and pointing table to that array
     */
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
     * @return the total number of keys in the array
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
     * @return the capacity of the hash table map
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
     * @return the load factor field
     */
    public double getLF() {
        return lf;
    }

    /**
     * This method returns a list of every key stored in the hash table map
     * @return a LinkedList of keys
     */
    @Override
    public LinkedList<KeyType> getKeys() {
        LinkedList<KeyType> allKeys = new LinkedList<>();
        // Iterate through the array. For the LinkedList at each element, add every key stored there
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
        // store original capacity and all the Pairs
        int origCapacity = getCapacity();
        LinkedList<Pair> allPairs = new LinkedList<>();
        for(LinkedList<Pair> l : table) {
            allPairs.addAll(l);
        }
        // Set table equal to a new array twice the size, instantiated with empty LinkedLists
        table = (LinkedList<Pair>[]) new LinkedList[origCapacity * 2];
        for (int i = 0; i < origCapacity * 2; i++) {
            table[i] = new LinkedList<>();
        }
        // For every Pair previously, re-insert it into the new array
        // Since this uses the put() method, the index for each key is re-calculated.
        for (Pair p : allPairs) {
            put(p.key, p.value);
        }
    }

    ////////////////////////////////////////////////////////// TESTER METHODS //////////////////////////////////////////////////////////
     
    /**
     * This tester method tests the put() method, and that the resize operation is performed at the correct load factors
     * This is done by inserting values and checking the number of keys and the capacity of the hash table map
     */
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
        // Inserting Hoerner should create a lf of 1 and lead to the capacity being doubled
        map.put(2, "Hoerner");
        assertEquals(map.getSize(), 3);
        assertEquals(map.getCapacity(), 6);
        map.put(29, "Busch");
        assertEquals(map.getSize(), 4);
        assertEquals(map.getCapacity(), 6);
        // Adding Swanson should lead to a lf of 0.83 and cause the capacity to be doubled again
        map.put(7, "Swanson");
        assertEquals(map.getSize(), 5);
        assertEquals(map.getCapacity(), 12);
    }

    /**
     * This tester method tests the containsKey(), get(), and remove() methods. It does so by inserting many key-value pairs,
     * then checking that containsKey() and get() return the correct values, and that return() also returns the correct values and that 
     * it correctly updates the number of keys.
     */
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
        assertFalse(map.containsKey(9));
        assertFalse(map.containsKey(17));
        assertFalse(map.containsKey(58));
        assertEquals(map.get(10), "Burden III");
        assertEquals(map.get(85), "Kmet");
        assertEquals(map.getSize(), 7);
        assertEquals(map.remove(4), "Swift");
        assertEquals(map.remove(25), "Monongai");
        assertEquals(map.getSize(), 5);
    }

    /**
     * This tester method tests every case in which an Exception should be thrown. It does this by calling methods with incorrect parameters, and
     * then using try catch blocks it ensures the test fails if no error or the wrong error is thrown.
     */
    @SuppressWarnings("unused")
    @Test
    public void testErrorHandling() {
        // The constructor ensures the original capacity is always at least one. This should throw an IllegalArgumentException
        try {
            HashTableMap<Integer, String> wrongMap = new HashTableMap<>(-17);
            fail("Constructor threw no exception for a negative input");
        } catch (IllegalArgumentException e) {

        } catch (Exception e) {
            fail("Constructor threw wrong exception for a negative input");
        }

        // The put() shouldn't allow duplicate keys to be inserted into the map
        HashTableMap<Integer, String> map = new HashTableMap<>();
        map.put(4, "Swift");
        try {
            map.put(4, "Crow-Armstrong");
            fail("put() threw no exception for a duplicate input");
        } catch (IllegalArgumentException e) {

        } catch (Exception e) {
            fail("put() threw wrong exception for a duplicate input");
        }

        // get() and remove() both throw NoSuchElementExceptions when the key does not exist in the map
        try {
            map.get(70);
            fail("get() threw no exception for a non-existing input");
        } catch (NoSuchElementException e) {

        } catch (Exception e) {
            fail("get() threw wrong exception for a non-existing input");
        }
        try {
            map.remove(62);
            fail("remove() threw no exception for a non-existing input");
        } catch (NoSuchElementException e) {

        } catch (Exception e) {
            fail("remove() threw no exception for a non-existing input");
        }

        // put(), containsKey(), return(), and get() should all throw NullPointerExceptions with null inputs.
        try {
            map.put(null, "Zero");
            fail("put() threw no exception for a null input");
        } catch (NullPointerException e) {

        } catch (Exception e) {
            fail("put() threw wrong exception for a null input");
        }
        try {
            map.containsKey(null);
            fail("containsKey() threw no exception for a null input");
        } catch (NullPointerException e) {

        } catch (Exception e) {
            fail("containsKey() threw wrong exception for a null input");
        }
        try {
            map.get(null);
            fail("get() threw no exception for a null input");
        } catch (NullPointerException e) {

        } catch (Exception e) {
            fail("get() threw wrong exception for a null input");
        }
        try {
            map.remove(null);
            fail("remove() threw no exception for a null input");
        } catch (NullPointerException e) {

        } catch (Exception e) {
            fail("remove() threw wrong exception for a null input");
        }
    }

    /**
     * This test tests the rehashing done by the resize method. It does this using the order of the getKeys() method. The way I've
     * written the getKeys() method, it should return keys in the order of the LinkedList they are in, in the order of the array indices
     * from 0 ascending.
     */
    @Test
    public void testRehashing() {
        HashTableMap<Integer, String> map = new HashTableMap<>(3);
        map.put(12, "Bush"); //should be at index 0
        map.put(2, "Bryant"); //should be at index 2
        assertEquals(map.getKeys().get(0), 12);
        assertEquals(map.getKeys().get(1), 2);
        map.put(1, "Johnson"); //This should trigger a resize operation
        assertEquals(map.getLF(), 0.5);
        map.put(6, "Gordon");
        // Now the order should be Bush, Gordon, Johnson, Bryant
        assertEquals(map.getKeys().get(0), 12);
        assertEquals(map.getKeys().get(1), 6);
        assertEquals(map.getKeys().get(2), 1);
        assertEquals(map.getKeys().get(3), 2);
        map.put(98, "Sweat");
        map.put(53, "Edwards");
        assertEquals(map.getLF(), 0.5);
        // Now the order should be Bush, Johnson, Bryant, Sweat, Edwards, Gordon
        assertEquals(map.getKeys().get(0), 12);
        assertEquals(map.getKeys().get(1), 1);
        assertEquals(map.getKeys().get(2), 2);
        assertEquals(map.getKeys().get(3), 98);
        assertEquals(map.getKeys().get(4), 53);
        assertEquals(map.getKeys().get(5), 6);
    }

    /**
     * This test tests that the getSize() and getCapacity() methods work properly under insertions, resizing, and when the clear() method is called.
     */
    @Test
    public void testClearAndGetters() {
        HashTableMap<Integer, String> map = new HashTableMap<>();
        assertEquals(map.getSize(), 0);
        assertEquals(map.getCapacity(), 8);
        map.put(49, "Arrieta");
        map.put(9, "Baez");
        map.put(17, "Bryant");
        map.put(44, "Rizzo");
        map.put(12, "Schwarber");
        assertEquals(map.getSize(), 5);
        assertEquals(map.getCapacity(), 8);
        map.put(18, "Zobrist"); // Resize operation triggered here
        map.put(24, "Fowler");
        assertEquals(map.getSize(), 7);
        assertEquals(map.getCapacity(), 16);
        // clear() should set keys to 0 and getKeys() should be empty, but capacity should be unchanged
        map.clear();
        assertEquals(map.getSize(), 0);
        assertEquals(map.getCapacity(), 16);
        assertTrue(map.getKeys().isEmpty());
    }
}
