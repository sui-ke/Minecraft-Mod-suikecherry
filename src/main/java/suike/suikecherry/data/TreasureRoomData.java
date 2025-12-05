package suike.suikecherry.data;

import suike.suikecherry.SuiKe;

import net.minecraft.util.ResourceLocation;

public class TreasureRoomData {
    private final int x, z;
    private final ResourceLocation temple_room;

    public TreasureRoomData(int x, int z, String temple_room) {
        this.x = x;
        this.z = z;
        this.temple_room = new ResourceLocation(SuiKe.MODID, temple_room);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public ResourceLocation getTreasureRoom() {
        return this.temple_room;
    }

    public String toString() {
        return String.format("[位置: x%d, z%d, 结构: %s]", this.x, this.z, this.temple_room.toString());
    }
}