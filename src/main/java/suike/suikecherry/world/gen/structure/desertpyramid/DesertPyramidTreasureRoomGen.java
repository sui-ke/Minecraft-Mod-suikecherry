package suike.suikecherry.world.gen.structure.desertpyramid;

import java.util.Map;
import java.util.Random;
import java.util.HashMap;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeDesert;
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

public class DesertPyramidTreasureRoomGen implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            // 获取当前区块的生物群系
            BlockPos pos = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);

            // 仅生成在沙漠
            if (world.getBiome(pos) instanceof BiomeDesert && canGenerateHere(world, chunkX, chunkZ, random)) {
                // 创建结构起始点
                DesertPyramidStart start = new DesertPyramidStart(world, random, chunkX, chunkZ);

                // 获取边界框
                StructureBoundingBox sbb = start.getBoundingBox();

                // 向下拓展边界框
                sbb = new StructureBoundingBox(
                    sbb.minX, 
                    50, // 向下扩展 14 格
                    sbb.minZ, 
                    sbb.maxX, 
                    sbb.maxY, 
                    sbb.maxZ
                );

                // 生成结构
                start.generateStructure(world, random, sbb);

                // 保存结构数据
                DesertPyramidData.getData((WorldServer) world).addTreasure(start);
            }
        }
    }

    private boolean canGenerateHere(World world, int chunkX, int chunkZ, Random rand) {
        // 检查生成概率
        if (rand.nextInt(6000) >= 4) return false;

        // 检查是否已经生成过
        DesertPyramidData data = DesertPyramidData.getData((WorldServer) world);
        if (data.isTreasureNearby(chunkX, chunkZ, 5)) {
            return false;
        }

        return true;
    }

// 神殿起始点
    public static class DesertPyramidStart extends StructureStart {
        private BlockPos center;

        public DesertPyramidStart() {}

        public DesertPyramidStart(World world, Random rand, int chunkX, int chunkZ) {
            super(chunkX, chunkZ);
            this.center = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);

            // 创建神殿组件
            DesertPyramidTreasureRoom room = new DesertPyramidTreasureRoom(rand, center.getX(), center.getZ());
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

// 神殿数据管理
    public static class DesertPyramidData extends WorldSavedData {
        public static final String DATA_NAME = "DesertTreasuresData";
        private final Map<Long, DesertPyramidTreasureRoomGen.DesertPyramidStart> temples = new HashMap<>();

        public DesertPyramidData(String name) {
            super(name);
        }

        public DesertPyramidData() {
            this(DATA_NAME);
        }

        public static DesertPyramidData getData(WorldServer world) {
            MapStorage storage = world.getMapStorage();
            DesertPyramidData data = (DesertPyramidData) storage.getOrLoadData(DesertPyramidData.class, DATA_NAME);
            
            if (data == null) {
                data = new DesertPyramidData();
                storage.setData(DATA_NAME, data);
            }
            return data;
        }

        public void addTreasure(DesertPyramidTreasureRoomGen.DesertPyramidStart start) {
            long key = ChunkPos.asLong(start.getCenter().getX() >> 4, start.getCenter().getZ() >> 4);
            temples.put(key, start);
            markDirty();
        }

        public boolean isTreasureNearby(int chunkX, int chunkZ, int distance) {
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
            NBTTagList list = nbt.getTagList("DesertPyramidTreasure", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound templeTag = list.getCompoundTagAt(i);
                DesertPyramidTreasureRoomGen.DesertPyramidStart start = new DesertPyramidTreasureRoomGen.DesertPyramidStart();
                start.readFromNBT(templeTag);
                long key = ChunkPos.asLong(start.getCenter().getX() >> 4, start.getCenter().getZ() >> 4);
                temples.put(key, start);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (DesertPyramidTreasureRoomGen.DesertPyramidStart start : temples.values()) {
                NBTTagCompound templeTag = new NBTTagCompound();
                start.writeToNBT(templeTag);
                list.appendTag(templeTag);
            }
            nbt.setTag("DesertPyramidTreasure", list);
            return nbt;
        }
    }
}