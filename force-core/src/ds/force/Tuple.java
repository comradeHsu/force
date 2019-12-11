package ds.force;

public class Tuple<T,R> {

    private T necessary;

    private R optional;

    public T getNecessary() {
        return necessary;
    }

    public Tuple<T,R> setNecessary(T necessary) {
        this.necessary = necessary;
        return this;
    }

    public R getOptional() {
        return optional;
    }

    public Tuple<T,R> setOptional(R optional) {
        this.optional = optional;
        return this;
    }
}
