package net.itsthesky.diskytest.fake.action;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestAction;
import net.itsthesky.diskytest.fake.FakeEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * In-process {@link RestAction} that runs its work supplier synchronously on the
 * calling thread.
 *
 * <p>This is the foundation of the loopback: every fake "send" returns a
 * {@code FakeRestAction} whose supplier mutates fake state and dispatches the
 * corresponding inbound JDA event in-process. {@link RestAction#queue},
 * {@link RestAction#complete} and {@link RestAction#submit(boolean)} all resolve
 * immediately.
 *
 * <p>Because {@link RestAction} has dozens of abstract methods that are not part
 * of the loopback flow, this class extends {@link FakeEntity} and exposes a typed
 * proxy via {@link #typed()}. Methods declared here are routed by the proxy;
 * everything else throws {@link UnsupportedOperationException}.
 */
public class FakeRestAction<T> extends FakeEntity<RestAction> {

    private final JDA jda;
    private final Callable<T> action;
    private @Nullable BooleanSupplier check;

    public FakeRestAction(JDA jda, Callable<T> action) {
        super(RestAction.class);
        this.jda = jda;
        this.action = action;
    }

    /**
     * Constructor used by subclasses that need the proxy to implement additional JDA
     * interfaces (e.g. {@code MessageCreateAction}) beyond the base {@code RestAction}.
     */
    public FakeRestAction(JDA jda, Callable<T> action, Class<?>... additionalInterfaces) {
        super(RestAction.class, additionalInterfaces);
        this.jda = jda;
        this.action = action;
    }

    /** Returns the proxy cast to the proper generic type. */
    @SuppressWarnings("unchecked")
    public RestAction<T> typed() {
        return (RestAction<T>) asProxy();
    }

    private T runChecked() throws Exception {
        if (check != null && !check.getAsBoolean())
            throw new CancellationException("FakeRestAction check returned false");
        return action.call();
    }

    // ===== JDA RestAction surface implemented by the proxy =====

    @NotNull
    public JDA getJDA() {
        return jda;
    }

    @NotNull
    public RestAction<T> setCheck(@Nullable BooleanSupplier checks) {
        this.check = checks;
        return typed();
    }

    @Nullable
    public BooleanSupplier getCheck() {
        return check;
    }

    @NotNull
    public RestAction<T> addCheck(@NotNull BooleanSupplier checks) {
        BooleanSupplier prev = this.check;
        this.check = prev == null ? checks : () -> prev.getAsBoolean() && checks.getAsBoolean();
        return typed();
    }

    @NotNull
    public RestAction<T> deadline(long timestamp) {
        return typed();
    }

    public void queue() {
        queue(null, null);
    }

    public void queue(@Nullable Consumer<? super T> success) {
        queue(success, null);
    }

    public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
        T result;
        try {
            result = runChecked();
        } catch (Throwable t) {
            if (failure != null) failure.accept(t);
            else if (RestAction.getDefaultFailure() != null) RestAction.getDefaultFailure().accept(t);
            return;
        }
        if (success != null) success.accept(result);
        else if (RestAction.getDefaultSuccess() != null) RestAction.getDefaultSuccess().accept(result);
    }

    public T complete() throws RateLimitedException {
        return complete(true);
    }

    public T complete(boolean shouldQueue) throws RateLimitedException {
        try {
            return runChecked();
        } catch (RateLimitedException re) {
            throw re;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public CompletableFuture<T> submit() {
        return submit(true);
    }

    @NotNull
    public CompletableFuture<T> submit(boolean shouldQueue) {
        try {
            return CompletableFuture.completedFuture(runChecked());
        } catch (Throwable t) {
            CompletableFuture<T> failed = new CompletableFuture<>();
            failed.completeExceptionally(t);
            return failed;
        }
    }

    @NotNull
    public <O> RestAction<O> map(@NotNull Function<? super T, ? extends O> map) {
        return new FakeRestAction<O>(jda, () -> map.apply(runChecked())).typed();
    }

    @NotNull
    public RestAction<T> onErrorMap(@Nullable Predicate<? super Throwable> condition,
                                    @NotNull Function<? super Throwable, ? extends T> map) {
        return new FakeRestAction<>(jda, () -> {
            try { return runChecked(); }
            catch (Throwable t) {
                if (condition == null || condition.test(t)) return map.apply(t);
                throw t instanceof Exception ? (Exception) t : new RuntimeException(t);
            }
        }).<T>typedRaw();
    }

    @SuppressWarnings("unchecked")
    private <X> RestAction<X> typedRaw() { return (RestAction<X>) asProxy(); }

    @NotNull
    public RestAction<T> onErrorFlatMap(@Nullable Predicate<? super Throwable> condition,
                                        @NotNull Function<? super Throwable, ? extends RestAction<? extends T>> map) {
        return new FakeRestAction<T>(jda, () -> {
            try { return runChecked(); }
            catch (Throwable t) {
                if (condition == null || condition.test(t)) return map.apply(t).complete();
                throw t instanceof Exception ? (Exception) t : new RuntimeException(t);
            }
        }).typed();
    }

    @NotNull
    public <O> RestAction<O> flatMap(@Nullable Predicate<? super T> condition,
                                     @NotNull Function<? super T, ? extends RestAction<O>> flatMap) {
        return new FakeRestAction<O>(jda, () -> {
            T value = runChecked();
            if (condition != null && !condition.test(value)) return null;
            return flatMap.apply(value).complete();
        }).typed();
    }

    @NotNull
    public RestAction<T> delay(long delay, @NotNull TimeUnit unit, @Nullable ScheduledExecutorService scheduler) {
        // In test mode we ignore delays — they only slow tests.
        return typed();
    }

    @NotNull
    public RestAction<T> timeout(long timeout, @NotNull TimeUnit unit) {
        return typed();
    }
}
