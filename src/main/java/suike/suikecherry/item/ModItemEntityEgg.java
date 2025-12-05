package suike.suikecherry.item;

import java.util.List;

import suike.suikecherry.SuiKe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityAnimal;

// 生物蛋
public class ModItemEntityEgg extends Item {
    public ModItemEntityEgg(String name, Class<? extends EntityLiving> entityClass) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.MISC);
        this.setEntityClass(entityClass);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);
    }

    private Class<? extends EntityLiving> entityClass;
    private void setEntityClass(Class<? extends EntityLiving> entityClass) {
        this.entityClass = entityClass;
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

        // 计算生成位置
        Vec3d spawnPos;
        if (rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            // 如果点击的是方块，在点击面上方生成
            spawnPos = new Vec3d(
                rayTrace.hitVec.x,
                rayTrace.hitVec.y + (rayTrace.sideHit == EnumFacing.UP ? 0.0 : 0.5),
                rayTrace.hitVec.z
            );
        } else {
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }

        // 创建生物实体
        EntityLiving entity;
        try {
            entity = this.entityClass.getConstructor(World.class).newInstance(world);
        } catch (Exception e) {
            e.printStackTrace();
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);
        }

        entity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

        // 服务器端生成实体
        if (!world.isRemote) {
            world.spawnEntity(entity);
            if (!player.capabilities.isCreativeMode) {
                itemStack.shrink(1);
            }
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }
}