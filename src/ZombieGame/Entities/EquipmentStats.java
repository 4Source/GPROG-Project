package ZombieGame.Entities;

import ZombieGame.CharacterEquipment;


import java.util.HashMap;
import java.util.Map;

public class EquipmentStats {
    double fireRate;
    double damageOfShot;
    int amountOfBullets;
    double angleOfBullets;
    int ammunition;

    private static final Map<CharacterEquipment, EquipmentStats.WeaponStats> weaponStatsMap = new HashMap<>();

    static {
        weaponStatsMap.put(CharacterEquipment.BAT, new EquipmentStats.WeaponStats(0.5, 5,0,0, 0));       // Nahkampf, keine Munition
        weaponStatsMap.put(CharacterEquipment.GUN, new EquipmentStats.WeaponStats(0.2, 3,1,0,30));
        weaponStatsMap.put(CharacterEquipment.PISTOL, new EquipmentStats.WeaponStats(0.4, 4,1,0,15));
        weaponStatsMap.put(CharacterEquipment.SHOTGUN, new EquipmentStats.WeaponStats(1.0,3,5,5, 8));
    }

    private static class WeaponStats {
        double fireRate;
        double damageOfShot;
        int bulletsOfShot;
        double angleOfBullets;
        int ammunition;

        WeaponStats(double fireRate, double damageOfShot, int bulletsOfShot, double angleOfBullets, int ammunition) {
            this.fireRate = fireRate;
            this.damageOfShot = damageOfShot;
            this.bulletsOfShot = bulletsOfShot;
            this.angleOfBullets = angleOfBullets;
            this.ammunition = ammunition;
        }
    }

    public EquipmentStats(CharacterEquipment equipment) {
        WeaponStats weaponStats = weaponStatsMap.get(equipment);
        this.fireRate = weaponStats.fireRate;
        this.damageOfShot = weaponStats.damageOfShot;
        this.amountOfBullets = weaponStats.bulletsOfShot;
        this.angleOfBullets = weaponStats.angleOfBullets;
        this.ammunition = weaponStats.ammunition;
    }

    public double getFireRate() {
        return fireRate;
    }

    public double getDamageOfShot() {
        return damageOfShot;
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
