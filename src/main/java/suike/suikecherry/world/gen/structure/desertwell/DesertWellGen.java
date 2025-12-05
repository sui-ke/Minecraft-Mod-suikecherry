package suike.suikecherry.world.gen.structure.desertwell;

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

public class DesertWellGen implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            // 获取当前区块的生物群系
            BlockPos pos = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);

            // 仅生成在沙漠
            if (world.getBiome(pos) instanceof BiomeDesert && canGenerateHere(world, chunkX, chunkZ, random)) {
                // 创建结构起始点
                DesertWellStart start = new DesertWellStart(world, random, chunkX, chunkZ);

                // 获取边界框
                StructureBoundingBox sbb = start.getBoundingBox();

                // 生成结构
                start.generateStructure(world, random, sbb);

                // 保存结构数据
                DesertWellData.getData((WorldServer) world).addDesertWell(start);
            }
        }
    }

    private boolean canGenerateHere(World world, int chunkX, int chunkZ, Random random) {
        // 检查生成概率
        if (random.nextInt(648) >= 4) return false;

        // 检查是否已经生成过
        DesertWellData data = DesertWellData.getData((WorldServer) world);
        if (data.isDesertWellNearby(chunkX, chunkZ, 16)) {
            return false;
        }

        return true;
    }

// 沙漠水井起始点
    public static class DesertWellStart extends StructureStart {
        private BlockPos center;

        public DesertWellStart() {}

        public DesertWellStart(World world, Random rand, int chunkX, int chunkZ) {
            super(chunkX, chunkZ);
            this.center = new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8);

            // 创建神殿组件
            DesertWell room = new DesertWell(world, rand, center.getX(), center.getZ());
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

// 沙漠水井数据管理
    public static class DesertWellData extends WorldSavedData {
        public static final String DATA_NAME = "DesertWellTreasureData";
        private final Map<Long, DesertWellGen.DesertWellStart> temples = new HashMap<>();

        public DesertWellData(String name) {
            super(name);
        }

        public DesertWellData() {
            this(DATA_NAME);
        }

        public static DesertWellData getData(WorldServer world) {
            MapStorage storage = world.getMapStorage();
            DesertWellData data = (DesertWellData) storage.getOrLoadData(DesertWellData.class, DATA_NAME);
            
            if (data == null) {
                data = new DesertWellData();
                storage.setData(DATA_NAME, data);
            }
            return data;
        }

        public void addDesertWell(DesertWellGen.DesertWellStart start) {
            long key = ChunkPos.asLong(start.getCenter().getX() >> 4, start.getCenter().getZ() >> 4);
            temples.put(key, start);
            markDirty();
        }

        public boolean isDesertWellNearby(int chunkX, int chunkZ, int distance) {
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
            NBTTagList list = nbt.getTagList("DesertWellTreasure", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound templeTag = list.getCompoundTagAt(i);
                DesertWellGen.DesertWellStart start = new DesertWellGen.DesertWellStart();
                start.readFromNBT(templeTag);
                long key = ChunkPos.asLong(start.getCenter().getX() >> 4, start.getCenter().getZ() >> 4);
                temples.put(key, start);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (DesertWellGen.DesertWellStart start : temples.values()) {
                NBTTagCompound templeTag = new NBTTagCompound();
                start.writeToNBT(templeTag);
                list.appendTag(templeTag);
            }
            nbt.setTag("DesertWellTreasure", list);
            return nbt;
        }
    }
}