package suike.suikecherry.inter;

import net.minecraftforge.common.BiomeDictionary;

public interface IBiomes {
    int getWeight();
    String getBOPType();
    BiomeDictionary.Type[] getDictType();
}