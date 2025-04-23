package suike.suikecherry.sitem;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sentity.boat.CherryBoatEntity;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;

//船物品
public class SItemBoat extends Item implements SItem {
    public SItemBoat(String name, CreativeTabs tabs) {
        /*设置物品名*/setRegistryName(name);
        /*设置物品名key*/setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置堆叠数量*/setMaxStackSize(1);
        /*设置创造模式物品栏*/setCreativeTab(tabs);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

//使用物品
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        Vec3d lookVec = player.getLookVec();
        Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d end = start;

        while (end.distanceTo(start) < 5) { // 最大距离为5
            RayTraceResult rayTraceResult = world.rayTraceBlocks(start, end, false, false, true);
                if (rayTraceResult != null && rayTraceResult.getBlockPos() != null) {

                    /*获取坐标*/BlockPos pos = rayTraceResult.getBlockPos();
                    /*获取当前坐标方块*/Block block = world.getBlockState(pos).getBlock();

                    //方块不为空气
                    if (block != Blocks.AIR) {
                        //为水或方块
                        if (block == Blocks.WATER || rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {

                            EnumFacing facing = rayTraceResult.sideHit;
                            if (facing == EnumFacing.UP) {
                                /*放置船*/placeBoat(player, world, pos.up(), hand);
                                break;
                            }
                        }
                    }
                }
                
            end = start.add(lookVec.scale(end.distanceTo(start) + 1)); //每次向前推进0.5格
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

//放置船
    public static void placeBoat(EntityPlayer player, World world, BlockPos pos, EnumHand hand) {
        Block block = world.getBlockState(pos).getBlock();
        int i = 0;
        //防止放在水里
        while (block == Blocks.WATER) {
            pos = pos.up();
            block = world.getBlockState(pos).getBlock();

            i++;
            if (i >= 3) {
                return;
            }
        }

        //确保只在服务器端生成实体
        if (!world.isRemote) {
            CherryBoatEntity boat = new CherryBoatEntity(world); //创建船的实体
            boat.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5); //设置船的位置
            boat.setCherryBoatRotation(player.rotationYaw, 0.0F);
            world.spawnEntity(boat);
            /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
        }
    }

//燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 400; //返回燃烧时间（单位：tick）
    }
}