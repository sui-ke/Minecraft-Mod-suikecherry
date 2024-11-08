package suike.suikecherry.sblock;

import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sitem.ItemBase;
import suike.suikecherry.world.CherryTree;
import suike.suikecherry.sound.SoundTypeNone;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

//树苗
public class SBlockSapling extends BlockSapling implements SBlock {
    public SBlockSapling(String name) {
        /*创建方块实例*/super();
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置硬度*/setHardness(0.0F);
        /*设置抗爆性*/setResistance(0.0F);
        /*设置不透明度*/setLightOpacity(0);
        /*清除声音*/setSoundType(new SoundTypeNone());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
    }

//获取物品&掉落物
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        //返回树苗的物品
        return new ItemStack(ItemBase.CHERRY_SAPLING);
    }
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        //返回树苗的掉落物
        return ItemBase.CHERRY_SAPLING;
    }

//生长
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (拥有空间生长(world, pos)) {
            if (!world.isRemote) {
                CherryTree.生成樱花树(world, pos, null, true);
            }
        }
    }

//检查是否拥有空间生长
    public static boolean 拥有空间生长(World world, BlockPos pos) {
        Block block = world.getBlockState(pos.up()).getBlock();
        //检查正上方一格
        if (block != Blocks.AIR) { //不为空气
            if (block != BlockBase.CHERRY_LOG) { //不为树干
                return false; //不允许生长
            }
        }

        //检查上方5x*5z*6y
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 2; y <= 5; y++) {
                    /*获取方块*/block = world.getBlockState(pos.add(x, y, z)).getBlock();
                    if (block != BlockBase.CHERRY_LEAVES && block != BlockBase.CHERRY_LOG && !block.toString().equals("Block{minecraft:air}")) {
                        return false; //如果有一个方块不是树叶, 树干, 空气, 则不允许生长
                    }
                }
            }
        }

        return true; //所有方块都是树叶, 树干, 空气, 则允许生长
    }
}