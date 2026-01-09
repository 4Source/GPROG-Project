package ZombieGame.Coordinates;

public record Offset(double x, double y) {
    public Offset() {
        this(0, 0);
    }

    public Offset add(int x, int y) {
        return new Offset(this.x + x, this.y + y);
    }

    public Offset add(Offset offset) {
        return new Offset((int) (x + offset.x()), (int) (y + offset.y()));
    }

    public Offset sub(int x, int y) {
        return new Offset(this.x - x, this.y - y);
    }

    public Offset sub(Offset offset) {
        return new Offset((int) (x - offset.x()), (int) (y - offset.y()));
    }
}
