package edu.trinity.cpsc215f23.treemap;

import edu.trinity.cpsc215f23.map.Entry;
import edu.trinity.cpsc215f23.map.Map;
import edu.trinity.cpsc215f23.tree.LinkedBinaryTree;
import edu.trinity.cpsc215f23.tree.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Realization of a map by means of a binary search tree.
 *
 * @author Takunari Miyazaki
 * @author Saqlain Anjum
 * @version 1.0.0
 */
public class BinarySearchTreeMap<K, V> extends LinkedBinaryTree<Entry<K, V>> implements Map<K, V> {

    /**
     * A default comparator object for key values.
     */
    protected final Comparator<K> comparator; // comparator

    /**
     * A representation of a node variable as a position with a key-value entry.
     */
    protected Position<Entry<K, V>> actionPos; // a node variable


    /**
     * Creates a BinarySearchTreeMap with a default comparator.
     */
    public BinarySearchTreeMap() {
        comparator = new DefaultComparator<>();
        addRoot(null);
    }

    /**
     * Creates a BinarySearchTreeMap without a default comparator.
     *
     * @param comparator Specified comparator object.
     */
    public BinarySearchTreeMap(Comparator<K> comparator) {
        this.comparator = comparator;
        addRoot(null);
    }

    /**
     * In this collection only internal nodes have entries.
     * Returns the size of the tree taking that into account.
     *
     * @return the number of entries in the map.
     */
    public int size() {
        return (super.size() - 1) / 2;
    }

    /**
     * Extracts the key of the entry at a given node of the tree.
     *
     * @param position A given node in a tree from which to extract a key.
     * @return the key of the entry at a given node of the tree.
     */
    protected K key(Position<Entry<K, V>> position) {
        return position.getElement().getKey();
    }

    /**
     * Extracts the value of the entry at a given node of the tree.
     *
     * @param position A given node in a tree from which to extract a value.
     * @return The value of the entry at a given node of the tree.
     */
    protected V value(Position<Entry<K, V>> position) {
        return position.getElement().getValue();
    }

    /**
     * Extracts the entry at a given node of the tree.
     *
     * @param position A given node in a tree from which to extract an entry.
     * @return The entry at a given node of the tree.
     */
    protected Entry<K, V> entry(Position<Entry<K, V>> position) {
        return position.getElement();
    }

    /**
     * Replaces an entry with a new entry (and reset the entry's location).
     *
     * @param position A given node whose entry needs replacement.
     * @param entry    The new replacing entry.
     * @return The value of the newly added entry.
     */
    protected V replaceEntry(Position<Entry<K, V>> position, Entry<K, V> entry) {
        ((BSTEntry<K, V>) entry).position = position;
        return set(position, entry).getValue();
    }

    /**
     * Checks whether a given key is valid.
     *
     * @param key The key being checked.
     * @throws IllegalArgumentException If the key is null.
     */
    protected void checkKey(K key) throws IllegalArgumentException {
        if (key == null) // just a simple test for now
        {
            throw new IllegalArgumentException("Key is null.");
        }
    }

    /**
     * Checks whether a given entry is valid.
     *
     * @param entry The entry being checked.
     * @throws IllegalArgumentException If the entry is not an instance of BSTEntry.
     */
    protected void checkEntry(Entry<K, V> entry) throws IllegalArgumentException {
        if (!(entry instanceof BSTEntry)) {
            throw new IllegalArgumentException("Entry is invalid, expecting type BSTEntry.");
        }
    }

    /**
     * Auxiliary method for inserting an entry at an external node. Inserts a given entry at a given external position,
     * expanding the external node to be internal with empty external children, and then returns the inserted entry.
     * (page 11.1.2)
     *
     * @param position The external position.
     * @param entry    The entry to add to the external.
     * @return The entry added.
     */
    protected Entry<K, V> insertAtExternal(Position<Entry<K, V>> position, Entry<K, V> entry) {
        this.set(position, entry);
        this.addLeft(position, null);
        this.addRight(position, null);

        return entry;
    }

    /**
     * Auxiliary method for removing an external node and its parent. Removes a given external node and its
     * parent, replacing external's parent with external's sibling.
     *
     * @param external The position to remove.
     */
    protected void removeExternal(Position<Entry<K, V>> external) {
        if (isExternal(external)) {
            Position<Entry<K, V>> externalParent = this.parent(external);
            this.remove(external);
            this.remove(externalParent);
        }
    }

    /**
     * Search the tree, starting at the root.
     *
     * @param key The node to search for.
     * @return The found node position.
     */
    private Position<Entry<K, V>> treeSearch(K key) {
        return treeSearch(key, root());
    }


    /**
     * An auxiliary method used by get, put, and remove.
     *
     * @param key      The node to search for.
     * @param position The starting tree position.
     * @return The found node position.
     */
    protected Position<Entry<K, V>> treeSearch(K key, Position<Entry<K, V>> position) {
        if (isExternal(position)) {
            return position; // key not found; return external node
        }
        K curKey = key(position);
        int comp = comparator.compare(key, curKey);
        if (comp < 0) {
            return treeSearch(key, left(position)); // search left subtree
        } else if (comp > 0) {
            return treeSearch(key, right(position)); // search right subtree
        }
        return position; // return internal node where key is found
    }

