package suike.suikecherry.world.gen.structure.oceanruins;

import java.util.*;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockBrushable;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.expand.oe.SpawnDrowned;
import suike.suikecherry.data.EnchData;
import suike.suikecherry.data.LootTableData;
import suike.suikecherry.data.TreasureData.Structure;
import suike.suikecherry.tileentity.BrushableTileEntity;

import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;

public class OceanRuins extends StructureComponent {
    // seed 8700015448746822241 // seed 254280 /tp 4194 35 -462

    // 小型石砖废墟
    private static final List<ResourceLocation> STONE_STRUCTURES = Lists.newArrayList(
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/brick_1"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/brick_2"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/brick_3"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/brick_4"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/brick_5"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/brick_6"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/brick_7"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/brick_8")
    );

    // 大型石砖废墟
    private static final List<ResourceLocation> BIG_STONE_STRUCTURES = Lists.newArrayList(
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/big_brick_1"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/big_brick_2"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/big_brick_3"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/big_brick_4")
    );

    /*/ 小型沙岩废墟
    private static final List<ResourceLocation> SAND_STONE_STRUCTURES = Lists.newArrayList(
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/warm_1"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/warm_2")
    );

    // 大型沙岩废墟
    private static final List<ResourceLocation> BIG_SAND_STONE_STRUCTURES = Lists.newArrayList(
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/big_warm_1"),
        new ResourceLocation(SuiKe.MODID, "underwater_ruin/big_warm_2")
    );*/

    private final World world;
    private final boolean isBig;
    private final boolean isFrozen;
    private final Rotation rotation;
    private final ResourceLocation oceanRuinsStructure;
    private final OceanRuinsProcessor processor;

    public OceanRuins(World world, Random rand, int x, int z, boolean isFrozen) {
        super(0);
        this.world = world;
        this.isBig = rand.nextBoolean();
        this.isFrozen = isFrozen;
        this.oceanRuinsStructure = this.getRandomStructure(rand);
        this.rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
        this.processor = new OceanRuinsProcessor(world, rand, this.isBig, isFrozen);

        this.boundingBox = new StructureBoundingBox(x, 45, z, x + 15, 64, z + 15);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        if (world.isRemote) return false;
        this.boundingBox = sbb;

        WorldServer worldServer = (WorldServer) world;
        TemplateManager manager = worldServer.getStructureTemplateManager();

        // 获取结构文件
        Template template = manager.get(
            worldServer.getMinecraftServer(),
            this.oceanRuinsStructure
        );

        if (template == null) return false;

        // 设置放置参数
        PlacementSettings settings = new PlacementSettings()
            .setRotation(this.rotation)
            .setReplacedBlock(null)
            .setIgnoreEntities(true);

        boolean a = this.isBig ? this.genBigOceanRuins(world, worldServer, rand, template, settings)
                               : this.genOceanRuinsGroup(world, worldServer, rand, manager, template, settings);

        return a;
    }

// 生成大型海底废墟
    private boolean genBigOceanRuins(World world, WorldServer worldServer, Random rand, Template template, PlacementSettings settings) {
        BlockPos center = new BlockPos(
            this.boundingBox.minX + 8,
            0,
            this.boundingBox.minZ + 8
        );

        // 获取海床高度
        int seafloorY = getSeafloorHeightOnBig(world, center);
        if (seafloorY > 58) return false;

        BlockPos structurePos = new BlockPos(
            center.getX(),
            seafloorY,
            center.getZ()
        );

        // 放置结构
        template.addBlocksToWorld(
            worldServer,
            structurePos,
            this.processor,
            settings,
            2
        );

        this.spawnDrowned(world, structurePos, template, 2, 5, rand);

        return true;
    }

// 大型废墟获取海床高度
    private int getSeafloorHeightOnBig(World world, BlockPos center) {
        int validCorners = 0;
        int totalY = 0;
        int minY = 100;

        // 检查中心点和四个角
        BlockPos[] positions = {
            center,
            center.add(0, 0, 16),  // 东北角
            center.add(16, 0, 0),  // 西南角
            center.add(16, 0, 16)  // 东南角
        };

        for (BlockPos pos : positions) {
            for (int y = 52; y > 20; y--) {
                BlockPos testPos = new BlockPos(pos.getX(), y, pos.getZ());
                IBlockState state = world.getBlockState(testPos);

                // 检查是否为可放置的固体方块
                if (!world.isAirBlock(testPos) && state.isOpaqueCube()) {
                    if (world.getBlockState(testPos).getMaterial() != Material.WATER) {
                        validCorners++;
                        totalY += y;
                        if (y < minY) minY = y;
                        break;
                    }
                }
            }
        }

        if (validCorners < 4) {
            return 100; // 取消生成
        }

        // 计算平均高度，但偏向最低点
        return minY + (int) ((totalY / validCorners - minY) * 0.2);
    }

