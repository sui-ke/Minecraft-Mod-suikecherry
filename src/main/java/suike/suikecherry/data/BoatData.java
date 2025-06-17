package suike.suikecherry.data;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ModItemBoat;

import net.minecraft.util.ResourceLocation;

public class BoatData {
    private final String boatName;
    private final ModItemBoat item;
    private final ResourceLocation textureFile;

    public BoatData(ModItemBoat item, String boatName) {
        this.item = item;
        this.boatName = boatName;
        this.textureFile = new ResourceLocation(SuiKe.MODID, "textures/entity/boat/" + boatName.replace("chest_", "") + ".png");
    }

    public ModItemBoat getModItemBoat() {
        return this.item;
    }

    public String getBoatName() {
        return this.boatName;
    }

    public ResourceLocation getTextureFile() {
        return this.textureFile;
    }

    public String toString() {
        return String.format("[船: %s, 纹理: %s]", this.boatName, this.textureFile);
    }
}