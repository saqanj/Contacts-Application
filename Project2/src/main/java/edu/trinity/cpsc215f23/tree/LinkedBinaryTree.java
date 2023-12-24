package edu.trinity.cpsc215f23.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of a binary tree using a node-based, linked
 * structure.
 *
 * @author Michael T. Goodrich
 * @author Roberto Tamassia
 * @author Michael H. Goldwasser
 */
public class LinkedBinaryTree<E> extends AbstractBinaryTree<E> {

    /**
     * Root of the tree.
     */
    protected Node<E> root = null;

    /**
     * Number of nodes in the tree.
     */
    protected int size = 0;

    /**
     * Factory function to create a new node storing the given element.
     *
     * @param element The element being added to the node.
     * @param parent  The parent of the new node.
     * @param left    The node to the left of the new node.
     * @param right   The node to the right to the new node.
     * @return The newly created node.
     */
    protected Node<E> createNode(E element, Node<E> parent, Node<E> left, Node<E> right) {
        return new Node<>(element, parent, left, right);
    }

    /**
     * Verifies that a Position belongs to the appropriate class, and is not
     * one that has been previously removed. Note that our current
     * implementation does not actually verify that the position belongs to
     * this particular list instance.
     *
     * @param position a Position (that should belong to this tree)
     * @return the underlying Node instance for the position
     * @throws IllegalArgumentException if an invalid position is detected
     */
    protected Node<E> validate(Position<E> position) throws IllegalArgumentException {
        if (!(position instanceof Node<E> node)) {
            throw new IllegalArgumentException("Not a valid position type.");
        }

        // safe cast
        if (node.getParent() == node)     // Convention for defunct node
        {
            throw new IllegalArgumentException("position is no longer in the tree");
        }

        return node;
    }

    /**
     * Returns the number of nodes in the tree.
     *
     * @return number of nodes in the tree
     */
    @Override
    public int size() {
        return size;
    }

    // accessor methods (not already implemented in AbstractBinaryTree)

    /**
     * Returns the root Position of the tree (or null if tree is empty).
     *
     * @return root Position of the tree (or null if tree is empty)
     */
    public Position<E> root() {
        return root;
    }

    /**
     * Returns the Position of position's parent (or null if position is root).
     *
     * @param position a valid Position within the tree
     * @return Position of position's parent (or null if position is root)
     * @throws IllegalArgumentException if position is not a valid Position
     */
    public Position<E> parent(Position<E> position) throws IllegalArgumentException {
        return validate(position).getParent();
    }

    /**
     * Returns the Position of position's left child (or null if no child exists).
     *
     * @param position a valid Position within the tree
     * @return the Position of the left child (or null if no child exists)
     * @throws IllegalArgumentException if position is not a valid Position
     */
    public Position<E> left(Position<E> position) throws IllegalArgumentException {
        return validate(position).getLeft();
    }

    /**
     * Returns the Position of position's right child (or null if no child exists).
     *
     * @param position a valid Position within the tree
     * @return the Position of the right child (or null if no child exists)
     * @throws IllegalArgumentException if position is not a valid Position
     */
    public Position<E> right(Position<E> position) throws IllegalArgumentException {
        return validate(position).getRight();
    }

    /**
     * Places the given element at the root of an empty tree and returns its new
     * Position.
     *
     * @param element the new element
     * @return the Position of the new element
     * @throws IllegalStateException if the tree is not empty
     */
    public Position<E> addRoot(E element) throws IllegalStateException {
        if (!isEmpty()) {
            throw new IllegalStateException("Tree is not empty.");
        }
        root = createNode(element, null, null, null);
        size = 1;

        return root;
    }

    // update methods supported by this class

    /**
     * Creates a new left child of the given position storing the given element and returns its
     * Position.
     *
     * @param position the Position to the left of which the new element is inserted
     * @param element  the new element
     * @return the Position of the new element
     * @throws IllegalArgumentException if position is not a valid Position
     * @throws IllegalArgumentException if position already has a left child
     */
    public Position<E> addLeft(Position<E> position, E element) throws IllegalArgumentException {
        Node<E> parent = validate(position);
        if (parent.getLeft() != null) {
            throw new IllegalArgumentException("Position already has a left child.");
        }
        Node<E> child = createNode(element, parent, null, null);
        parent.setLeft(child);
        size++;

        return child;
    }