// 生成海底废墟群
    private boolean genOceanRuinsGroup(World world, WorldServer worldServer, Random rand, TemplateManager manager, Template template, PlacementSettings settings) {
        BlockPos center = new BlockPos(
            this.boundingBox.minX + 8,
            0,
            this.boundingBox.minZ + 8
        );

        // 获取第一个结构的海床高度
        int seafloorY = getSimpleSeafloorHeight(world, center);
        if (seafloorY > 58) return false;

        // 放置第一个结构
        BlockPos firstPos = new BlockPos(
            center.getX(),
            seafloorY,
            center.getZ()
        );

        template.addBlocksToWorld(
            world,
            firstPos,
            this.processor,
            settings,
            2
        );

        this.spawnDrowned(world, firstPos, template, 0, 1, rand);

        // 生成 2~4 个附加结构 (总共 1~5 个)
        int count = 2 + rand.nextInt(4);
        List<BlockPos> placedPositions = Lists.newArrayList(firstPos);

        Random randPos = new Random(this.world.getSeed() + firstPos.toLong() * 3);
        for (int i = 0; i < count; i++) {
            // 随机角度和距离 (10~16 格)
            float angle = randPos.nextFloat() * 2 * (float) Math.PI;
            int distance = 10 + randPos.nextInt(6);

            // 计算新位置
            BlockPos newCenter = new BlockPos(
                center.getX() + (int) (Math.cos(angle) * distance),
                0,
                center.getZ() + (int) (Math.sin(angle) * distance)
            );

            // 获取新位置的海床高度
            int newSeafloorY = getSimpleSeafloorHeight(world, newCenter);
            if (newSeafloorY > 56) continue;

            // 检查是否与其他结构重叠
            BlockPos newPos = new BlockPos(
                newCenter.getX() - template.getSize().getX() / 2,
                newSeafloorY - 1,
                newCenter.getZ() - template.getSize().getZ() / 2
            );

            boolean tooClose = placedPositions.stream().anyMatch(pos -> 
                pos.distanceSq(newPos) < 25 // 5格距离平方
            );

            if (tooClose) continue;

            // 随机获取小型结构
            randPos = new Random(this.world.getSeed() + newPos.toLong() * 7);
            ResourceLocation smallStructure = this.getRandomStructure(randPos);

            Template smallTemplate = manager.get(worldServer.getMinecraftServer(), smallStructure);
            if (smallTemplate == null) continue;

            // 放置结构
            smallTemplate.addBlocksToWorld(
                world,
                newPos,
                this.processor,
                settings.setRotation(Rotation.values()[randPos.nextInt(Rotation.values().length)]),
                2
            );

            this.spawnDrowned(world, newPos, smallTemplate, 0, 1, randPos);

            placedPositions.add(newPos);
        }

        return true;
    }

// 海底废墟群获取海床高度
    private int getSimpleSeafloorHeight(World world, BlockPos center) {
        int validCorners = 0;
        int totalY = 0;
        int minY = 100;

        BlockPos[] positions = {
            center,
            center.add(0, 0, 6),  // 东北角
            center.add(6, 0, 0),  // 西南角
            center.add(6, 0, 6)  // 东南角
        };

        for (BlockPos pos : positions) {
            for (int y = 52; y > 20; y--) {
                BlockPos testPos = new BlockPos(pos.getX(), y, pos.getZ());
                IBlockState state = world.getBlockState(testPos);

                // 检查是否为可放置的固体方块
                if (!world.isAirBlock(testPos) && state.isOpaqueCube()) {
                    if (world.getBlockState(testPos).getMaterial() != Material.WATER) {
                        validCorners++;
                        totalY += y;
                        if (y < minY) minY = y;
                        break;
                    }
                }
            }
        }

        if (validCorners < 4) {
            return 100; // 取消生成
        }

        // 计算平均高度，但偏向最低点
        return minY + (int) ((totalY / validCorners - minY) * 0.2);
    }

