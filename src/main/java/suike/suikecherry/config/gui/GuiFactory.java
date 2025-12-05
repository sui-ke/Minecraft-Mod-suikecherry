package suike.suikecherry.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class GuiFactory implements IModGuiFactory {

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent) {
        return new ConfigGui(parent);
    }

    @Override public void initialize(Minecraft minecraft) {}
    @Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return null; }
}