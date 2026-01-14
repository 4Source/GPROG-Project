package ZombieGame.Algorithms;

public class BoxBlur {
    /**
     * Applies a box blur to a 2D array.
     * 
     * @param map The input 2D double array (values 0..1)
     * @param radius How far neighbors influence the value
     * @return A new 2D array with blurred values
     */
    public static double[][] blur(double[][] src, int radius) {
        int height = src.length;
        int width = src[0].length;
        double[][] result = new double[height][width];

        // Horizontal pass
        for (int y = 0; y < height; y++) {
            double sum = 0;
            for (int x = -radius; x <= radius; x++) {
                int xi = clamp(x, 0, width - 1);
                sum += src[y][xi];
            }
            for (int x = 0; x < width; x++) {
                result[y][x] = sum / (2 * radius + 1);

                int removeIndex = clamp(x - radius, 0, width - 1);
                int addIndex = clamp(x + radius + 1, 0, width - 1);

                sum += src[y][addIndex] - src[y][removeIndex];
            }
        }

        // Vertical pass
        double[][] finalResult = new double[height][width];
        for (int x = 0; x < width; x++) {
            double sum = 0;
            for (int y = -radius; y <= radius; y++) {
                int yi = clamp(y, 0, height - 1);
                sum += result[yi][x];
            }
            for (int y = 0; y < height; y++) {
                finalResult[y][x] = sum / (2 * radius + 1);

                int removeIndex = clamp(y - radius, 0, height - 1);
                int addIndex = clamp(y + radius + 1, 0, height - 1);

                sum += result[addIndex][x] - result[removeIndex][x];
            }
        }

        return finalResult;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
