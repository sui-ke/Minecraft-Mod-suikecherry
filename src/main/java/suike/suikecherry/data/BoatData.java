package suike.suikecherry.data;

import java.util.List;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ModItemBoat;

import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

public class BoatData {
    private final String boatName;
    private final ModItemBoat item;
    private final ResourceLocation textureFile;

    public BoatData(ModItemBoat item, String boatName) {
        this.item = item;
        this.boatName = boatName;
        String id = getId(boatName);
        if (id.equals("minecraft")) {
            boatName = boatName.replace("_boat", "").replace("dark_", "dark").replace("dark_", "dark");
            boatName = "boat_" + boatName;
        }
        this.textureFile = new ResourceLocation(id, "textures/entity/boat/" + boatName.replace("_chest", "") + ".png");
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

    public static List<String> MCType = Lists.newArrayList(
        "oak", "spruce", "acacia", "birch", "jungle"
    );

    private static String getId(String boatName) {
        for (String type : MCType) {
            if (boatName.contains(type)) {
                return "minecraft";
            }
        }
        return SuiKe.MODID;
    }
}