    /**
     * Returns a value whose associated key is k.
     *
     * @param key The key whose associated value is to be returned.
     * @return The value associated with a provided key.
     * @throws IllegalArgumentException If the key parameter is null.
     */
    public V get(K key) throws IllegalArgumentException {
        checkKey(key); // may throw an InvalidKeyException
        Position<Entry<K, V>> currentPos = treeSearch(key);
        actionPos = currentPos; // node where the search ended

        return isInternal(currentPos) ? value(currentPos) : null;
    }

    /**
     * Inserts an entry with a given key and value v into the map, returning
     * the old value whose associated key is key if it exists.
     *
     * @param key   The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @return A value of the newly added entry if the entry already exists and is being replaced.
     * @throws IllegalArgumentException If the key parameter is null.
     */
    public V put(K key, V value) throws IllegalArgumentException {
        checkKey(key); // may throw an IllegalArgumentException
        Position<Entry<K, V>> insPos = treeSearch(key);
        BSTEntry<K, V> entry = new BSTEntry<>(key, value, insPos);
        actionPos = insPos; // node where the entry is being inserted
        if (isExternal(insPos)) { // we need a new node, key is new
            insertAtExternal(insPos, entry).getValue();
            return null;
        }

        return replaceEntry(insPos, entry); // key already exists
    }


    /**
     * Removes from the map the entry whose key is k, returning the value of
     * the removed entry.
     *
     * @param key The key whose entry is to be removed from the map.
     * @return The corresponding value of the removed key.
     * @throws IllegalArgumentException If the key parameter is null.
     */
    public V remove(K key) throws IllegalArgumentException {
        checkKey(key); // may throw an IllegalArgumentException
        Position<Entry<K, V>> remPos = treeSearch(key);
        if (isExternal(remPos)) {
            return null; // key not found
        }
        Entry<K, V> toReturn = entry(remPos); // old entry
        if (isExternal(left(remPos))) {
            remPos = left(remPos); // left case
        } else if (isExternal(right(remPos))) {
            remPos = right(remPos); // right case
        } else { // entry is at a node with internal children
            Position<Entry<K, V>> swapPos = remPos; // find node for moving entry
            remPos = left(swapPos);
            do {
                remPos = right(remPos);
            } while (isInternal(remPos));
            replaceEntry(swapPos, parent(remPos).getElement());
        }
        actionPos = sibling(remPos); // sibling of the leaf to be removed
        removeExternal(remPos);

        return toReturn.getValue();
    }

    /**
     * Returns an iterable collection of the keys of all entries stored in the map.
     *
     * @return Iterable collection of all the keys stored in a map.
     */
    public Iterable<K> keySet() {
        Iterable<Entry<K, V>> entryCollection = entrySet();
        List<K> keyList = new ArrayList<>();
        for (Entry<K, V> entry : entryCollection) {
            K currKey = entry.getKey();
            keyList.add(currKey);
        }
        return keyList;
    }

    /**
     * Returns an iterable collection of the values of all entries stored in
     * the map.
     *
     * @return Iterable collection of all the values stored in a map.
     */
    public Iterable<V> values() {
        Iterable<Entry<K, V>> entryCollection = entrySet();
        List<V> valueList = new ArrayList<>();
        for (Entry<K, V> entry : entryCollection) {
            V currValue = entry.getValue();
            valueList.add(currValue);
        }
        return valueList;
    }

    /**
     * Returns an iterable collection of all entries stored in the map. The sentinels are excluded.
     *
     * @return An iterable colletion of all entries stored in the map.
     */
    public Iterable<Entry<K, V>> entrySet() {
        Iterable<Position<Entry<K, V>>> positionCollection = this.positions();
        List<Entry<K, V>> entrySet = new ArrayList<>();
        for (Position<Entry<K, V>> position : positionCollection) {
            Entry<K, V> currEntry = entry(position);
            if (currEntry == null) {
                continue;
            }
            entrySet.add(currEntry);
        }
        return entrySet;
    }

    /**
     * Overridden toString method for outputting the key-value entries of a BinarySearchTreeMap.
     *
     * @return The formatted string output of key-value entires..
     */
    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        for (Entry<K, V> entry : entrySet()) {
            toReturn.append("(");
            toReturn.append(entry.getKey());
            toReturn.append(", ");
            toReturn.append(entry.getValue());
            toReturn.append(")");
            toReturn.append(", ");
        }
        toReturn.setLength(toReturn.length() - 2);
        return toReturn.toString();
    }

    /**
     * Nested class for location-aware binary search tree entries
     */
    protected static class BSTEntry<K, V> implements Entry<K, V> {

        /**
         * Object representation of a key.
         */
        protected final K key;

        /**
         * Object representation of a value.
         */
        protected final V value;

        /**
         * Object representation of a positional key-value entry.
         */
        protected Position<Entry<K, V>> position;

        /**
         * A BSTEntry constructor using given key, value, and positional entry parameters.
         *
         * @param key      The key for a BSTEntry object.
         * @param value    The value for a BSTEntry object.
         * @param position The positional entry for a BSTEntry object.
         */
        BSTEntry(K key, V value, Position<Entry<K, V>> position) {
            this.key = key;
            this.value = value;
            this.position = position;
        }

        /**
         * An access method for getting a key from a BSTEntry.
         *
         * @return The key from a BSTEntry.
         */
        public K getKey() {
            return key;
        }

        /**
         * An access method for getting a value from a BSTEntry.
         *
         * @return The value of a BSTEntry.
         */
        public V getValue() {
            return value;
        }

        /**
         * An access method for getting a positional entry from a BSTEntry.
         *
         * @return The positional entry from a BSTEntry.
         */
        public Position<Entry<K, V>> position() {
            return position;
        }
    }
}
