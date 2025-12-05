package suike.suikecherry.world.gen.structure.oceanruins;

import java.util.*;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.google.common.collect.Lists;

public class OceanRuinsGen implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            // 获取当前区块的生物群系
            BlockPos pos = new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8);
            Biome biome = world.getBiome(pos);

            // 仅生成在海洋生物群系
            if (biome instanceof BiomeOcean && canGenerateHere(world, chunkX, chunkZ, random)) {
                // 判断是否为寒带海洋
                // boolean isFrozen;

                // 创建结构起始点
                OceanRuinsStart start = new OceanRuinsStart(world, random, chunkX, chunkZ, true);

                // 获取边界框
                StructureBoundingBox sbb = start.getBoundingBox();

                // 生成结构
                start.generateStructure(world, random, sbb);

                // 保存结构数据
                OceanRuinsData.getData((WorldServer) world).addRuins(start);
            }
        }
    }

    private boolean canGenerateHere(World world, int chunkX, int chunkZ, Random rand) {
        // 检查生成概率
        if (rand.nextInt(1600) >= 4) return false;

        // 检查是否已经生成过
        OceanRuinsData data = OceanRuinsData.getData((WorldServer) world);
        if (data.isRuinsNearby(chunkX, chunkZ, 3)) {
            return false;
        }

        return true;
    }

// 海底废墟起始点
    public static class OceanRuinsStart extends StructureStart {
        private BlockPos center;
        private boolean isFrozen;

        public OceanRuinsStart() {}

        public OceanRuinsStart(World world, Random rand, int chunkX, int chunkZ, boolean isFrozen) {
            super(chunkX, chunkZ);
            this.center = new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8);
            this.isFrozen = isFrozen;
            
            // 创建废墟组件
            OceanRuins ruin = new OceanRuins(world, rand, center.getX(), center.getZ(), isFrozen);
            this.components.add(ruin);
            
            // 更新边界框
            this.boundingBox = ruin.getBoundingBox();
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

// 海底废墟数据管理
    public static class OceanRuinsData extends WorldSavedData {
        public static final String DATA_NAME = "OceanRuinsData";
        private final Map<Long, OceanRuinsGen.OceanRuinsStart> ruins = new HashMap<>();

        public OceanRuinsData(String name) {
            super(name);
        }

        public OceanRuinsData() {
            this(DATA_NAME);
        }

        public static OceanRuinsData getData(WorldServer world) {
            MapStorage storage = world.getMapStorage();
            OceanRuinsData data = (OceanRuinsData) storage.getOrLoadData(OceanRuinsData.class, DATA_NAME);
            
            if (data == null) {
                data = new OceanRuinsData();
                storage.setData(DATA_NAME, data);
            }
            return data;
        }

        public void addRuins(OceanRuinsGen.OceanRuinsStart start) {
            long key = ChunkPos.asLong(start.getCenter().getX() >> 4, start.getCenter().getZ() >> 4);
            ruins.put(key, start);
            markDirty();
        }

        public boolean isRuinsNearby(int chunkX, int chunkZ, int distance) {
            for (long key : ruins.keySet()) {
                int ruinChunkX = (int) (key >> 32);
                int ruinChunkZ = (int) (key & 0xFFFFFFFF);
                
                if (Math.abs(chunkX - ruinChunkX) <= distance && 
                    Math.abs(chunkZ - ruinChunkZ) <= distance) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagList list = nbt.getTagList("OceanRuins", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound ruinTag = list.getCompoundTagAt(i);
                OceanRuinsGen.OceanRuinsStart start = new OceanRuinsGen.OceanRuinsStart();
                start.readFromNBT(ruinTag);
                long key = ChunkPos.asLong(start.getCenter().getX() >> 4, start.getCenter().getZ() >> 4);
                ruins.put(key, start);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            NBTTagList list = new NBTTagList();
            for (OceanRuinsGen.OceanRuinsStart start : ruins.values()) {
                NBTTagCompound ruinTag = new NBTTagCompound();
                start.writeToNBT(ruinTag);
                list.appendTag(ruinTag);
            }
            nbt.setTag("OceanRuins", list);
            return nbt;
        }
    }
}