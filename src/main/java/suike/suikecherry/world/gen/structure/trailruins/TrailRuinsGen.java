package suike.suikecherry.world.gen.structure.trailruins;

import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.google.common.collect.Lists;

public class TrailRuinsGen implements IWorldGenerator {

    public static List<Biome> biomes = Lists.newArrayList(
        Biome.getBiome(21),  // 丛林
        Biome.getBiome(5),   // 针叶林
        Biome.getBiome(30),  // 积雪针叶林
        Biome.getBiome(32),  // 原始松木针叶林
        Biome.getBiome(160)  // 原始云杉针叶林
    );

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            // 获取当前区块的生物群系
            BlockPos pos = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);

            // 仅生成在 biomes 列表的生物群系
            if (biomes.contains(world.getBiome(pos)) && canGenerateHere(world, chunkX, chunkZ, random)) {
                // 创建结构起始点
                TrailRuinsStart start = new TrailRuinsStart(world, random, chunkX, chunkZ);

                // 获取边界框
                StructureBoundingBox sbb = start.getBoundingBox();

                // 生成结构
                start.generateStructure(world, random, sbb);

                // 保存结构数据
                TrailRuinsData.getData((WorldServer) world).addTrailRuins(start);
            }
        }
    }

    private boolean canGenerateHere(World world, int chunkX, int chunkZ, Random random) {
        // 检查生成概率
        if (random.nextInt(8000) >= 4) return false;

        // 检查是否已经生成过
        TrailRuinsData data = TrailRuinsData.getData((WorldServer) world);
        if (data.isTrailRuinsNearby(chunkX, chunkZ, 60)) {
            return false;
        }

        return true;
    }

// 古迹废墟起始点
    public static class TrailRuinsStart extends StructureStart {
        private BlockPos center;

        public TrailRuinsStart() {}

        public TrailRuinsStart(World world, Random rand, int chunkX, int chunkZ) {
            super(chunkX, chunkZ);
            this.center = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);

            // 创建神殿组件
            TrailRuins room = new TrailRuins(world, rand, center.getX(), center.getZ());
            this.components.add(room);

            // 更新边界框
            this.boundingBox = room.getBoundingBox();
            updateBoundingBox();
        }

        public BlockPos getCenter() {
            return center;
        }

        @Override
        public void generateStructure(World world, Random rand, StructureBoundingBox sbb) {
            super.generateStructure(world, rand, sbb);
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            tag.setLong("Center", center.toLong());
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            this.center = BlockPos.fromLong(tag.getLong("Center"));
        }
    }

// 古迹废墟数据管理
    public static class TrailRuinsData extends WorldSavedData {
        public static final String DATA_NAME = "TrailRuinsData";
        private final Map<Long, TrailRuinsGen.TrailRuinsStart> temples = new HashMap<>();

        public TrailRuinsData(String name) {
            super(name);
        }

        public TrailRuinsData() {
            this(DATA_NAME);
        }

        public static TrailRuinsData getData(WorldServer world) {
            MapStorage storage = world.getMapStorage();
            TrailRuinsData data = (TrailRuinsData) storage.getOrLoadData(TrailRuinsData.class, DATA_NAME);
            
            if (data == null) {
                data = new TrailRuinsData();
                storage.setData(DATA_NAME, data);
            }
            return data;
        }

        public void addTrailRuins(TrailRuinsGen.TrailRuinsStart start) {
            long key = ChunkPos.asLong(start.getCenter().getX() >> 4, start.getCenter().getZ() >> 4);
            temples.put(key, start);
            markDirty();
        }

        public boolean isTrailRuinsNearby(int chunkX, int chunkZ, int distance) {
            for (long key : temples.keySet()) {
                int templeChunkX = (int) (key >> 32);
                int templeChunkZ = (int) (key & 0xFFFFFFFF);
                
                if (Math.abs(chunkX - templeChunkX) <= distance && 
                    Math.abs(chunkZ - templeChunkZ) <= distance) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagList list = nbt.getTagList("TrailRuins", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound templeTag = list.getCompoundTagAt(i);
                TrailRuinsGen.TrailRuinsStart start = new TrailRuinsGen.TrailRuinsStart();
                start.readFromNBT(templeTag);
                long key = ChunkPos.asLong(start.getCenter().getX() >> 4, start.getCenter().getZ() >> 4);
                temples.put(key, start);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (TrailRuinsGen.TrailRuinsStart start : temples.values()) {
                NBTTagCompound templeTag = new NBTTagCompound();
                start.writeToNBT(templeTag);
                list.appendTag(templeTag);
            }
            nbt.setTag("TrailRuins", list);
            return nbt;
        }
    }
}