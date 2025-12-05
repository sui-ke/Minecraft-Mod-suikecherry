package suike.suikecherry.world.gen.structure.trailruins;

import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.io.InputStream;
import java.io.IOException;

import suike.suikecherry.SuiKe;
import suike.suikecherry.config.CreateConfigFile;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockBrushable;
import suike.suikecherry.tileentity.BrushableTileEntity;
import suike.suikecherry.data.AxisPosition;
import suike.suikecherry.data.TrailRuinsBuildSlot;
import suike.suikecherry.data.TreasureData.Structure;

import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;

import com.google.common.collect.Lists;

public class TrailRuins extends StructureComponent {
    // -6283703233594719083 /tp -1694 70 -1534

    // 主道路废墟
    public static final List<ResourceLocation> ROAD_STRUCTURES = Lists.newArrayList(
        new ResourceLocation(SuiKe.MODID, "trail_ruins/road_section_1"),
        new ResourceLocation(SuiKe.MODID, "trail_ruins/road_section_2"),
        new ResourceLocation(SuiKe.MODID, "trail_ruins/road_section_3")
    );
    // 居民房屋废墟
    public static final List<ResourceLocation> ROOM_STRUCTURES = Lists.newArrayList(
        new ResourceLocation(SuiKe.MODID, "trail_ruins/one_room_1"),
        new ResourceLocation(SuiKe.MODID, "trail_ruins/one_room_2"),
        new ResourceLocation(SuiKe.MODID, "trail_ruins/one_room_3")
    );
    // 塔楼废墟
    public static final List<ResourceLocation> FULL_STRUCTURES = Lists.newArrayList(
        new ResourceLocation(SuiKe.MODID, "trail_ruins/group_full_1"),
        new ResourceLocation(SuiKe.MODID, "trail_ruins/group_full_2"),
        new ResourceLocation(SuiKe.MODID, "trail_ruins/group_full_3")
    );
    // 大厅废墟
    public static final List<ResourceLocation> HALL_STRUCTURES = Lists.newArrayList(
        new ResourceLocation(SuiKe.MODID, "trail_ruins/group_hall_1"),
        new ResourceLocation(SuiKe.MODID, "trail_ruins/group_hall_2"),
        new ResourceLocation(SuiKe.MODID, "trail_ruins/group_hall_3")
    );

    private final World world;
    private final int roadLength;
    private final boolean isXAxis;
    private final PlacementSettings settings;

    private int genY;
    private Random rand;
    private TrailRuinsProcessor trailRuinsPro;
    private TrailRuinsBuildSlot buildSlot;

    public TrailRuins(World world, Random rand, int x, int z) {
        super(0);
        this.world = world;
        this.isXAxis = rand.nextBoolean();
        this.roadLength = rand.nextInt(4) + 2; // 2 ~ 5 个道路结构
        this.buildSlot = new TrailRuinsBuildSlot(rand, this.roadLength);
        this.boundingBox = new StructureBoundingBox(x, 0, z, x, 0, z);
        this.settings = new PlacementSettings()
            .setRotation(this.isXAxis ? Rotation.NONE : Rotation.CLOCKWISE_90)
            .setReplacedBlock(null)
            .setIgnoreEntities(true);
    }

// 主生成方法
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        if (world.isRemote) return false;

        this.rand = rand;
        this.trailRuinsPro = new TrailRuinsProcessor(world, rand);

        WorldServer worldServer = (WorldServer) world;

        BlockPos center = new BlockPos(
            this.boundingBox.minX + 3 + rand.nextInt(10),
            0,
            this.boundingBox.minZ + 3 + rand.nextInt(10)
        );

        // 获取地面高度
        center = world.getHeight(center);
        if (center.getY() > 80) return false;

        // 将废墟埋入地下
        this.genY = center.down(12 + rand.nextInt(3)).getY(); // 生成在空中测试结构生成

