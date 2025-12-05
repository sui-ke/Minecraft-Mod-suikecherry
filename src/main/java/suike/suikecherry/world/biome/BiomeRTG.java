package suike.suikecherry.world.biome;

import java.util.*;

import rtg.api.config.BiomeConfig;
import rtg.api.world.RTGWorld;
import rtg.api.world.deco.DecoBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.surface.SurfaceGeneric;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class BiomeRTG extends RealisticBiomeBase {

    // 1080685322359309583
    // /tp 1596 92 2264

    public BiomeRTG(Biome biome) {
        super(biome);
    }

    @Override
    public TerrainBase initTerrain() {
        return new BiomeRTGTerrain();
    }

    @Override
    public SurfaceBase initSurface() {
        return new SurfaceGeneric(
            this.getConfig(),
            Blocks.GRASS.getDefaultState(),
            Blocks.DIRT.getDefaultState()
        );
    }

    @Override
    public ResourceLocation baseBiomeResLoc() {
        return new ResourceLocation("minecraft", this.baseBiome().getBiomeName().toString().replace("suikecherry:", ""));
    }

    @Override public void initDecos() {}
    @Override public void addDeco(DecoBase deco) {}
    @Override public boolean allowVanillaTrees() { return false; }
    @Override public void addTree(TreeRTG tree, boolean allowed) {}
    @Override public void addDeco(DecoBase deco, boolean allowed) {}
    @Override public Collection<TreeRTG> getTrees() { return new ArrayList<>(); }
    @Override public Collection<DecoBase> getDecos() { return new ArrayList<>(); }

    public class BiomeRTGTerrain extends TerrainBase {
        public BiomeRTGTerrain() {
            super();
        }

        @Override
        public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
            return this.terrainHighland((float)x, (float)y, rtgWorld, river, 10.0F, 68.0F, 40.0F, this.base - 62.0F);
        }
    }
}