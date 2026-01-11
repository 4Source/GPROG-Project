package ZombieGame.Coordinates;

public record Rotation(double radians) {
    public Rotation() {
        this(0.0);
    }

    public static Rotation fromRadians(double radians) {
        return new Rotation(radians);
    }

    public static Rotation fromDegrees(double degrees) {
        return new Rotation(Math.toRadians(degrees));
    }

    public static final Rotation ZERO = new Rotation(0);
    public static final Rotation PI = new Rotation(Math.PI);
    public static final Rotation TWO_PI = new Rotation(Math.PI * 2);
    public static final Rotation HALF_PI = new Rotation(Math.PI / 2);

    public double degrees() {
        return Math.toDegrees(radians);
    }

    public Rotation add(Rotation rotation) {
        return new Rotation(this.radians + rotation.radians);
    }

    public Rotation sub(Rotation rotation) {
        return new Rotation(this.radians - rotation.radians);
    }

    public Rotation mul(double factor) {
        return new Rotation(this.radians * factor);
    }

    public Rotation div(double divisor) {
        return new Rotation(this.radians / divisor);
    }

    /**
     * Counter-clockwise rotation
     */
    public Rotation rotateCCW(Rotation rotation) {
        return new Rotation(this.radians + rotation.radians);
    }

    /**
     * Clockwise rotation
     */
    public Rotation rotateCW(Rotation rotation) {
        return new Rotation(this.radians - rotation.radians);
    }

    /**
     * Counter-clockwise rotation
     */
    public Rotation rotateCCW(double radians) {
        return new Rotation(this.radians + radians);
    }

    /**
     * Clockwise rotation
     */
    public Rotation rotateCW(double radians) {
        return new Rotation(this.radians - radians);
    }

    /** Normalizes to [0, 2π) */
    public Rotation normalizeUnsigned() {
        double twoPi = Math.PI * 2;
        double r = radians % twoPi;
        if (r < 0)
            r += twoPi;
        return new Rotation(r);
    }

    /** Normalizes to (-π, π] */
    public Rotation normalizeSigned() {
        double twoPi = Math.PI * 2;
        double r = (radians + Math.PI) % twoPi;
        if (r < 0)
            r += twoPi;
        return new Rotation(r - Math.PI);
    }

    private static final double EPSILON = 1e-9;

    public boolean isZero() {
        return normalizeUnsigned().radians < EPSILON;
    }

    public double sin() {
        return Math.sin(radians);
    }

    public double cos() {
        return Math.cos(radians);
    }

    public double tan() {
        return Math.tan(radians);
    }

    @Override
    public String toString() {
        return String.format("%.2f°", degrees());
    }
}