        // 获取初始生成坐标
        center = new BlockPos(
            center.getX(),
            this.genY,
            center.getZ()
        );

        TemplateManager manager = worldServer.getStructureTemplateManager();

        // 生成道路
        this.genRoadSection(manager, worldServer, center);

        // 生成子结构
        int[] structureCounts = distributeSlots(this.buildSlot.getMaxSlot());
        this.genRoomSection(manager, worldServer, center, structureCounts[0]);
        this.genFullSection(manager, worldServer, center, structureCounts[1]);
        this.genHallSection(manager, worldServer, center, structureCounts[2]);

        return true;
    }

// 生成主道路
    public void genRoadSection(TemplateManager manager, WorldServer worldServer, BlockPos center) {
        for (int i = 1; i <= this.roadLength; i++) {
            // 获取结构
            ResourceLocation structure = ROAD_STRUCTURES.get(this.rand.nextInt(ROAD_STRUCTURES.size()));
            Template template = manager.get(worldServer.getMinecraftServer(), structure);
            if (template == null) continue;

            this.gen(worldServer, template, center);

            // 道路间距
            center = new BlockPos(
                center.getX() + (this.isXAxis ? 9 : 0),
                this.genY,
                center.getZ() + (this.isXAxis ? 0 : 9)
            );
        }
    }

// 生成居民房屋废墟
    public void genRoomSection(TemplateManager manager, WorldServer worldServer, BlockPos originalRoadCenter, int maxRoom) {
        BlockPos roadCenter = originalRoadCenter;

        for (int i = 1; i <= maxRoom && i <= 3; i++) {
            // 获取结构
            ResourceLocation structure = ROOM_STRUCTURES.get(this.rand.nextInt(ROOM_STRUCTURES.size()));
            Template template = manager.get(worldServer.getMinecraftServer(), structure);
            if (template == null) continue;

            // 获取生成位置
            AxisPosition offset = this.buildSlot.getGenPos("room", this.isXAxis);
            if (offset == null) continue;

            roadCenter = new BlockPos(
                originalRoadCenter.getX() + offset.getX(),
                this.genY,
                originalRoadCenter.getZ() + offset.getZ()
            );

            this.gen(worldServer, template, roadCenter);
        }
    }

// 生成塔楼废墟
    public void genFullSection(TemplateManager manager, WorldServer worldServer, BlockPos originalRoadCenter, int maxFull) {
        BlockPos roadCenter = originalRoadCenter;

        for (int i = 1; i <= maxFull && i <= 2; i++) {
            // 获取结构
            ResourceLocation structure = FULL_STRUCTURES.get(this.rand.nextInt(FULL_STRUCTURES.size()));
            Template template = manager.get(worldServer.getMinecraftServer(), structure);
            if (template == null) continue;

            // 获取生成位置
            AxisPosition offset = this.buildSlot.getGenPos("full", this.isXAxis);
            if (offset == null) continue;

            roadCenter = new BlockPos(
                originalRoadCenter.getX() + offset.getX(),
                this.genY,
                originalRoadCenter.getZ() + offset.getZ()
            );

            this.gen(worldServer, template, roadCenter);
        }
    }

// 生成大厅废墟
    public void genHallSection(TemplateManager manager, WorldServer worldServer, BlockPos originalRoadCenter, int maxHall) {
        BlockPos roadCenter = originalRoadCenter;

        for (int i = 1; i <= maxHall && i <= 3; i++) {
            // 获取结构
            ResourceLocation structure = HALL_STRUCTURES.get(this.rand.nextInt(HALL_STRUCTURES.size()));
            Template template = manager.get(worldServer.getMinecraftServer(), structure);
            if (template == null) continue;

            // 获取生成位置
            AxisPosition offset = this.buildSlot.getGenPos("hall", this.isXAxis);
            if (offset == null) continue;

            roadCenter = new BlockPos(
                originalRoadCenter.getX() + offset.getX(),
                this.genY,
                originalRoadCenter.getZ() + offset.getZ()
            );

            this.gen(worldServer, template, roadCenter);
        }
    }

