package common;

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

    public static double randomDouble(double avg, double sd) {
        return random.nextGaussian() * sd + avg;
    }

    public static int randomInteger(int avg, int relativeSD){
        var sd = avg/relativeSD;
        return (int) random.nextGaussian()*sd+avg;
    }

    public static int randomInteger(int avg, double sd){
        return (int) Math.min(((int) random.nextGaussian()*sd+avg), 0);
    }

    public static <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    public static <T> List<T> nRandomItems(List<T> items, int n){
        Random rand = new Random();

        for (int i = 0; i < Math.min(n, items.size()); i++) {
            int randomIndex = rand.nextInt(items.size());
            T randomElement = items.get(randomIndex);
            items.remove(randomIndex);
        }
        return items;
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
