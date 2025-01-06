package info.itsthesky.disky.api.datastruct.base;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DataStruct<T> {

    default @Nullable String preValidate(List<String> presentKeys) {
        return null;
    }

}
