package net.itsthesky.disky.api.datastruct;

import net.itsthesky.disky.api.datastruct.base.DataStruct;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ReflectBasicDataStructure extends BaseDataStructElement<Object, DataStruct<Object>> {

    @Override
    public Class<DataStruct<Object>> getDataStructClass() {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }

    @Override
    public Class<?> getReturnType() {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }

}
