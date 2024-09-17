/**
*
*
*This data structure is designed for rapid data retrieval, offering the efficiency 
*of both a list and a hashtable. It enables O(1) access to data for a specific date, 
*as well as O(1) access to data spanning a specified number of days prior to any 
*stored date.
* @author Oskar
* @version 1.0
* @since 2024-01-06
*/

package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HashArray<K, V> implements Iterable<V> {

    private Map<K, IndexAndValue> keyValueMap;
    private List<K> keyList;

    public HashArray() {
        this.keyValueMap = new HashMap<>();
        this.keyList = new ArrayList<>();
    }

    public HashArray(HashArray<K, V> hashArray) {
        this.keyValueMap = new HashMap<>(hashArray.keyValueMap);
        this.keyList = new ArrayList<>(hashArray.keyList);
    }

    private class IndexAndValue {
        private int index;
        private V value;

        public IndexAndValue(int index, V value) {
            this.index = index;
            this.value = value;
        }

        public final int getIndex() { return index; }

        public final V getValue() { return value; }

    }

    public boolean hasData(K key) { return contains(key); }

    public boolean hasData(K key, int loockback) {
        return hasData(key) && indexOf(key) > loockback;
    }

    public int size() { return keyList.size(); }

    public boolean isEmpty() { return keyList.isEmpty(); }

    public V get(K key) {
        if (!contains(key)) {
            return null;
        }
        return keyValueMap.get(key).getValue();
    }

    public void clear() {
        keyValueMap.clear();
        keyList.clear();
    }

    public V getAref(K key, int lookback) {
        if (!contains(key)) {
            throw new IllegalArgumentException(
                    "Key " + key + " is not available");
        }
        int indexKey = indexOf(key);
        int indexAref = indexKey - lookback;

        boolean indexOutOfBounds = indexAref < 0;
        if (indexOutOfBounds) {
            throw new IndexOutOfBoundsException(
                    "Index " + indexAref + " is out of bounds");
        }

        K keyAref = keyList.get(indexAref);
        return keyValueMap.get(keyAref).getValue();
    }

    public int indexOf(K key) {
        if (!contains(key)) {
            throw new IndexOutOfBoundsException(
                    "Key " + key + " is not available");
        }
        return keyValueMap.get(key).getIndex();
    }

    public K getKeyAtIndex(int index) {
        return keyList.get(index);
    }

    public K getArefKey(K key, int lookback) {
        return keyList.get(indexOf(key) - lookback);
    }

    /**
     * 
     * @param key   (Date)
     * @param value
     * 
     *              The keys (dates) need to be putted in sorted order.
     */
    public void put(K key, V value) {
        if (contains(key)) {
            // Keep the same index
            keyValueMap.get(key).value = value;
            return;
        }

        keyList.add(key);
        keyValueMap.put(key,
                new IndexAndValue(keyList.size() - 1, value));
    }

    public boolean contains(K key) {
        return keyValueMap.containsKey(key);
    }

    /*
     * This is O(1)
     */
    public List<K> getKeyList() {
        return new ArrayList<>(keyList);
    }

    /*
     * This is Theta(n)
     */
    public List<V> getValueList() {
        List<V> valueList = new ArrayList<>();
        this.forEach(valueList::add);
        return valueList;
    }

    public V getValueAtIndex(int index) {
        return keyValueMap.get(keyList.get(index)).getValue();
    }

    public void sortBasedKey() {
        keyList.sort((k1, k2) -> {
            if (k1 instanceof Comparable
                    && k2 instanceof Comparable) {
                @SuppressWarnings("unchecked")
                Comparable<K> compKey1 = (Comparable<K>) k1;
                return compKey1.compareTo(k2);
            }
            throw new IllegalStateException(
                    "Key is not comparable");
        });

        // Update indeks
        for (int i = 0; i < size(); i++) {
            K key = getKeyAtIndex(i);
            keyValueMap.get(key).index = i;
        }
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            private final Iterator<K> keyIterator = keyList
                    .iterator();

            @Override
            public boolean hasNext() {
                return keyIterator.hasNext();
            }

            @Override
            public V next() {
                K key = keyIterator.next();
                return get(key);
            }

        };
    }

    // This work for Integer keys date yyyyymmdd
    @SuppressWarnings("unchecked")
    public K getClosestKey(K key) {
        if (!(key instanceof Integer)) {
            throw new IllegalArgumentException(
                    "Key must be an instance of Integer.");
        }

        // Assuming keyList is sorted
        if (keyList.isEmpty()) {
            throw new IllegalStateException("KeyList is empty");
        }
        int target = (Integer) key;
        if (target < ((Integer) keyList.get(0))) {

            throw new IllegalStateException(
                    "There is no key before the target key: "
                            + key + ".First key is: "
                            + keyList.get(0));

        }
        int index = Collections
                .binarySearch((List<Integer>) keyList, target);

        if (index >= 0) {
            return keyList.get(index);
        }

        // If the target is not found, binarySearch returns (-(insertion point) - 1)
        int insertionPoint = -index - 1;

        // Return the number just before the insertion point if it exists
        if (insertionPoint > 0) {
            return keyList.get(insertionPoint - 1);
        }
        throw new IllegalStateException(
                "Unexpected error in getClosestKey");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (K key : keyList) {
            sb.append("[" + key + " : " + get(key) + "] \n");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    // Simple demo/test
    public static void main(String[] args) {
        HashArray<Integer, Double> hashArray = new HashArray<>();

        hashArray.put(20231231, 200.0);
        hashArray.put(20240102, 202.5);
        hashArray.put(20240101, 203.0);

        System.out.println("Key before 20240101 value: "
                + hashArray.getAref(20240101, 1));

        System.out.println("\nAll values in oreder:");
        System.out.println(hashArray);

        hashArray.sortBasedKey();
        System.out.println(
                "\nShould be similar to above but in sorted order: ");
        System.out.println(hashArray);

        System.out.println("Should be 203.0: "
                + hashArray.getAref(20240102, 1));
        System.out.println(
                "Should be 203.0: " + hashArray.get(20240101));

        System.out.println(
                "Should be 2: " + hashArray.indexOf(20240102));

    }

}