    /**
     * Creates a new right child of the given position storing the given element and returns
     * its Position.
     *
     * @param position the Position to the right of which the new element is inserted
     * @param element  the new element
     * @return the Position of the new element
     * @throws IllegalArgumentException if position is not a valid Position
     * @throws IllegalArgumentException if position already has a right child
     */
    public Position<E> addRight(Position<E> position, E element) throws IllegalArgumentException {
        Node<E> parent = validate(position);
        if (parent.getRight() != null) {
            throw new IllegalArgumentException("Position already has a right child.");
        }
        Node<E> child = createNode(element, parent, null, null);
        parent.setRight(child);
        size++;

        return child;
    }

    /**
     * Replaces the element at the given position with the given element and returns the
     * replaced element.
     *
     * @param position the relevant Position
     * @param element  the new element
     * @return the replaced element
     * @throws IllegalArgumentException if position is not a valid Position
     */
    public E set(Position<E> position, E element) throws IllegalArgumentException {
        Node<E> node = validate(position);
        E temp = node.getElement();
        node.setElement(element);

        return temp;
    }

    /**
     * Attaches trees t1 and t2, respectively, as the left and right subtree of
     * the leaf the given position. As a side effect, t1 and t2 are set to empty trees.
     *
     * @param position a leaf of the tree
     * @param t1       a tree whose structure becomes the left child of position
     * @param t2       a tree whose structure becomes the right child of position
     * @throws IllegalArgumentException if position is not a valid Position
     * @throws IllegalArgumentException if position is not a leaf
     */
    public void attach(Position<E> position, LinkedBinaryTree<E> t1,
                       LinkedBinaryTree<E> t2) throws IllegalArgumentException {
        Node<E> node = validate(position);
        if (isInternal(position)) {
            throw new IllegalArgumentException("position must be a leaf");
        }
        size += t1.size() + t2.size();
        if (!t1.isEmpty()) {                  // attach t1 as left subtree
            t1.root.setParent(node);
            node.setLeft(t1.root);
            t1.root = null;
            t1.size = 0;
        }
        if (!t2.isEmpty()) {                  // attach t2 as right subtree
            t2.root.setParent(node);
            node.setRight(t2.root);
            t2.root = null;
            t2.size = 0;
        }
    }

    /**
     * Removes the node at the given position and replaces it with its child, if any.
     *
     * @param position the relevant Position
     * @return element that was removed
     * @throws IllegalArgumentException if position is not a valid Position
     * @throws IllegalArgumentException if position has two children.
     */
    public E remove(Position<E> position) throws IllegalArgumentException {
        Node<E> node = validate(position);
        if (numChildren(position) == 2) {
            throw new IllegalArgumentException("position has two children");
        }
        Node<E> child = (node.getLeft() != null ? node.getLeft() : node.getRight());
        if (child != null) {
            child.setParent(node.getParent());  // grandparent becomes its parent
        }
        if (node == root) {
            root = child;                       // child becomes root
        } else {
            Node<E> parent = node.getParent();
            if (node == parent.getLeft()) {
                parent.setLeft(child);
            } else {
                parent.setRight(child);
            }
        }
        size--;
        E temp = node.getElement();
        node.setElement(null);                // help garbage collection
        node.setLeft(null);
        node.setRight(null);
        node.setParent(node);                 // our convention for defunct node

        return temp;
    }

    /**
     * Generates an iterable pre-ordered collection of the Linked Binary Tree.
     *
     * @return Iterable collection of elements in linked binary tree.
     */
    public Iterable<E> preorderElements() {
        List<E> list = new ArrayList<>();

        if (!isEmpty()) {
            preorderElements(root(), list);
        }

        return list;
    }


