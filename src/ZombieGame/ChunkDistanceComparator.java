package ZombieGame;

import java.util.Comparator;

import ZombieGame.Coordinates.ChunkIndex;

public class ChunkDistanceComparator implements Comparator<ChunkIndex> {
    private final World world;

    public ChunkDistanceComparator(World world) {
        this.world = world;
    }

    @Override
    public int compare(ChunkIndex a, ChunkIndex b) {
        double da = PhysicsSystem.distance(a.toWorldPos(), Viewport.getCenter().toWorldPos(world));
        double db = PhysicsSystem.distance(b.toWorldPos(), Viewport.getCenter().toWorldPos(world));
        return Double.compare(da, db);
    }

}
