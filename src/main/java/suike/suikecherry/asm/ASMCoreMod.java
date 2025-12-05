package suike.suikecherry.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("suikecherryasm")
@IFMLLoadingPlugin.SortingIndex(1)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class ASMCoreMod implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "suike.suikecherry.asm.ClassTransformer" };
    }

    @Override public String getSetupClass() { return null; }
    @Override public String getModContainerClass() { return null; }
    @Override public String getAccessTransformerClass() { return null; }
    @Override public void injectData(java.util.Map<String, Object> data) {}
}