    /**
     * Auxiliary method which preorders the elements of a tree given a preordered list to return and the position from which to iterate.
     *
     * @param v position from which to iterate.
     * @param l A list to return containing pre-ordered iteration of the tree.
     */
    protected void preorderElements(Position<E> v, List<E> l) {
        l.add(l.size(), v.getElement());
        if (left(v) != null) {
            preorderElements(left(v), l);
        }
        if (right(v) != null) {
            preorderElements(right(v), l);
        }
    }

    /**
     * A method for returning an iterable collection of inordered tree elements.
     *
     * @return Iterable collection of inordered tree elements.
     */
    public Iterable<E> inorderElements() {
        List<E> list = new ArrayList<>();

        if (!isEmpty()) {
            inorderElements(root(), list);
        }

        return list;
    }

    /**
     * Auxiliary method which inorders the elements of a tree given an in-order list to return and the position from which to iterate.
     *
     * @param v A position from which to begin iteration.
     * @param l The list to which the inordered list should be returned.
     */
    protected void inorderElements(Position<E> v, List<E> l) {
        if (left(v) != null) {
            inorderElements(left(v), l);
        }
        l.add(l.size(), v.getElement());
        if (right(v) != null) {
            inorderElements(right(v), l);
        }
    }

    /**
     * A method for generating a post-ordered iterable collection of tree elements.
     *
     * @return The post-ordered iterable collection of tree elements.
     */
    public Iterable<E> postorderElements() {
        List<E> list = new ArrayList<>();

        if (!isEmpty()) {
            postorderElements(root(), list);
        }

        return list;
    }

    /**
     * Auxiliary method which post-orders the elements of a tree given a post-order list to return and the position from which to iterate.
     *
     * @param v A position from which to begin iteration.
     * @param l A list to return the post-ordered iteration of tree elements.
     */
    protected void postorderElements(Position<E> v, List<E> l) {
        if (left(v) != null) {
            postorderElements(left(v), l);
        }
        if (right(v) != null) {
            postorderElements(right(v), l);
        }
        l.add(l.size(), v.getElement());
    }

    /**
     * Nested static class for a binary tree node.
     */
    protected static class Node<E> implements Position<E> {
        /**
         * An element stored at this node.
         */
        private E element;

        /**
         * A reference to the parent node (if any).
         */
        private Node<E> parent;

        /**
         * A reference to the left child (if any).
         */
        private Node<E> left;

        /**
         * A reference to the right child (if any).
         */
        private Node<E> right;

        /**
         * Constructs a node with the given element and neighbors.
         *
         * @param e          the element to be stored
         * @param above      reference to a parent node
         * @param leftChild  reference to a left child node
         * @param rightChild reference to a right child node
         */
        public Node(E e, Node<E> above, Node<E> leftChild, Node<E> rightChild) {
            element = e;
            parent = above;
            left = leftChild;
            right = rightChild;
        }


        /**
         * Accesses an element from a node.
         *
         * @return The node's element.
         */
        public E getElement() {
            return element;
        }

        /**
         * Updater method for setting the element of a node.
         *
         * @param element The element to which the node's current element needs to be set.
         */
        public void setElement(E element) {
            this.element = element;
        }

        /**
         * Accessor method for getting the parent of a node.
         *
         * @return the node's parent.
         */
        public Node<E> getParent() {
            return parent;
        }

        /**
         * Updater method for setting a node's parent to be another given node.
         *
         * @param parentNode The node the current parent node needs to be set to.
         */
        public void setParent(Node<E> parentNode) {
            parent = parentNode;
        }

        /**
         * Accessor method for getting the node left of a current node.
         *
         * @return The node left of a current node.
         */
        public Node<E> getLeft() {
            return left;
        }

        /**
         * Updater method for setting a node's left node to be another given node.
         *
         * @param leftChild The node the current left node needs to be set to.
         */
        public void setLeft(Node<E> leftChild) {
            left = leftChild;
        }

        /**
         * Accessor method for getting a current node's right node.
         *
         * @return The right node to a current node.
         */
        public Node<E> getRight() {
            return right;
        }

        /**
         * Updater method for changing the current node's right node to be another given node.
         *
         * @param rightChild The replacement right node.
         */
        public void setRight(Node<E> rightChild) {
            right = rightChild;
        }
    }

}
