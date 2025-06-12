package suike.suikecherry.data;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class TrailRuinsBuildSlot {
    private final Random rand;
    private final int roadLength;
    private SlotType left;
    private SlotType right;
    private static final int ROAD_WIDTH = 5;

    public TrailRuinsBuildSlot (Random rand, int roadLength) {
        this.rand = rand;
        this.roadLength = roadLength;
        this.left = new SlotType("left", roadLength);
        this.right = new SlotType("right", roadLength);
    }

    public int getMaxSlot() {
        return left.slots.size() + right.slots.size();
    }

// 获取生成位置
    public AxisPosition getGenPos(String type, boolean isXAxis) {
        if (!hasAvailableSlots()) return null;

        SlotType slotType = chooseTargetSlots();
        List<Integer> targetSlots = slotType.slots;

        int index = this.rand.nextInt(targetSlots.size());
        int slot = targetSlots.get(index);

        AxisPosition pos;
        if ("room".equals(type)) {
            pos = this.getGenPosFromGenType(slotType, slot, isXAxis, 7);
        }
        else if ("full".equals(type)) {
            pos = this.getGenPosFromGenType(slotType, slot, isXAxis, 5);
        }
        else if ("hall".equals(type)) {
            pos = this.getGenPosFromGenType(slotType, slot, isXAxis, 11);
        }
        else {
            return null;
        }

        targetSlots.remove(index);
        return pos;
    }

// 选择槽位侧 (优先非空侧)
    public boolean hasAvailableSlots() {
        return !left.isEmpty() || !right.isEmpty();
    }
    private SlotType chooseTargetSlots() {
        if (left.isEmpty()) return right;
        if (right.isEmpty()) return left;
        return this.rand.nextBoolean() ? left : right;
    }

// 根据类型返回位置偏移
    public AxisPosition getGenPosFromGenType(SlotType slotType, int slot, boolean isXAxis, int lateralOffset) {
        // 槽位位置
        int baseOffset = (slot - 1) * 9; // slot=1 → 0, slot=2 → 9, slot=3 → 18
        baseOffset += this.rand.nextInt(5) - 2; // 随机微调(-2 ~ 3)

        // 侧向偏移 (left/right): 相对道路原点偏移
        int sideOffset;
        if ("right".equals(slotType.type)) {
            // 右侧规则：
            // x 轴遗迹: 朝南方偏移: z+
            // z 轴遗迹: 朝西方偏移: x-
            sideOffset = isXAxis ? ROAD_WIDTH : -ROAD_WIDTH;
        } else {
            // 左侧规则：
            // x 轴遗迹: 朝北方偏移: z-
            // z 轴遗迹: 朝东方偏移: x+
            sideOffset = isXAxis ? -lateralOffset : lateralOffset;
        }

        int x = isXAxis ? baseOffset : sideOffset;
        int z = isXAxis ? sideOffset : baseOffset;

        return new AxisPosition(x, z);
    }

// 各侧槽位信息
    private class SlotType {
        private final String type;
        private List<Integer> slots;

        private SlotType(String type, int roadLength) {
            this.type = type;
            this.slots = this.generateSlots(roadLength);
        }

        private List<Integer> generateSlots(int roadLength) {
            List<Integer> slots = new ArrayList<>();
            for (int i = 1; i <= roadLength; i++) {
                slots.add(i);
            }
            return slots;
        }

        private boolean isEmpty() {
            return this.slots.isEmpty();
        }
    }
}