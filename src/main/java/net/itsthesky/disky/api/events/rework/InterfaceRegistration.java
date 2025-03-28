package net.itsthesky.disky.api.events.rework;

/*
 * DiSky
 * Copyright (C) 2025 ItsTheSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

class InterfaceRegistration<T extends Event, I, R, P> {

    private final Class<I> interfaceClass;
    private final Class<R> returnTypeClass;
    private final @Nullable Class<P> parameterTypeClass;

    private final String methodName;

    private final BiFunction<P, T, R> function;

    InterfaceRegistration(Class<I> interfaceClass,
                          Class<R> returnTypeClass,
                          @Nullable Class<P> parameterTypeClass,
                          String methodName,
                          BiFunction<P, T, R> function) {
        this.interfaceClass = interfaceClass;
        this.returnTypeClass = returnTypeClass;
        this.parameterTypeClass = parameterTypeClass;
        this.methodName = methodName;
        this.function = function;
    }

    Class<I> getInterfaceClass() {
        return interfaceClass;
    }

    Class<R> getReturnTypeClass() {
        return returnTypeClass;
    }

    @Nullable Class<P> getParameterTypeClass() {
        return parameterTypeClass;
    }

    String getMethodName() {
        return methodName;
    }

    BiFunction<P, T, R> getFunction() {
        return function;
    }
}
