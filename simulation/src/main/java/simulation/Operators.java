package simulation;

import akka.japi.function.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Operators {

    private Operators() {

    }

    public static <T1, T2, R> CompletionStage<R> compose(
        CompletionStage<T1> cs1, CompletionStage<T2> cs2, Function2<T1, T2, R> combineWith) {
        CompletableFuture<T1> f1 = cs1.toCompletableFuture();
        CompletableFuture<T2> f2 = cs2.toCompletableFuture();

        return CompletableFuture
            .allOf(f1, f2)
            .thenApply(v -> Operators.suppressExceptions(() -> combineWith.apply(f1.join(), f2.join())));
    }

    public static <T1, T2, T3, R> CompletionStage<R> compose(
        CompletionStage<T1> cs1, CompletionStage<T2> cs2, CompletionStage<T3> cs3,
        Function3<T1, T2, T3, R> combineWith) {
        CompletableFuture<T1> f1 = cs1.toCompletableFuture();
        CompletableFuture<T2> f2 = cs2.toCompletableFuture();
        CompletableFuture<T3> f3 = cs3.toCompletableFuture();

        return CompletableFuture
            .allOf(f1, f2, f3)
            .thenApply(v -> Operators.suppressExceptions(() -> combineWith.apply(f1.join(), f2.join(), f3.join())));
    }

    public static <T1, T2, T3, T4, R> CompletionStage<R> compose(
        CompletionStage<T1> cs1, CompletionStage<T2> cs2, CompletionStage<T3> cs3, CompletionStage<T4> cs4,
        Function4<T1, T2, T3, T4, R> combineWith) {
        CompletableFuture<T1> f1 = cs1.toCompletableFuture();
        CompletableFuture<T2> f2 = cs2.toCompletableFuture();
        CompletableFuture<T3> f3 = cs3.toCompletableFuture();
        CompletableFuture<T4> f4 = cs4.toCompletableFuture();

        return CompletableFuture
            .allOf(f1, f2, f3, f4)
            .thenApply(v -> Operators.suppressExceptions(() -> combineWith.apply(f1.join(), f2.join(), f3.join(),
                f4.join())));
    }

    public static <T1, T2, T3, T4, T5, R> CompletionStage<R> compose(
        CompletionStage<T1> cs1, CompletionStage<T2> cs2, CompletionStage<T3> cs3, CompletionStage<T4> cs4,
        CompletionStage<T5> cs5,
        Function5<T1, T2, T3, T4, T5, R> combineWith) {
        CompletableFuture<T1> f1 = cs1.toCompletableFuture();
        CompletableFuture<T2> f2 = cs2.toCompletableFuture();
        CompletableFuture<T3> f3 = cs3.toCompletableFuture();
        CompletableFuture<T4> f4 = cs4.toCompletableFuture();
        CompletableFuture<T5> f5 = cs5.toCompletableFuture();

        return CompletableFuture
            .allOf(f1, f2, f3, f4, f5)
            .thenApply(v -> Operators.suppressExceptions(() -> combineWith.apply(f1.join(), f2.join(), f3.join(),
                f4.join(), f5
                    .join())));
    }

    public static <T1, T2, T3, T4, T5, T6, R> CompletionStage<R> compose(
        CompletionStage<T1> cs1, CompletionStage<T2> cs2, CompletionStage<T3> cs3, CompletionStage<T4> cs4,
        CompletionStage<T5> cs5,
        CompletionStage<T6> cs6,
        Function6<T1, T2, T3, T4, T5, T6, R> combineWith) {
        CompletableFuture<T1> f1 = cs1.toCompletableFuture();
        CompletableFuture<T2> f2 = cs2.toCompletableFuture();
        CompletableFuture<T3> f3 = cs3.toCompletableFuture();
        CompletableFuture<T4> f4 = cs4.toCompletableFuture();
        CompletableFuture<T5> f5 = cs5.toCompletableFuture();
        CompletableFuture<T6> f6 = cs6.toCompletableFuture();

        return CompletableFuture
            .allOf(f1, f2, f3, f4, f5, f6)
            .thenApply(v -> Operators.suppressExceptions(() -> combineWith.apply(
                f1.join(), f2.join(), f3.join(), f4.join(), f5.join(), f6.join())));
    }

    public static <T1, T2, T3, T4, T5, T6, T7, R> CompletionStage<R> compose(
        CompletionStage<T1> cs1, CompletionStage<T2> cs2, CompletionStage<T3> cs3, CompletionStage<T4> cs4,
        CompletionStage<T5> cs5,
        CompletionStage<T6> cs6, CompletionStage<T7> cs7,
        Function7<T1, T2, T3, T4, T5, T6, T7, R> combineWith) {
        CompletableFuture<T1> f1 = cs1.toCompletableFuture();
        CompletableFuture<T2> f2 = cs2.toCompletableFuture();
        CompletableFuture<T3> f3 = cs3.toCompletableFuture();
        CompletableFuture<T4> f4 = cs4.toCompletableFuture();
        CompletableFuture<T5> f5 = cs5.toCompletableFuture();
        CompletableFuture<T6> f6 = cs6.toCompletableFuture();
        CompletableFuture<T7> f7 = cs7.toCompletableFuture();

        return CompletableFuture
            .allOf(f1, f2, f3, f4, f5, f6)
            .thenApply(v -> Operators.suppressExceptions(() -> combineWith.apply(
                f1.join(), f2.join(), f3.join(), f4.join(), f5.join(), f6.join(), f7.join())));
    }

    public static <T> CompletionStage<List<T>> allOf(Stream<CompletionStage<T>> futures) {
        return allOf(futures.collect(Collectors.toList()));
    }

    public static <T> CompletionStage<List<T>> allOf(List<CompletionStage<T>> futures) {
        if (futures.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        AtomicReference<List<T>> results = new AtomicReference<>(new ArrayList<>());
        CompletableFuture<List<T>> result = new CompletableFuture<>();

        futures.forEach(f -> {
            f.thenAccept(r -> results.getAndUpdate(currentResults -> {
                currentResults.add(r);

                if (currentResults.size() == futures.size()) {
                    result.complete(currentResults);
                }

                return currentResults;
            }));

            f.exceptionally(ex -> {
                result.completeExceptionally(ex);
                return null;
            });
        });

        return result;
    }

    public static <T> CompletionStage<Optional<T>> optCS(Optional<CompletionStage<T>> opt) {
        return opt
            .map(cs -> cs.thenApply(Optional::of))
            .orElseGet(() -> CompletableFuture.completedFuture(Optional.empty()));
    }

    public static <T> CompletionStage<Optional<T>> flatOptCS(Optional<CompletionStage<Optional<T>>> opt) {
        if (opt.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        } else {
            return opt.get();
        }
    }

    public static <T, E extends Exception> CompletionStage<T> completeExceptionally(E with) {
        CompletableFuture<T> result = new CompletableFuture<>();
        result.completeExceptionally(with);
        return result;
    }

    public static <T> CompletionStage<T> completeExceptionally() {
        CompletableFuture<T> result = new CompletableFuture<>();
        result.completeExceptionally(new RuntimeException());
        return result;
    }

    public static <T> Optional<T> exceptionToNone(ExceptionalSupplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> List<T> filterOptional(List<Optional<T>> optionals) {
        return optionals
            .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    public static boolean isCause(Class<? extends Throwable> expected, Throwable exc) {
        return expected.isInstance(exc) || (
            exc != null && isCause(expected, exc.getCause())
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> hasCause(Throwable t, Class<T> exType) {
        if (exType.isInstance(t)) {
            return Optional.of((T) t);
        } else if (t.getCause() != null) {
            return hasCause(t.getCause(), exType);
        } else {
            return Optional.empty();
        }
    }

    public static void ignoreExceptions(ExceptionalRunnable runnable, Logger log) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (log != null) {
                log.warn("An exception occurred but will be ignored", e);
            }
        }
    }

    public static void ignoreExceptions(ExceptionalRunnable runnable) {
        ignoreExceptions(runnable, null);
    }

    public static <T> T ignoreExceptionsWithDefault(ExceptionalSupplier<T> supplier, T defaultValue, Logger log) {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (log != null) {
                log.warn("An exception occurred but will be ignored", e);
            }

            return defaultValue;
        }
    }

    public static <T> T ignoreExceptionsWithDefault(ExceptionalSupplier<T> supplier, T defaultValue) {
        return ignoreExceptionsWithDefault(supplier, defaultValue, null);
    }

    public static void require(boolean condition, String message, Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void require(boolean condition) {
        require(condition, "pre-conditions not met");
    }

    public static void suppressExceptions(ExceptionalRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            ExceptionUtils.wrapAndThrow(e);
        }
    }

    public static void suppressExceptions(ExceptionalRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(message, e);
        }
    }

    public static <T> T suppressExceptions(ExceptionalSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return ExceptionUtils.wrapAndThrow(e);
        }
    }

    public static <T> T suppressExceptions(ExceptionalSupplier<T> supplier, String message) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(message, e);
        }
    }

    @FunctionalInterface
    public interface ExceptionalRunnable {

        void run() throws Exception;

    }

    @FunctionalInterface
    public interface ExceptionalConsumer<T> {

        void accept(T param) throws Exception;

    }

    @FunctionalInterface
    public interface ExceptionalSupplier<T> {

        T get() throws Exception;

    }

    @FunctionalInterface
    public interface ExceptionalFunction<I, R> {

        R apply(I in) throws Exception;

    }

}
