package lab9;

import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of interface Map61B with BST as core data structure.
 *
 * @author Your name here
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>, Iterable<K> {

    private class Node {
        /* (K, V) pair stored in this Node. */
        private K key;
        private V value;
        private int size;
        /* Children of this Node. */
        private Node left;
        private Node right;

        private Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private Node root;  /* Root node of the tree. */
    private int size; /* The number of key-value pairs in the tree */

    /* Creates an empty BSTMap. */
    public BSTMap() {
        this.clear();
    }

    /* Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /** Returns the value mapped to by KEY in the subtree rooted in P.
     *  or null if this map contains no mapping for the key.
     */
    private V getHelper(K key, Node p) {
        if (p == null) {
            return null;
        } else if (key.equals(p.key)) {
            return p.value;
        } else if (key.compareTo(p.key) < 0) {
            return getHelper(key, p.left);
        } else {
            return getHelper(key, p.right);
        }
    }

    /** Returns the value to which the specified key is mapped, or null if this
     *  map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        return getHelper(key, root);
    }

    /** Returns a BSTMap rooted in p with (KEY, VALUE) added as a key-value mapping.
      * Or if p is null, it returns a one node BSTMap containing (KEY, VALUE).
     */
    private Node putHelper(K key, V value, Node p) {
        if (root == null) {
            root = new Node(key, value);
            return root;
        }
        if (p == null) {
            return new Node(key, value);
        } else if (key.compareTo(p.key) < 0) {
            p.left = putHelper(key, value, p.left);
        } else if (key.compareTo(p.key) > 0) {
            p.right = putHelper(key, value, p.right);
        } else {
            p.value = value;
        }
        return p;
    }

    /** Inserts the key KEY
     *  If it is already present, updates value to be VALUE.
     */
    @Override
    public void put(K key, V value) {
        size += 1;
        putHelper(key, value, root);
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    //////////////// EVERYTHING BELOW THIS LINE IS OPTIONAL ////////////////

    /* Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /** Removes KEY from the tree if present
     *  returns VALUE removed,
     *  null on failed removal.
     */
    @Override
    public V remove(K key) {
        if (key == null) {
            return null;
        }
        return removeHelper(key, root);
    }

    private V removeHelper(K key, Node node) {
        if (node == null) {
            return null;
        }
        if (key.equals(node.key)) {
            if (isLeaf(node)) {
                deleteWithoutChild(node);
            } else if (hasOneChild(node)) {
                deleteWithOneChild(node);
            } else {
                Node rightMost = rightMost(node.left);
                V temp = node.value;
                node.key = rightMost.key;
                node.value = rightMost.value;
                if (isLeaf(rightMost)) {
                    deleteWithoutChild(rightMost);
                } else {
                    deleteWithOneChild(rightMost);
                }
                size -= 1;
                return temp;
            }
            size -= 1;
            return node.value;
        } else if (key.compareTo(node.key) < 0) {
            return removeHelper(key, node.left);
        } else {
            return removeHelper(key, node.right);
        }
    }

    private Node rightMost(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    private Node findFatherNode(K key, Node node) {
        if (node == null || isLeaf(node)) {
            return null;
        }
        if (node.left != null && key.equals(node.left.key)) {
            return node;
        } else if (node.right != null && key.equals(node.right.key)) {
            return node;
        } else if (key.compareTo(node.key) < 0) {
            return findFatherNode(key, node.left);
        } else if (key.compareTo(node.key) > 0) {
            return findFatherNode(key, node.right);
        } else {
            return null;
        }
    }

    private void deleteWithOneChild(Node node) {
        Node father = findFatherNode(node.key, root);
        Node connectTo = node.left == null ? node.right : node.left;
        if (father.left != null && father.left.key.equals(node.key)) {
            father.left = connectTo;
        } else {
            father.right = connectTo;
        }
    }

    private void deleteWithoutChild(Node node) {
        Node father = findFatherNode(node.key, root);
        if (father == null) {
            root = null;
            return;
        }
        if (father.left != null && father.left.key.equals(node.key)) {
            father.left = null;
        } else {
            father.right = null;
        }
    }

    private boolean isLeaf(Node node) {
        return node != null && node.left == null && node.right == null;
    }

    private boolean hasOneChild(Node node) {
        if (node == null || node.left != null && node.right != null || isLeaf(node)) {
            return false;
        }
        return true;
    }
    /** Removes the key-value entry for the specified key only if it is
     *  currently mapped to the specified value.  Returns the VALUE removed,
     *  null on failed removal.
     **/
    @Override
    public V remove(K key, V value) {
        if (get(key) == value) {
            return remove(key);
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public boolean isBST() {
        return isBST(this.root);
    }

    private boolean isBST(Node node) {
        if (node == null) {
            return true;
        }
        if (node.left != null && node.key.compareTo(node.left.key) < 0) {
            return false;
        } else if (node.right != null && node.key.compareTo(node.right.key) > 0) {
            return false;
        }
        return isBST(node.left) && isBST(node.right);
    }
}
