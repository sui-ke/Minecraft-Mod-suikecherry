package suike.suikecherry.item;

import suike.suikecherry.SuiKe;
import suike.suikecherry.entity.boat.ModEntityBoat;
import suike.suikecherry.entity.boat.ModEntityChestBoat;

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
public class ModItemBoat extends Item {
    public ModItemBoat(String name, Class<? extends ModEntityBoat> entityBoat) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(name + "_" + SuiKe.MODID);
        /*设置堆叠数量*/this.setMaxStackSize(1);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.TRANSPORTATION);
        this.setBoat(entityBoat);
        ModEntityBoat.setBoatData(entityBoat, this, name);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private Class<? extends ModEntityBoat> entityBoat;
    private void setBoat(Class<? extends ModEntityBoat> entityBoat) {
        this.entityBoat = entityBoat;
    }

//使用物品
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));

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
    public void placeBoat(EntityPlayer player, World world, BlockPos pos, EnumHand hand) {
        Block block = world.getBlockState(pos).getBlock();
        int i = 0;
        BlockPos originalPos = pos;

        //防止放在水里
        while (block == Blocks.WATER) {
            pos = pos.up();
            block = world.getBlockState(pos).getBlock();

            i++;
            if (i >= 3) {
                pos = originalPos;
                break; // 深水区, 直接放在梳理
            }
        }

        ModEntityBoat boat = null;
        try {
            //创建船的实体
            boat = this.entityBoat.getDeclaredConstructor(World.class).newInstance(world);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 确保只在服务器端生成实体
        if (boat != null) {
            boat.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5); // 设置船的位置
            boat.setRotation(player.rotationYaw, 0.0F); // 设置船朝向为玩家朝向
            world.spawnEntity(boat); // 生成船只
            player.getHeldItem(hand).shrink(1); // 消耗一个物品
        }
    }

//燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 400; //返回燃烧时间（单位：tick）
    }
}