// 随机获取结构
    private ResourceLocation getRandomStructure(Random rand) {
        List<ResourceLocation> structures;

        if (this.isBig) {
            structures = BIG_STONE_STRUCTURES;
        } else {
            structures = STONE_STRUCTURES;
        }

        /*if (this.isBig) {
            structures = isFrozen ? BIG_STONE_STRUCTURES : BIG_SAND_STONE_STRUCTURES;
        } else {
            structures = isFrozen ? STONE_STRUCTURES : SAND_STONE_STRUCTURES;
        }*/

        return structures.get(rand.nextInt(structures.size()));
    }

// 海底废墟放置方块处理器
    public static class OceanRuinsProcessor implements ITemplateProcessor {
        private final World world;
        private final Random random;
        private final boolean isBig;
        private final boolean isFrozen;
        private boolean hasChests;

        private static final IBlockState mossyStoneBricks = Blocks.STONEBRICK.getStateFromMeta(1);
        private static final IBlockState crackedStoneBricks = Blocks.STONEBRICK.getStateFromMeta(2);

        private static final IBlockState suspiciousSandState = BlockBase.SUSPICIOUS_SAND.getDefaultState();
        private static final IBlockState suspiciousGravelState = BlockBase.SUSPICIOUS_GRAVEL.getDefaultState();

        public OceanRuinsProcessor(World world, Random random, boolean isBig, boolean isFrozen) {
            this.world = world;
            this.random = random;
            this.isBig = isBig;
            this.isFrozen = isFrozen;
            this.hasChests = false;
        }

        @Override
        public Template.BlockInfo processBlock(World world, BlockPos pos, Template.BlockInfo blockInfo) {
            IBlockState state = blockInfo.blockState;
            Block block = state.getBlock();

            if (block == Blocks.STONEBRICK && block.getMetaFromState(state) != 3) {
                return new Template.BlockInfo(pos, this.getRandomBricksState(state), blockInfo.tileentityData);
            }

            if (block == Blocks.GLASS) {
                IBlockState newState = this.getRandomSuspiciousState();
                NBTTagCompound nbt = newState == suspiciousGravelState
                                    ? this.getTreasure(pos)
                                    : new NBTTagCompound();

                return new Template.BlockInfo(pos, newState, nbt);
            }

            if (block == Blocks.CHEST) {
                return this.processChest(pos, state, blockInfo);
            }

            return blockInfo;
        }

        // 石砖随机
        private IBlockState getRandomBricksState(IBlockState state) {
            float r = this.random.nextFloat();
            return r < 0.7f ? state :
                   r < 0.9f ? mossyStoneBricks : crackedStoneBricks;
        }

        // 可疑方块
        private IBlockState getRandomSuspiciousState() {
            IBlockState suspiciou = this.getSuspiciousState();
            return this.random.nextFloat() < 0.6f 
                ? suspiciou
                : this.getDefaultBlock();
        }
        private NBTTagCompound getTreasure(BlockPos pos) {
            BrushableTileEntity tile = new BrushableTileEntity();
            tile.initTreasure(this.world, pos, Structure.oceanRuins);
            return tile.writeToNBT(new NBTTagCompound());
        }

        // 箱子战利品
        private Template.BlockInfo processChest(BlockPos pos, IBlockState state, Template.BlockInfo blockInfo) {
            if (this.isBig || (!this.hasChests && this.random.nextFloat() < 0.4f)) {
                this.hasChests = true;
                // 初始化箱子战利品
                TileEntityChest tile = new TileEntityChest();
                tile.setLootTable(
                    this.isBig ? LootTableData.big_underwater_ruin : LootTableData.underwater_ruin,
                    this.random.nextLong()
                );

                // 返回修改后的方块信息
                return new Template.BlockInfo(pos, state, tile.writeToNBT(new NBTTagCompound()));
            } else {
                return new Template.BlockInfo(pos, this.getDefaultBlock(), null);
            }
        }

        private IBlockState getSuspiciousState() {
            return this.isFrozen ? suspiciousGravelState : suspiciousSandState;
        }

        private IBlockState getDefaultBlock() {
            return ((ModBlockBrushable) this.getSuspiciousState().getBlock()).getDefaultBlock();
        }
    }

// 生成溺尸
    public void spawnDrowned(World world, BlockPos center, Template template, int minCount, int maxCount, Random rand) {
        if (Examine.OEID) {
            SpawnDrowned.spawn(
                world,
                center.add(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2),
                minCount,
                maxCount,
                rand
            );
        }
    }

    @Override
    public void readStructureFromNBT(NBTTagCompound a, TemplateManager b) {}

    @Override
    public void writeStructureToNBT(NBTTagCompound a) {}
}