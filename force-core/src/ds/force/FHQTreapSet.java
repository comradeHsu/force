package ds.force;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Random;
import java.util.SortedSet;

public class FHQTreapSet<E> implements NavigableSet<E> {

    /**
     * The comparator used to maintain order in this tree map, or
     * null if it uses the natural ordering of its keys.
     *
     * @serial
     */
    private final Comparator<? super E> comparator;

    transient Random random = new Random();

    transient Node<E> root;

    /**
     * The number of entries in the treap
     */
    private transient int size = 0;

    public FHQTreapSet(){
        comparator = null;
    }

    public FHQTreapSet(Comparator<? super E> comparator){
        this.comparator = comparator;
    }

    public Node<E> split(E item){
        Node<E> node = this.root;
        Node<E> newNode = null, splitPoint = null;
        Deque<Node<E>> stack = new ArrayDeque<>();
        while (node != null){
            if (comparator.compare(node.element,item) <= 0){
                node = node.right;
            } else {
                node = node.left;
            }
            stack.push(node);
        }
        while (!stack.isEmpty()){
            Node<E> current = stack.pop();
            if (comparator.compare(current.element,item) <= 0){
                current.right = splitPoint;
                splitPoint = current;
            } else {
                current.left = newNode;
                newNode = current;
            }
            update(current);
        }
        return newNode;
    }

    public Node<E> merge(Node<E> a, Node<E> b){
        if (a == null || b == null) return a == null ? b : a;
        if (a.priority < b.priority){
            a.right = merge(a.right,b);
            update(a);
            return a;
        } else {
            b.left = merge(a,b.left);
            update(b);
            return b;
        }
    }

    private Node<E> merge1(Node<E> a, Node<E> b){
        Node<E> c = a, d = b;
        Deque<Node<E>> stack = new ArrayDeque<>();
        while(c != null && d != null){
            stack.push(c);
            stack.push(d);
            if (c.priority < d.priority){
                c = c.right;
            } else {
                d = d.left;
            }
        }
        return null;
    }

    private void update(Node<E> node){
        node.size = sizeOf(node.left) + sizeOf(node.right) + 1;
    }

    private static <E> int sizeOf(Node<E> node){
        return node == null ? 0 : node.size;
    }

    @Override
    public E lower(E e) {
        return null;
    }

    @Override
    public E floor(E e) {
        return null;
    }

    @Override
    public E ceiling(E e) {
        return null;
    }

    @Override
    public E higher(E e) {
        return null;
    }

    @Override
    public E pollFirst() {
        return null;
    }

    @Override
    public E pollLast() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public NavigableSet<E> descendingSet() {
        return null;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return null;
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return null;
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return null;
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return null;
    }

    @Override
    public Comparator<? super E> comparator() {
        return null;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return null;
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return null;
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return null;
    }

    @Override
    public E first() {
        return null;
    }

    @Override
    public E last() {
        return null;
    }

    static class Node<E> {

        E element;

        int priority;

        Node<E> left,right;

        int size;

        Node(E element,int priority){
            this.element = element;
            this.priority = priority;
        }
    }
}
