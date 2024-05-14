package info.itsthesky.disky.elements.changers;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.util.Utils;
import org.bukkit.event.Event;

import java.lang.reflect.Array;
import java.util.Arrays;

public interface IAsyncGettableExpression<T> {

    T[] getAsync(Event e);

    default T[] getArrayAsync(Event event, Expression<T> exprInstance) {
        T[] values = getAsync(event);
        if (values == null) {
            return (T[]) Array.newInstance(exprInstance.getReturnType(), 0);
        }
        if (values.length == 0)
            return values;

        int numNonNull = 0;
        for (T value : values)
            if (value != null)
                numNonNull++;

        if (!exprInstance.getAnd()) {
            if (values.length == 1 && values[0] != null)
                return Arrays.copyOf(values, 1);
            int rand = Utils.random(0, numNonNull);
            T[] valueArray = (T[]) Array.newInstance(values.getClass().getComponentType(), 1);
            for (T value : values) {
                if (value != null) {
                    if (rand == 0) {
                        valueArray[0] = value;
                        return valueArray;
                    }
                    rand--;
                }
            }
            assert false;
        }

        if (numNonNull == values.length)
            return Arrays.copyOf(values, values.length);
        T[] valueArray = (T[]) Array.newInstance(values.getClass().getComponentType(), numNonNull);
        int i = 0;
        for (T value : values)
            if (value != null)
                valueArray[i++] = value;
        return valueArray;
    }

}
