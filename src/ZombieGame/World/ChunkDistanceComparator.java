package ZombieGame.World;

import java.util.Comparator;

import ZombieGame.Viewport;
import ZombieGame.Coordinates.ChunkIndex;
import ZombieGame.Coordinates.WorldPos;

public class ChunkDistanceComparator implements Comparator<ChunkIndex> {
    private final World world;

    public ChunkDistanceComparator(World world) {
        this.world = world;
    }

    @Override
    public int compare(ChunkIndex a, ChunkIndex b) {
        WorldPos center = Viewport.getCenter().toWorldPos(world);

        // Compute center position of chunks
        double halfChunk = Chunk.getChunkSize() / 2.0;
        WorldPos wa = a.toWorldPos().add(halfChunk, halfChunk);
        WorldPos wb = b.toWorldPos().add(halfChunk, halfChunk);

        double aspectRatio = ((double) Viewport.getScreenWidth()) / ((double) Viewport.getScreenHeight());

        // Compute weighted distance to the center
        double da = (Math.abs(wa.x() - center.x()) / aspectRatio) + Math.abs(wa.y() - center.y());
        double db = (Math.abs(wb.x() - center.x()) / aspectRatio) + Math.abs(wb.y() - center.y());

        return Double.compare(da, db);
    }

}
