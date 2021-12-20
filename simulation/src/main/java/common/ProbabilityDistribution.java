package common;

import akka.japi.Pair;
import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProbabilityDistribution<T> {

    /**
     * The set of first values of each pair is the random distribution (must sum up to 1), the 2nd
     * parameter are the possible values.
     */
    List<Pair<Double, T>> variants;

    Random random;

    public static <T> ProbabilityDistribution<T> apply(List<Pair<Double, T>> variants) {
        assert DoubleMath.fuzzyEquals(variants.stream().map(Pair::first).reduce(Double::sum).orElse(0.0), 1.0, 0.001);
        return new ProbabilityDistribution<>(variants, new Random());
    }

    @SafeVarargs
    public static <T> ProbabilityDistribution<T> apply(Pair<Double, T> ...variants) {
        return apply(Arrays.stream(variants).collect(Collectors.toList()));
    }

    public static <T> ProbabilityDistribution<T> singleValue(T singleValue) {
        return apply(Pair.apply(1.0d, singleValue));
    }

    public static ProbabilityDistribution<String> singleValueWithPotentialTypos(String value) {
        List<String> variants = Lists.newArrayList();

        if (value.contains("ah")) {
            variants.add(value.replaceFirst("ah", "a"));
        }

        if (value.contains("äu")) {
            variants.add(value.replaceFirst("äu", "eu"));
        }

        if (value.contains("eu")) {
            variants.add(value.replaceFirst("eu", "äu"));
        }

        if (value.contains("ch")) {
            variants.add(value.replaceFirst("ch", "sch"));
        }

        if (value.contains("sch")) {
            variants.add(value.replaceFirst("sch", "ch"));
        }

        if (value.contains("ou")) {
            variants.add(value.replaceFirst("ou", "o"));
            variants.add(value.replaceFirst("ou", "u"));
        }

        if (value.contains("ie")) {
            variants.add(value.replaceFirst("ie", "i"));
        }

        if (value.contains("ss")) {
            variants.add(value.replaceFirst("ss", "s"));
        }

        if (value.contains("ß")) {
            variants.add(value.replaceFirst("ß", "s"));
        }

        if (!value.toLowerCase().equals(value)) {
            variants.add(value.toLowerCase());
        }

        if (variants.size() > 0) {
            var prob = 0.2 / variants.size() - 1;
            var result = Lists.<Pair<Double, String>>newArrayList();

            result.add(Pair.apply(0.8, value));
            variants.forEach(v -> result.add(Pair.apply(prob, v)));

            return apply(result);
        } else {
            return singleValue(value);
        }
    }

    /**
     * Returns a random string based on probability for each variant.
     *
     * @return A randomly selected variant.
     */
    public T getValue() {
        T result = null;
        var v = random.nextDouble();
        var sum = 0d;
        var i = 0;

        while (result == null && i < variants.size()) {
            sum = sum + variants.get(i).first();

            if (v < sum) {
                result = variants.get(i).second();
            }

            i++;
        }

        return result;
    }

}
