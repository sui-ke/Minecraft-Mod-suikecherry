package suike.suikecherry.expand.futuremc;

import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.reflect.Field;

import suike.suikecherry.block.BlockBase;

import thedarkcolour.futuremc.registry.FBlocks;
import thedarkcolour.futuremc.tile.BeeHiveTile;
import thedarkcolour.futuremc.entity.bee.EntityBee;
import thedarkcolour.futuremc.block.buzzybees.BeeHiveBlock;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;

public class PlaceBeeHive {
    private static final Block cherryLog = BlockBase.CHERRY_LOG;
    private static final EnumFacing[] DIRECTIONS = {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
    private static final IBlockState beeNestBlockstate = Block.getBlockFromItem(Item.getByNameOrId("futuremc:bee_nest")).getDefaultState();

    public static void place(World world, BlockPos treePos, Random random) {
        // 获取生成坐标
        BlockPos beeHivePos = getBeeHivePos(world, treePos.up(3), random);
        if (beeHivePos == null) return;

        // 获取方块方向
        EnumFacing facing = getValidFacing(world, beeHivePos, random);
        if (facing == null) return;

        // 设置方块的朝向属性
        IBlockState state = beeNestBlockstate.withProperty(BeeHiveBlock.FACING, facing);

        // 放置蜂巢
        world.setBlockState(beeHivePos, state, 2);
        world.notifyBlockUpdate(beeHivePos, Blocks.AIR.getDefaultState(), state, 3);

        // 获取蜂巢的 TileEntity 并添加蜜蜂
        TileEntity tileEntity = world.getTileEntity(beeHivePos);
        if (tileEntity instanceof BeeHiveTile) {
            BeeHiveTile beeHiveTile = (BeeHiveTile) tileEntity;
            addBeesToHive(world, beeHiveTile, beeHivePos, random);
        }
    }

    // 获取蜂巢位置
    private static BlockPos getBeeHivePos(World world, BlockPos pos, Random random) {
        List<BlockPos> airPositions = new ArrayList<>(); // 存储空气坐标的列表

        // 遍历 5x2x5 范围
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = pos.add(x, y, z); // 计算当前坐标
                    if (world.isAirBlock(checkPos) && isAdjacentToCherryLog(world, checkPos)) { // 如果是空气 且 邻近树干
                        airPositions.add(checkPos); // 添加到列表
                    }
                }
            }
        }

        return airPositions.isEmpty() ? null : airPositions.get(random.nextInt(airPositions.size()));
    }

    // 检查坐标是否邻近树干 cherryLog
    private static boolean isAdjacentToCherryLog(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (world.getBlockState(pos.offset(facing)).getBlock() == cherryLog) {
                return true;
            }
        }
        return false;
    }

    private static EnumFacing getValidFacing(World world, BlockPos pos, Random random) {
        List<EnumFacing> validDirections = new ArrayList<>(Arrays.asList(DIRECTIONS));

        while (!validDirections.isEmpty()) {
            EnumFacing facing = validDirections.get(random.nextInt(validDirections.size()));
            validDirections.remove(facing);

            if (world.isAirBlock(pos.offset(facing))) {
                return facing;
            }
        }

        return null; // 没有符合条件的朝向
    }

    private static void addBeesToHive(World world, BeeHiveTile beeHiveTile, BlockPos pos, Random random) {
        int beeAmount = 2 + random.nextInt(2); // 2-3 只蜜蜂
        for (int i = 0; i < beeAmount; i++) {
            EntityBee bee = getBee(world, pos);
            beeHiveTile.tryEnterHive(bee, false, random.nextInt(599)); // 尝试添加到蜂巢
        }

        beeHiveTile.markDirty();
    }

    public static EntityBee getBee(World world, BlockPos beeHivePos) {
        EntityBee bee = new EntityBee(world);

        try {
            //为蜜蜂绑定蜂巢
            Field hivePosField = EntityBee.class.getDeclaredField("hivePos");
            hivePosField.setAccessible(true);
            hivePosField.set(bee, beeHivePos);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return bee;
    }
}