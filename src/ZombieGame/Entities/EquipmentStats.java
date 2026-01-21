package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;
import ZombieGame.Components.ShellStats;


import java.util.HashMap;
import java.util.Map;

public class EquipmentStats {
    double fireRate;
    int amountOfBullets;
    double angleOfBullets;
    int ammunition;
    ShellStats shellStats;

    private static final Map<CharacterEquipment, EquipmentStats.WeaponStats> weaponStatsMap = new HashMap<>();

    static {
        weaponStatsMap.put(CharacterEquipment.BAT, new EquipmentStats.WeaponStats(0.5, 0,0, 0, new ShellStats(0,0,5)));
        weaponStatsMap.put(CharacterEquipment.GUN, new EquipmentStats.WeaponStats(0.2, 1,0,30, new ShellStats(600,600,3)));
        weaponStatsMap.put(CharacterEquipment.PISTOL, new EquipmentStats.WeaponStats(0.4, 1,0,15, new ShellStats(600,500,4)));
        weaponStatsMap.put(CharacterEquipment.SHOTGUN, new EquipmentStats.WeaponStats(1.0,5,5, 8, new ShellStats(400,400,3)));
    }

    private static class WeaponStats {
        double fireRate;
        int bulletsOfShot;
        double angleOfBullets;
        int ammunition;
        ShellStats shellStats;

        WeaponStats(double fireRate, int bulletsOfShot, double angleOfBullets, int ammunition, ShellStats shellStats) {
            this.fireRate = fireRate;
            this.bulletsOfShot = bulletsOfShot;
            this.angleOfBullets = angleOfBullets;
            this.ammunition = ammunition;
            this.shellStats = shellStats;
        }
    }

    public EquipmentStats(CharacterEquipment equipment) {
        WeaponStats weaponStats = weaponStatsMap.get(equipment);
        this.fireRate = weaponStats.fireRate;
        this.amountOfBullets = weaponStats.bulletsOfShot;
        this.angleOfBullets = weaponStats.angleOfBullets;
        this.ammunition = weaponStats.ammunition;
        shellStats = weaponStats.shellStats;
    }

    public double getFireRate() {
        return fireRate;
    }

    public ShellStats getShellStats() {
        return shellStats;
    }

    public int getAmountOfBullets() {
        return amountOfBullets;
    }

    public double getAngleOfBullets() {
        return angleOfBullets;
    }

    public int getAmmunition() {
        return ammunition;
    }

    public void addAmmunition(int ammunition) {
        this.ammunition += ammunition;
    }

    public void shoot() {
        this.ammunition--;
    }
}
