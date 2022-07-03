package eu.bebendorf.templ8.macro.control;

import lombok.Getter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ForEachLoopState {

    @Getter
    ForEachLoopState parent;
    List<Object> values;
    @Getter
    int index = -1;

    public ForEachLoopState(ForEachLoopState parent, Object o) {
        this.parent = parent;
        if(o == null)
            throw new IllegalArgumentException("invalid iterable");
        if(o.getClass().isArray()) {
            int len = Array.getLength(o);
            values = new ArrayList<>(len);
            for(int i=0; i<len; i++)
                values.add(Array.get(o, i));
        } else if(o instanceof Iterable) {
            values = new ArrayList<>();
            for(Object e : (Iterable<Object>) o)
                values.add(e);
        } else {
            throw new IllegalArgumentException("invalid iterable");
        }
    }

    public boolean hasNext() {
        return index + 1 < values.size();
    }

    public Object next() {
        if(!hasNext())
            return null;
        index++;
        return values.get(index);
    }

    public int getCount() {
        return values.size();
    }

}
