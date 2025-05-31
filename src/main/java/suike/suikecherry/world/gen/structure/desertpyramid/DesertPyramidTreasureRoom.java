package suike.suikecherry.world.gen.structure.desertpyramid;

import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.tileentity.BrushableTileEntity;
import suike.suikecherry.data.TreasureData.Structure;
import suike.suikecherry.data.TreasureRoomData;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.nbt.NBTTagCompound;

public class DesertPyramidTreasureRoom extends ComponentScatteredFeaturePieces.DesertPyramid {
    // seed 254280

    private static final IBlockState sandState = Blocks.SAND.getDefaultState();
    private static final IBlockState sandStoneState = Blocks.SANDSTONE.getDefaultState();
    private static final IBlockState suspiciousSandState = BlockBase.SUSPICIOUS_SAND.getDefaultState();
    private static final TreasureRoomData treasure_room_east_1 = new TreasureRoomData(9, 12, "treasure_room/treasure_room_east_1");
    private static final TreasureRoomData treasure_room_east_2 = new TreasureRoomData(9, 0, "treasure_room/treasure_room_east_2");
    private static final TreasureRoomData treasure_room_west_1 = new TreasureRoomData(3, 0, "treasure_room/treasure_room_west_1");
    private static final TreasureRoomData treasure_room_west_2 = new TreasureRoomData(3, 12, "treasure_room/treasure_room_west_2");
    private static final TreasureRoomData treasure_room_south_1 = new TreasureRoomData(0, 9, "treasure_room/treasure_room_south_1");
    private static final TreasureRoomData treasure_room_south_2 = new TreasureRoomData(12, 9, "treasure_room/treasure_room_south_2");
    private static final TreasureRoomData treasure_room_north_1 = new TreasureRoomData(12, 3, "treasure_room/treasure_room_north_1");
    private static final TreasureRoomData treasure_room_north_2 = new TreasureRoomData(0, 3, "treasure_room/treasure_room_north_2");

    private static final EnumFacing[] facings = {EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST,  EnumFacing.WEST};
    private static final BlockPos[] checkPositions = {new BlockPos(10, 5, 0), new BlockPos(10, 5, 20), new BlockPos(0, 5, 10), new BlockPos(20, 5, 10)};

    public DesertPyramidTreasureRoom() {}

    public DesertPyramidTreasureRoom(Random rand, int chunkX, int chunkZ) {
        super(rand, chunkX, chunkZ);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // 调用原版沙漠神殿生成逻辑
        boolean isVanillaGenerated = super.addComponentParts(world, rand, sbb);

        if (isVanillaGenerated) {
            // 生成藏宝室
            this.generateTreasureRoom(world, rand, sbb);
        }
        return isVanillaGenerated;
    }

// 生成藏宝室
    private void generateTreasureRoom(World world, Random rand, StructureBoundingBox sbb) {
        if (world.isRemote) return;

        // 获取神殿朝向
        EnumFacing pyramidFacing = getPyramidFacing(world, sbb);
        if (pyramidFacing == null) return;

        // 选择并获取藏宝室位置
        boolean roomSite = rand.nextBoolean();
        TreasureRoomData treasure_room = getTreasureRoomData(pyramidFacing, roomSite);

        // 计算绝对位置
        int worldX = this.boundingBox.minX + treasure_room.getX();
        int worldY = this.boundingBox.minY - 4;  // 固定Y坐标偏移
        int worldZ = this.boundingBox.minZ + treasure_room.getZ();

        // 加载NBT结构
        WorldServer worldServer = (WorldServer) world;
        TemplateManager manager = worldServer.getStructureTemplateManager();

        // 获取结构文件
        Template template = manager.getTemplate(
            worldServer.getMinecraftServer(), 
            treasure_room.getTreasureRoom()
        );

        if (template != null) {
            PlacementSettings settings = new PlacementSettings()
                .setReplacedBlock(null)
                .setIgnoreEntities(true);

            // 放置结构
            BlockPos pos = new BlockPos(worldX, worldY, worldZ);
            template.addBlocksToWorld(
                worldServer,
                pos,
                new TreasureRoomProcessor(world, rand),
                settings,
                2
            );
        }
    }

// 获取沙漠神殿方向
    private EnumFacing getPyramidFacing(World world, StructureBoundingBox sbb) {
        // 检查四个位置
        for (int i = 0; i < checkPositions.length; i++) {
            BlockPos worldPos = new BlockPos(
                this.boundingBox.minX + checkPositions[i].getX(),
                this.boundingBox.minY + checkPositions[i].getY(),
                this.boundingBox.minZ + checkPositions[i].getZ()
            );

            IBlockState state = world.getBlockState(worldPos);
            // 检查是否是雕纹砂岩 (meta = 1)
            if (state.getBlock() == Blocks.SANDSTONE && 
                state.getValue(BlockSandStone.TYPE) == BlockSandStone.EnumType.CHISELED) {
                    return facings[i];
            }
        }

        // 默认返回北向
        return null;
    }

// 获取藏宝室生成位置及结构
    private TreasureRoomData getTreasureRoomData(EnumFacing pyramidFacing, boolean roomSite) {
        switch (pyramidFacing) {
            case SOUTH:
                return roomSite ? treasure_room_south_1 : treasure_room_south_2;
            case EAST:
                return roomSite ? treasure_room_east_1 : treasure_room_east_2;
            case WEST:
                return roomSite ? treasure_room_west_1 : treasure_room_west_2;
            case NORTH:
            default:
                return roomSite ? treasure_room_north_1 : treasure_room_north_2;
        }
    }

// 藏宝室放置方块处理器
    public static class TreasureRoomProcessor implements ITemplateProcessor {
        private final World world;
        private final Random random;

        private static final IBlockState sandStoneState = Blocks.SANDSTONE.getDefaultState();
        private static final IBlockState suspiciousSandState = BlockBase.SUSPICIOUS_SAND.getDefaultState();

        public TreasureRoomProcessor(World world, Random random) {
            this.world = world;
            this.random = random;
        }

        @Override
        public Template.BlockInfo processBlock(World world, BlockPos pos, Template.BlockInfo blockInfo) {
            if (blockInfo.blockState.getBlock() == Blocks.STONE) {
                IBlockState state = this.getRandomBlockState();
                NBTTagCompound nbt = state == suspiciousSandState
                                    ? this.getTreasure(pos)
                                    : new NBTTagCompound();

                return new Template.BlockInfo(pos, state, nbt);
            }

            if (blockInfo.blockState.getBlock() == Blocks.GLASS) {
                IBlockState state = this.getRandomSuspiciousState();
                NBTTagCompound nbt = state == suspiciousSandState
                                    ? this.getTreasure(pos)
                                    : new NBTTagCompound();

                return new Template.BlockInfo(pos, state, nbt);
            }

            return blockInfo;
        }

        // 可疑方块
        private IBlockState getRandomBlockState() {
            float r = this.random.nextFloat();
            return r < 0.5f ? sandState : 
                   r < 0.9f ? sandStoneState : suspiciousSandState;
        }
        private IBlockState getRandomSuspiciousState() {
            return this.random.nextFloat() < 0.2f 
                ? suspiciousSandState
                : sandState;
        }
        private NBTTagCompound getTreasure(BlockPos pos) {
            BrushableTileEntity tile = new BrushableTileEntity();
            tile.initTreasure(this.world, pos, Structure.desertPyramid);
            return tile.writeToNBT(new NBTTagCompound());
        }
    }
}