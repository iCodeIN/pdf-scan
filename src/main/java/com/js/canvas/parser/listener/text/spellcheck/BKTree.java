package com.js.canvas.parser.listener.text.spellcheck;

import java.util.*;

/**
 * This class implements a generic BK tree datastructure
 */
public class BKTree<T> {

    // the BKTree.Metric being used for this BKtree
    private Metric<T> metric = null;
    // the root node
    private Node<T> root = null;

    /**
     * Construct a new BKTree with a given BKTree.Metric
     *
     * @param m
     */
    public BKTree(Metric m) {
        this.metric = m;
    }

    /**
     * Add an object to this BKtree
     *
     * @param obj the object to be added to the tree
     * @return true if the object was added successfully, false otherwise
     */
    public boolean add(T obj) {
        if (root == null) {
            root = new Node<>(obj);
            return true;
        } else {
            return root.add(new Node<>(obj), metric);
        }
    }

    /**
     * Add a collection of objects to this BKtree
     *
     * @param other the collection to be added into this BKtree
     * @return true if any of the objects was added successfully, false otherwise
     */
    public boolean addAll(Collection<T> other) {
        boolean treeChanged = false;
        for (T obj : other)
            treeChanged |= add(obj);
        return treeChanged;
    }

    /**
     * Check whether this BKTree contains a given object
     *
     * @param obj the object to be searched
     * @return true if this BKTree contains the query object, false otherwise
     */
    public boolean contains(T obj) {
        return contains(obj, 0);
    }

    /**
     * Check whether this BKTree contains a given object
     *
     * @param obj         the object to be searched
     * @param maxDistance the maximum tolerance allowed when matching the query object
     * @return true if this BKTree contains the query object, false otherwise
     */
    public boolean contains(T obj, int maxDistance) {
        return !get(obj, maxDistance).isEmpty();
    }

    /**
     * Test whether this BKtree is empty
     *
     * @return true if this BKTree contains no nodes, false otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Get the size of this BKTree
     *
     * @return the number of nodes in this BKTree
     */
    public int size() {
        if (root == null)
            return 0;
        return root.size();
    }

    /**
     * Retrieve all objects inside this BKTree that match the query object within a specified tolerance
     *
     * @param obj         the query object
     * @param maxDistance the maximal allowed tolerance when matching the query object
     * @return
     */
    public Collection<T> get(T obj, int maxDistance) {
        if (root == null)
            return new HashSet<>();
        else {
            Set<T> out = new HashSet<>();
            for (Node<T> n : root.get(obj, metric, maxDistance)) {
                out.add(n.data);
            }
            return out;
        }
    }

    public Object[] toArray() {
        int N = size();
        if (N == 0)
            return new Object[]{};

        Object[] out = new Object[N];
        int i = 0;
        for (Node<T> n : root.all()) {
            out[i] = n.data;
            i++;
        }
        return out;
    }

    public void clear() {
        root = null;
    }

    private void graph() {
        graph(root, 0);
    }

    private void graph(Node n, int level) {
        String indent = "";
        for (int i = 0; i < level; i++)
            indent += "   ";
        System.out.println(indent + n.data.hashCode() + "");
        for (Object c : n.children.values()) {
            graph((Node) c, level + 1);
        }
    }

    /**
     * define metric for object of type T
     *
     * @param <T>
     */
    public interface Metric<T> {
        int distance(T obj0, T obj1);
    }

    /**
     * define a node type for a BK tree
     *
     * @param <T>
     */
    static class Node<T> {
        // child nodes
        private Map<Integer, Node<T>> children;

        // parent node
        private Node parent;

        // data inside the node
        private T data;

        /**
         * Construct a new BKTree.Node with given data
         *
         * @param obj
         */
        public Node(T obj) {
            this.data = obj;
            this.children = new HashMap<>();
            this.parent = null;
        }

        /**
         * add a BKTree.Node to this BKTree.Node
         * This method might recursively call itself on the children of the current BKTree.Node
         *
         * @param c BKTree.Node to be added
         * @param m BKTree.Metric to be used when inserting items in the subtree
         * @return true if the node was added successfully, false otherwise
         */
        public boolean add(Node<T> c, Metric<T> m) {
            int dist = m.distance(c.data, data);
            if (dist == 0)
                return false;
            if (children.containsKey(dist))
                return children.get(dist).add(c, m);
            else {
                children.put(dist, c);
                c.parent = this;
                return true;
            }
        }

        /**
         * Retrieve all BKTree.Nodes that match the given data, with a given tolerance
         *
         * @param obj       the data to be searched for
         * @param m         the BKTree.Metric to be used when comparing data inside the BKTree.Node
         * @param tolerance the tolerance to be considered when traversing the BKTree
         * @return
         */
        public Collection<Node<T>> get(T obj, Metric<T> m, int tolerance) {
            Set<Node<T>> out = new HashSet<>();

            // check self
            int distance = m.distance(data, obj);
            if (distance < tolerance)
                out.add(this);

            // easy case : no children
            if (children.isEmpty())
                return out;

            // recursion
            int lowerbound = Math.max(1, distance - tolerance);
            int upperbound = distance + tolerance;
            for (int i = lowerbound; i < upperbound; i++) {
                if (children.containsKey(i)) {
                    Node<T> child = children.get(i);
                    out.addAll(child.get(obj, m, tolerance));
                }
            }

            // default
            return out;
        }

        /**
         * Get the size of the subtree rooted at this BKTree.Node
         *
         * @return the size of the subtree rooted at this BKtree.Node
         */
        public int size() {
            int s = 0;
            for (Node<T> n : children.values())
                s += n.size();
            s++; // add self
            return s;
        }

        public List<Node<T>> all() {
            List<Node<T>> tmp = new ArrayList<>();
            all(tmp);
            return tmp;
        }

        private void all(Collection<Node<T>> tmp) {
            tmp.add(this);
            for (Node<T> n : children.values())
                n.all(tmp);
        }
    }
}