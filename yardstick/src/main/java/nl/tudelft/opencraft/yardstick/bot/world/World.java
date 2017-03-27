package nl.tudelft.opencraft.yardstick.bot.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import nl.tudelft.opencraft.yardstick.bot.entity.Entity;
import nl.tudelft.opencraft.yardstick.util.Vector3i;

public class World {

    private final Dimension dimension;
    private final WorldType type;
    private final WorldPhysics physics;
    //
    private final Map<ChunkLocation, Chunk> chunks = new HashMap<>();
    private final Map<Integer, Entity> entities = new HashMap<>();
    private Position spawnPoint;

    public World(Dimension dimension, WorldType type) {
        this.dimension = dimension;
        this.type = type;
        this.physics = new SimpleWorldPhysics(this);
    }

    public Dimension getDimension() {
        return dimension;
    }

    public WorldType getType() {
        return type;
    }

    public WorldPhysics getPhysics() {
        return physics;
    }

    public Collection<Chunk> getLoadedChunks() {
        return chunks.values();
    }

    public void loadChunk(Chunk chunk) {
        chunks.put(chunk.getLocation(), chunk);
    }

    public void unloadChunk(Chunk chunk) {
        ChunkLocation location = chunk.getLocation();
        unloadChunk(location.getX(), location.getZ());
    }

    public void unloadChunk(int x, int z) {
        chunks.remove(new ChunkLocation(x, z));
    }

    public ChunkLocation getChunkLocation(int x, int z) {
        int chunkX = Math.floorDiv(x, 16);
        int chunkZ = Math.floorDiv(z, 16);

        return new ChunkLocation(chunkX, chunkZ);
    }

    public Chunk getChunk(ChunkLocation location) throws ChunkNotLoadedException {
        if (chunks.containsKey(location)) {
            return chunks.get(location);
        } else {
            throw new ChunkNotLoadedException(location);
        }
    }

    public Block getBlockAt(Vector3i v) throws ChunkNotLoadedException {
        return getBlockAt(v.getX(), v.getY(), v.getZ());
    }

    public Block getBlockAt(int x, int y, int z) throws ChunkNotLoadedException {
        Chunk chunk = getChunk(getChunkLocation(x, z));
        return new Block(x, y, z, chunk);
    }

    public Block getHighestBlockAt(int x, int z) throws ChunkNotLoadedException {
        for (int y = 200; y > 0; y--) { // TODO: Fix world height
            if (physics.canStand(new Vector3i(x, y, z))) {
                return getBlockAt(x, y, z);
            }
        }
        return getBlockAt(x, 0, z);
    }

    public Collection<Entity> getVisibleEntities() {
        return entities.values();
    }

    public boolean isEntityLoaded(int id) {
        return entities.containsKey(id);
    }

    public void loadEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public void unloadEntity(Entity entity) {
        unloadEntity(entity.getId());
    }

    public void unloadEntity(int id) {
        entities.remove(id);
    }

    public Entity getEntity(int id) {
        return entities.get(id);
    }

    public Position getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Position spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
}
