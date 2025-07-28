/*package suike.suikecherry.entity.sniffer;

import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import suike.suikecherry.item.ItemBase;
import suike.suikecherry.entity.sniffer.EntitySniffer.State;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;

import com.google.common.collect.ImmutableSet;

public class SnifferBrain extends EntityAIBase {
    private final EntitySniffer sniffer;
    private BlockPos targetPos; // 挖掘目标
    private int retryCount;
    private static final int MAX_RETRY = 3;
    private static final int SEARCH_RANGE = 20;
    private static final int VERTICAL_RANGE = 3;

    // 可探测的方块列表
    private static final Set<IBlockState> DIGGABLE_BLOCKS = ImmutableSet.of();

    public SnifferBrain(EntitySniffer sniffer) {
        this.sniffer = sniffer;
    }

    @Override
    public boolean shouldExecute() {
        // 尝试设置为嗅探状态
        sniffer.setState(State.SNIFFING);
        // 是否进入嗅探状态
        if (true) return false;
        return sniffer.getState() == EntitySniffer.State.SNIFFING;
    }

    @Override
    public void startExecuting() {}

    @Override
    public void updateTask() {
        // 检查是否到达目标
        if (sniffer.getDistanceSqToCenter(this.targetPos) < 2.0D) {
            startDigging();
        } else {
            moveToTarget();
        }
    }

    // 在范围内搜索可挖掘方块
    private void findTargetBlock() {
        BlockPos center = sniffer.getPosition();
        List<BlockPos> candidates = new ArrayList<>();

        for (int x = -SEARCH_RANGE; x <= SEARCH_RANGE; x++) {
            for (int y = -VERTICAL_RANGE; y <= VERTICAL_RANGE; y++) {
                for (int z = -SEARCH_RANGE; z <= SEARCH_RANGE; z++) {
                    BlockPos pos = center.add(x, y, z);
                    if (DIGGABLE_BLOCKS.contains(sniffer.world.getBlockState(pos).getBlock())) {
                        candidates.add(pos);
                    }
                }
            }
        }

        if (!candidates.isEmpty()) {
            this.targetPos = candidates.get(sniffer.getRandom().nextInt(candidates.size()));
            sniffer.getNavigator().tryMoveToXYZ(
                this.targetPos.getX() + 0.5D, 
                this.targetPos.getY(), 
                this.targetPos.getZ() + 0.5D, 
                1.25D
            );
        } else {
            this.retryCount++;
            if (this.retryCount >= MAX_RETRY) {
                this.resetTask();
            }
        }
    }

    private void moveToTarget() {
        // 检查路径是否可达
        Path path = sniffer.getNavigator().getPath();
        if (path != null && path.isFinished()) {
            this.retryCount++;
            if (this.retryCount >= MAX_RETRY) {
                this.resetTask();
            } else {
                findTargetBlock();
            }
        }
    }

    @Override
    public void resetTask() {}

    private void finishDigging() {
        // 生成战利品
        ItemStack loot = new ItemStack(this.getRandomLoot());
        if (!ItemBase.isValidItemStack(loot)) return;

        // 设为空闲
        sniffer.setState(State.IDLING);
    }

    private Item getRandomLoot() {
        return null; 
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (true) return false;
        return sniffer.getState() == EntitySniffer.State.SNIFFING 
            || sniffer.getState() == EntitySniffer.State.DIGGING;
    }
}*/