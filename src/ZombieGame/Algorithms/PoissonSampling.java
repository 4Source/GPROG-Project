package ZombieGame.Algorithms;

import java.util.concurrent.ThreadLocalRandom;

public class PoissonSampling {
    public static int sample(double lambda) {
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;

        do {
            k++;
            p *= ThreadLocalRandom.current().nextDouble();
        } while (p > L);

        return k - 1;
    }
}
