package net.itsthesky.disky.api.events.rework;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.function.Function;

/**
 * A class that holds registration information for a rest event value.
 *
 * @param <T> The JDA event type
 * @param <A> The RestAction type
 * @param <R> The result type of the RestAction
 */
class RestValueRegistration<T extends Event, A, R> {
    private final String codeName;
    private final Function<T, RestAction<A>> actionMapper;
    private final Function<A, R> resultMapper;

    /**
     * Creates a new rest value registration.
     *
     * @param codeName The code name used in 'event-codename' expressions
     * @param actionMapper A function to extract the RestAction from the JDA event
     * @param resultMapper A function to map the result of the RestAction
     */
    RestValueRegistration(String codeName, Function<T, RestAction<A>> actionMapper, Function<A, R> resultMapper) {
        this.codeName = codeName;
        this.actionMapper = actionMapper;
        this.resultMapper = resultMapper;
    }

    /**
     * Creates a new rest value registration with an identity result mapper.
     *
     * @param codeName The code name used in 'event-codename' expressions
     * @param actionMapper A function to extract the RestAction from the JDA event
     */
    RestValueRegistration(String codeName, Function<T, RestAction<A>> actionMapper) {
        this(codeName, actionMapper, a -> (R) a);
    }

    /**
     * Gets the code name.
     *
     * @return The code name
     */
    String getCodeName() {
        return codeName;
    }

    /**
     * Gets the action mapper function.
     *
     * @return The action mapper function
     */
    Function<T, RestAction<A>> getActionMapper() {
        return actionMapper;
    }

    /**
     * Gets the result mapper function.
     *
     * @return The result mapper function
     */
    Function<A, R> getResultMapper() {
        return resultMapper;
    }
}