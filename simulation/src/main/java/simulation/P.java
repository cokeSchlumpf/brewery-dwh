package simulation;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class P {

    private static final Random random = new Random();

    private P() {

    }

    public static double randomDouble(double avg, int relativeSD) {
        var sd = avg / relativeSD;
        return random.nextGaussian() * sd + avg;
    }

    public static <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    public static Duration randomDuration(Duration avg, Duration sd) {
        var seconds = random.nextGaussian() * sd.getSeconds() + avg.getSeconds();
        return Duration.ofSeconds((long) seconds);
    }

    public static Duration randomDuration(Duration avg) {
        return randomDuration(avg, avg.dividedBy(4));
    }

    public static Duration randomDurationMinutes(int minutes) {
        return randomDuration(Duration.ofMinutes(minutes));
    }

}
