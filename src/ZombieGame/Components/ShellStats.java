package ZombieGame.Components;

public class ShellStats {
    double speed;
    double range;
    int damage;

    public ShellStats(double speed, double range, int damage) {
        this.speed = speed;
        this.range = range;
        this.damage = damage;
    }

    public double getSpeed() {
        return speed;
    }

    public double getRange() {
        return range;
    }

    public int getDamage() {
        return damage;
    }
}
