package ZombieGame;

// Source: https://www.codestudy.net/blog/fastest-gaussian-blur-implementation/
public class GaussianBlur {

    private static double[] gaussianKernel(int radius, double sigma) {
        int size = radius * 2 + 1;
        double[] kernel = new double[size];
        double sum = 0.0;

        for (int i = 0; i < size; i++) {
            int x = i - radius;
            double v = Math.exp(-(x * x) / (2 * sigma * sigma));
            kernel[i] = v;
            sum += v;
        }

        // normalize
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }
        return kernel;
    }

    private static double[][] convolveHorizontal(double[][] src, int width, int height, double[] kernel, int radius) {
        double[][] dst = new double[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sum = 0;
                for (int i = -radius; i <= radius; i++) {
                    int xi = Math.max(0, Math.min(x + i, width - 1));
                    sum += src[xi][y] * kernel[i + radius];
                }
                dst[x][y] = sum;
            }
        }
        return dst;
    }

    private static double[][] convolveVertical(double[][] src, int width, int height, double[] kernel, int radius) {
        double[][] dst = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double sum = 0;
                for (int i = -radius; i <= radius; i++) {
                    int yi = Math.max(0, Math.min(y + i, height - 1));
                    sum += src[x][yi] * kernel[i + radius];
                }
                dst[x][y] = sum;
            }
        }
        return dst;
    }

    /**
     * Applies a gaussian blur to a 2D array
     * 
     * @param src The input 2D double array (values 0..1)
     * @param radius How far neighbors influence the value
     * @param sigma Controls the amount of blurring
     * @return A new 2D array with blurred values
     */
    public static double[][] blur(double[][] src, int radius, float sigma) {
        int height = src.length;
        int width = src[0].length;
        double[] kernel = gaussianKernel(radius, sigma);
        double[][] temp = convolveHorizontal(src, width, height, kernel, radius);
        return convolveVertical(temp, width, height, kernel, radius);
    }
}