// 生成结构
    public void gen(WorldServer worldServer, Template template, BlockPos center) {
        if (this.world.isAirBlock(center)) return; // 避免生成在空中

        template.addBlocksToWorld(
            worldServer,
            center,
            this.trailRuinsPro,
            this.settings,
            2
        );
    }

// 分配数量
    private int[] distributeSlots(int totalSlots) {
        int[] counts = new int[3]; // 0=room, 1=full, 2=hall

        // 基础保证：每种类型至少1个（如果totalSlots>=3）
        int guaranteed = Math.min(1, totalSlots / 3);
        Arrays.fill(counts, guaranteed);

        int remaining = totalSlots - (guaranteed * 3);

        // 随机分配剩余槽位 (可能不全部用完)
        while (remaining > 0) {
            int type = this.rand.nextInt(3);

            // 当前类型数量不超过平均值+2
            if (counts[type] < (totalSlots / 3) + 2) {
                counts[type]++;
                remaining--;
            }

            if (this.rand.nextFloat() < 0.3f) {
                break; // 30%概率提前终止分配
            }
        }

        return counts;
    }

// 古迹废墟放置方块处理器
    public static class TrailRuinsProcessor implements ITemplateProcessor {
        private boolean a;
        private final World world;
        private final Random random;

        private static final IBlockState gravelState = Blocks.GRAVEL.getDefaultState();
        private static final IBlockState suspiciousGravelState = BlockBase.SUSPICIOUS_GRAVEL.getDefaultState();

        private static final ResourceLocation bookNBT = new ResourceLocation(SuiKe.MODID, "trail_ruins/book_suspicious_gravel");

        public TrailRuinsProcessor(World world, Random random) {
            this.a = true;
            this.world = world;
            this.random = random;
        }

        @Override
        public Template.BlockInfo processBlock(World world, BlockPos pos, Template.BlockInfo blockInfo) {
            if (blockInfo.blockState == gravelState) {
                IBlockState state = this.getRandomSuspiciousState();
                NBTTagCompound nbt = state == suspiciousGravelState
                                    ? this.getTreasure(pos)
                                    : new NBTTagCompound();

                return new Template.BlockInfo(pos, state, nbt);
            }

            return blockInfo;
        }

        // 可疑方块
        private IBlockState getRandomSuspiciousState() {
            return this.random.nextFloat() < 0.15f
                ? suspiciousGravelState
                : gravelState;
        }
        private NBTTagCompound getTreasure(BlockPos pos) {
            BrushableTileEntity tile = new BrushableTileEntity();
            tile.initTreasure(this.world, pos, Structure.trailRuins);
            NBTTagCompound nbt = tile.writeToNBT(new NBTTagCompound());

            // 低概率触发彩蛋, 每个遗迹只能有一个
            if (this.a && this.random.nextFloat() < 0.0004f) {
                nbt = getTreasureBook(nbt);
            }

            return nbt;
        }

        // 彩蛋: 陶罐学徒的日记
        private NBTTagCompound getTreasureBook(NBTTagCompound nbt) {
            if (!nbt.hasKey("Treasure")) return nbt;

            try (InputStream input = this.getClass().getResourceAsStream("/assets/suikecherry/structures/trail_ruins/book_suspicious_gravel.nbt")) {
                if (input != null) {
                    NBTTagCompound predefinedNbt = CompressedStreamTools.readCompressed(input);
                    if (predefinedNbt != null && predefinedNbt.hasKey("Treasure")) {
                        nbt.setTag("Treasure", predefinedNbt.getTag("Treasure"));
                        this.a = false;
                    }
                }
            } catch (IOException e) {}

            return nbt;
        }
    }

    @Override
    public void readStructureFromNBT(NBTTagCompound a, TemplateManager b) {}

    @Override
    public void writeStructureToNBT(NBTTagCompound a) {}
}