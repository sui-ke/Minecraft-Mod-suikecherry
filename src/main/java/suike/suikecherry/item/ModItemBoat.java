package suike.suikecherry.item;

import java.util.List;

import suike.suikecherry.SuiKe;
import suike.suikecherry.entity.boat.ModEntityBoat;
import suike.suikecherry.entity.boat.ModEntityChestBoat;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;

// 船物品
public class ModItemBoat extends Item {
    public ModItemBoat(String name, Class<? extends ModEntityBoat> entityBoat) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
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

// 使用物品
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        // 计算视线轨迹
        Vec3d eyePos = new Vec3d(
            player.posX, 
            player.posY + player.getEyeHeight(), 
            player.posZ
        );
        Vec3d lookVec = player.getLookVec();
        Vec3d endPos = eyePos.add(lookVec.scale(5.0D)); // 最大射程5格

        // 执行方块级射线追踪
        RayTraceResult rayTrace = world.rayTraceBlocks(eyePos, endPos, true);
        if (rayTrace == null || rayTrace.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(EnumActionResult.PASS, itemStack);
        }

        // 检查实体碰撞
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(
            player, 
            player.getEntityBoundingBox()
                .expand(lookVec.x * 5.0D, lookVec.y * 5.0D, lookVec.z * 5.0D)
                .grow(1.0D)
        );
        for (Entity entity : entities) {
            if (entity.canBeCollidedWith() && entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize()).contains(eyePos)) {
                return new ActionResult<>(EnumActionResult.PASS, itemStack);
            }
        }

        // 获取点击方块信息
        BlockPos hitPos = rayTrace.getBlockPos();
        IBlockState state = world.getBlockState(hitPos);
        boolean isWater = state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER;

        // 计算船体生成坐标
        double yOffset = isWater ? rayTrace.hitVec.y - 0.12D : rayTrace.hitVec.y;
        Vec3d spawnPos = new Vec3d(rayTrace.hitVec.x, yOffset, rayTrace.hitVec.z);

        // 创建船只实体
        ModEntityBoat boat;
        try {
            boat = this.entityBoat.getConstructor(World.class).newInstance(world);
        } catch (Exception e) {
            e.printStackTrace();
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }

        boat.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
        boat.setRotation(player.rotationYaw, 0.0F);

        // 碰撞检测
        if (!world.getCollisionBoxes(boat, boat.getEntityBoundingBox().grow(-0.1D)).isEmpty()) {
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }

        // 服务器端生成实体
        if (!world.isRemote) {
            world.spawnEntity(boat);
            if (!player.capabilities.isCreativeMode) {
                itemStack.shrink(1);
            }
            player.addStat(StatList.getObjectUseStats(this));
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

// 燃料功能
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 400; //返回燃烧时间（单位：tick）
    }
}