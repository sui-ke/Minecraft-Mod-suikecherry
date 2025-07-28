/*/
package suike.suikecherry.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("suikecherryasm")
@IFMLLoadingPlugin.SortingIndex(1)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class ASMCoreMod implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
            "suike.suikecherry.asm.LayerArmorBaseASM"
        };
    }

    @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) {}
    @Override public String getModContainerClass() { return null; }
    @Override public String getAccessTransformerClass() { return null; }
}//*/