package ds.force;

/**
 * An collection of first in and last out
 *
 * @author comradeHsu
 */
public interface Stack<E> {

    /**
     * return the size of stack
     *
     * @return the number of elements in this stack
     */
    int size();

    /**
     * Returns <tt>true</tt> if this stack contains no elements.
     *
     * @return <tt>true</tt> if this stack contains no elements
     */
    boolean isEmpty();

    /**
     * Pushes an item onto the top of this stack.
     *
     * @param item the item to be pushed onto this stack.
     * @return the <code>item</code> argument.
     */
    E push(E item);

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     *
     * @return The object at the top of this stack
     */
    E pop();

    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     *
     * @return the object at the top of this stack
     */
    E peek();

    /**
     * Removes all of the elements from this stack (optional operation).
     * The stack will be empty after this call returns.
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> operation
     *                                       is not supported by this stack
     */
    void clear();
}
