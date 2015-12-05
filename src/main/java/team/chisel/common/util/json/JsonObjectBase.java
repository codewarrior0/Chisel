package team.chisel.common.util.json;


public abstract class JsonObjectBase<T, F> {

    private T cache;

    public final T get(F factory) {
        return cache != null ? cache : (cache = create(factory));
    }

    public abstract T create(F factory);
}
