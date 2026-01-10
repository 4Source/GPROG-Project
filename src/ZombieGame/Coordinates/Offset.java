package ZombieGame.Coordinates;

public record Offset(double x, double y) {

    public Offset() {
        this(0, 0);
    }

    /**
     * @return Returns a new pos where the summands are added to it
     */
    public Offset add(double summandX, double summandY) {
        return new Offset(this.x + summandX, this.y + summandY);
    }

    /**
     * @return Returns a new pos where the summand is added to it
     */
    public Offset add(Offset summand) {
        return new Offset(this.x + summand.x, this.y + summand.y);
    }

    /**
     * @return Returns a new pos where the subtrahends are subtracted from this pos
     */
    public Offset sub(double subtrahendX, double subtrahendY) {
        return new Offset(this.x - subtrahendX, this.y - subtrahendY);
    }

    /**
     * @return Returns a new pos where the subtrahend is subtracted from this pos
     */
    public Offset sub(Offset subtrahend) {
        return new Offset(this.x - subtrahend.x, this.y - subtrahend.y);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public Offset mul(double factor) {
        return new Offset(this.x * factor, this.y * factor);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public Offset mul(double factorX, double factorY) {
        return new Offset(this.x * factorX, this.y * factorY);
    }

    /**
     * @return Returns a new pos where the parts are multiplied with the factor
     */
    public Offset mul(Offset factor) {
        return new Offset(this.x * factor.x, this.y * factor.y);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public Offset div(double divisor) {
        return new Offset(this.x / divisor, this.y / divisor);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public Offset div(double divisorX, double divisorY) {
        return new Offset(this.x / divisorX, this.y / divisorY);
    }

    /**
     * @return Returns a new pos where the parts are divided by the divisor
     */
    public Offset div(Offset divisor) {
        return new Offset(this.x / divisor.x, this.y / divisor.y);
    }

    /**
     * @return a new Offset (-x, -y)
     */
    public Offset invert() {
        return new Offset(-this.x, -this.y);
    }

    /**
     * @return a new Offset (-x, y)
     */
    public Offset invertX() {
        return new Offset(-this.x, this.y);
    }

    /**
     * @return a new Offset (x, -y)
     */
    public Offset invertY() {
        return new Offset(this.x, -this.y);
    }

    /**
     * @param exponent The exponent (for exponents of 2 prefer {@link #pow2()})
     * @return Returns a new pos where each part is separately raised to the power of exponent
     */
    public Offset pow(double exponent) {
        return new Offset(Math.pow(this.x, exponent), Math.pow(this.y, exponent));
    }

    /**
     * @return Returns a new pos where each part is separately raised to the power of 2
     */
    public Offset pow2() {
        return new Offset(this.x * this.x, this.y * this.y);
    }

    /**
     * @param exponent The exponent (for exponents of 2 prefer {@link #xPow2()})
     * @return Returns the x part raised to the power of exponent
     */
    public double xPow(double exponent) {
        return Math.pow(this.x, exponent);
    }

    /**
     * @return Returns the x part raised to the power of 2
     */
    public double xPow2() {
        return this.x * this.x;
    }

    /**
     * @param exponent The exponent (for exponents of 2 prefer {@link #yPow2()})
     * @return Returns the y part raised to the power of exponent
     */
    public double yPow(double exponent) {
        return Math.pow(this.y, exponent);
    }

    /**
     * @return Returns the x part raised to the power of 2
     */
    public double yPow2() {
        return this.y * this.y;
    }

    /**
     * @return Returns the absolute value. If the pos is not negative, the pos is returned. If the pos is negative, the negation of the pos is returned.
     */
    public Offset abs() {
        return new Offset(Math.abs(this.x), Math.abs(this.y));
    }
}
