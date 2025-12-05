package suike.suikecherry.world.gen.decoratedpot;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ModItemPotterySherd;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockDecoratedPot;
import suike.suikecherry.tileentity.DecoratedPotTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagCompound;

public class DecoratedPotGen {

    private static final IBlockState POT = BlockBase.DECORATED_POT.getDefaultState();
    private static final EnumFacing[] DIRECTIONS = {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};

    public static void placedPot(World world, BlockPos pos) {
        // 获取所有纹饰类型
        placedPot(world, pos, ModItemPotterySherd.getDefaultPotteryTypes());
    }

    public static void placedPot(World world, BlockPos pos, String... potteryTypes) {
        Random rand = getRandom(world.getSeed(), pos);
        String[] sherdIDs = getPotSherdIDs(rand, potteryTypes);

        // 随机朝向
        IBlockState state = POT.withProperty(ModBlockDecoratedPot.FACING, DIRECTIONS[rand.nextInt(DIRECTIONS.length)]);

        world.setBlockState(pos, state);

        // 设置陶罐纹饰
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof DecoratedPotTileEntity) {
            ((DecoratedPotTileEntity) tile).setDecoratedPotFromString(sherdIDs);
        }
    }

    // 获取纹饰组
    public static NBTTagCompound getPotNBT(World world, BlockPos pos, String... potteryTypes) {
        NBTTagCompound nbt = new NBTTagCompound();
        String[] sherdIDs = getPotSherdIDs(world, pos, potteryTypes);

        NBTTagList list = new NBTTagList();
        for (String id : sherdIDs) {
            list.appendTag(new NBTTagString(id != null ? id : "brick"));
        }
        nbt.setTag("Sherds", list);

        return nbt;
    }

    public static String[] getPotSherdIDs(World world, BlockPos pos, String... potteryTypes) {
        return getPotSherdIDs(getRandom(world.getSeed(), pos), potteryTypes);
    }
    private static String[] getPotSherdIDs(Random rand, String... potteryTypes) {
        if (potteryTypes == null || potteryTypes.length == 0) {
            // 使用默认纹饰列表
            potteryTypes = ModItemPotterySherd.getDefaultPotteryTypes();
        }

        String[] sherdIDs = new String[4];
        boolean hasNonBrick = false;

        // 随机选择纹饰
        for (int i = 0; i < sherdIDs.length; i++) {
            if (potteryTypes.length == 0 || rand.nextFloat() < 0.25f) {
                sherdIDs[i] = "brick";
            } else {
                sherdIDs[i] = potteryTypes[rand.nextInt(potteryTypes.length)];
                hasNonBrick = true;
            }
        }

        // 如果全是 "brick"，强制替换其中一个为随机纹饰
        if (!hasNonBrick) {
            sherdIDs[rand.nextInt(sherdIDs.length)] = potteryTypes[rand.nextInt(potteryTypes.length)];
        }

        return sherdIDs;
    }

    private static Random getRandom(long seed, BlockPos pos) {
        long totalSeed = seed
            + (pos.getX() >> 4) * 1234567L
            + (pos.getZ() >> 4) * 9876543L
            + pos.getX() * 127L 
            + pos.getY() * 317L 
            + pos.getZ() * 911L;

        return new Random(totalSeed);
    }
}