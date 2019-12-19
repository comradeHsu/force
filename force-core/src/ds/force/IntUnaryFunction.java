package ds.force;

@FunctionalInterface
public interface IntUnaryFunction<T> {

    /**
     * Applies this operator to the given operand.
     *
     * @param t the first function argument
     * @param operand the operand
     * @return the operator result
     */
    int applyAsInt(T t, int operand);
}
