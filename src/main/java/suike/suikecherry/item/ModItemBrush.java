package suike.suikecherry.item;

import java.util.List;
import java.util.ArrayList;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.block.ModBlockBrushable;
import suike.suikecherry.tileentity.BrushableTileEntity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;

public class ModItemBrush extends Item {
    private static final int MAX_USE_DURATION = 600; // 最大使用时长
    private static final double MAX_DISTANCE = 4.5;  // 最大有效距离

    private static final String brushingGeneric = "item.brush.brushing.generic";

    public ModItemBrush(String name) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置堆叠数量*/this.setMaxStackSize(1);
        /*设置最大耐久值*/this.setMaxDamage(250);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.TOOLS);

        /*添加到ITEMS列表*/ItemBase.ITEMS.add(this);

        // 添加动画
        this.addPropertyOverride(
            new ResourceLocation("brushing"),
            new IItemPropertyGetter() {
                @Override
                public float apply(ItemStack stack, World world, EntityLivingBase entity) {
                    if (entity instanceof EntityPlayer && entity.isHandActive() && entity.getActiveItemStack() == stack) {
                        int cycle = ((stack.getMaxItemUseDuration() - ((EntityPlayer) entity).getItemInUseCount()) % 20) / 5;
                        return cycle == 0 ? 0.25f : 
                               cycle == 1 ? 0.5f  : 
                               cycle == 2 ? 0.75f : 0.5f;
                    }
                    return 0;
                }
            }
        );
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return MAX_USE_DURATION;
    }

// 右键开始触发使用
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        // 验证距离
        if (this.canReachBlock(player, pos)) {
            // 开始使用
            player.setActiveHand(hand);
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase user, int count) {
        if (user instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) user;
            // 计算剩余使用时间
            int remainingTicks = this.getMaxItemUseDuration(stack) - count;
            // 每10 ticks触发一次效果（第5、15、25...tick）
            if (remainingTicks % 10 == 5) {
                // 获取目标方块位置
                World world = player.world;
                RayTraceResult ray = rayTrace(world, player, false);
                if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos targetPos = ray.getBlockPos();
                    if (world.isRemote) {
                        // 生成粒子效果
                        this.spawnDustParticles(world, targetPos, ray.sideHit);
                    }

                    String brushingSound = brushingGeneric;
                    Block block = world.getBlockState(targetPos).getBlock();
                    if (block instanceof ModBlockBrushable) {
                        ModBlockBrushable brushableBlock = (ModBlockBrushable) block;
                        brushingSound = brushableBlock.getBrushingSound();

                        if (!world.isRemote && remainingTicks % 20 == 5) {
                            BrushableTileEntity tile = (BrushableTileEntity) world.getTileEntity(targetPos);
                            tile.upDusted(player, stack, ray.sideHit);
                        }
                    }

                    Sound.playSound(world, targetPos, brushingSound);
                }
            }
        }
    }

// 验证玩家与方块的交互距离
    private boolean canReachBlock(EntityPlayer player, BlockPos pos) {
        Vec3d eyesPos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d blockCenter = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
        double distance = eyesPos.distanceTo(blockCenter);
        return distance <= MAX_DISTANCE;
    }

// 生成粒子效果
    private void spawnDustParticles(World world, BlockPos pos, EnumFacing facing) {
        double baseX = pos.getX() + 0.5 + facing.getFrontOffsetX() * 0.51;
        double baseY = pos.getY() + 0.5 + facing.getFrontOffsetY() * 0.51;
        double baseZ = pos.getZ() + 0.5 + facing.getFrontOffsetZ() * 0.51;

        for (int i = 0; i < 8; ++i) {
            double offsetX = (facing.getAxis() != EnumFacing.Axis.X) ? world.rand.nextDouble() - 0.5 : 0;
            double offsetY = (facing.getAxis() != EnumFacing.Axis.Y) ? world.rand.nextDouble() - 0.5 : 0;
            double offsetZ = (facing.getAxis() != EnumFacing.Axis.Z) ? world.rand.nextDouble() - 0.5 : 0;

            // 应用面方向偏移
            offsetX *= 0.7 * Math.abs(facing.getFrontOffsetX() + 1);
            offsetY *= 0.7 * Math.abs(facing.getFrontOffsetY() + 1);
            offsetZ *= 0.7 * Math.abs(facing.getFrontOffsetZ() + 1);

            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, 
                baseX + offsetX,
                baseY + offsetY,
                baseZ + offsetZ,
                0.0D, 0.0D, 0.0D,
                Block.getStateId(world.getBlockState(pos))
            );
        }
    }

// 考古物品栏
    public static final List<Item> ARCHAEOLOGY_ITEMS = new ArrayList<>();
    public static final ArchaeologyTabs ARCHAEOLOGY_TABS = new ArchaeologyTabs("suikecherry.archaeologyTabs");

    public static void InventoryTabs() {
        ARCHAEOLOGY_TABS.displayAllRelevantItems(NonNullList.create());
    }

    public static class ArchaeologyTabs extends CreativeTabs {

        private ArchaeologyTabs(String label) {
            super(label);
        }

        @Override
        public ItemStack getTabIconItem() {
            //设置分类窗口的图标
            return new ItemStack(ItemBase.BRUSH);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> items) {
            super.displayAllRelevantItems(items);

            items.add(getTabIconItem());
            for (Item item : ARCHAEOLOGY_ITEMS) {
                items.add(new ItemStack(item));
            }
            items.addAll(ModItemPotterySherd.getAllSherdItme());
            items.add(new ItemStack(ItemBase.MUSIC_DISC_RELIC));
        }
    }
}