package suike.suikecherry.world.gen.structure.desertwell;

import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.BlockBase;
import suike.suikecherry.block.ModBlockBrushable;
import suike.suikecherry.tileentity.BrushableTileEntity;
import suike.suikecherry.data.TreasureData.Structure;

import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.nbt.NBTTagCompound;

public class DesertWell extends StructureComponent {

    private final World world;

    private static final ResourceLocation desert_well = new ResourceLocation(SuiKe.MODID, "desert_well");
    private static final PlacementSettings settings = new PlacementSettings().setReplacedBlock(null).setIgnoreEntities(true);

    public DesertWell(World world, Random rand, int x, int z) {
        super(0);
        this.world = world;
        this.boundingBox = new StructureBoundingBox(x, 60, z, x + 5, 80, z + 5);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        if (world.isRemote) return false;

        WorldServer worldServer = (WorldServer) world;
        TemplateManager manager = worldServer.getStructureTemplateManager();

        BlockPos center = new BlockPos(
            this.boundingBox.minX + 3 + rand.nextInt(10),
            0,
            this.boundingBox.minZ + 3 + rand.nextInt(10)
        );

        // 获取地面高度
        center = world.getHeight(center);
        if (center.getY() < 60 || center.getY() > 80) return false;

        // 获取结构文件
        Template template = manager.get(worldServer.getMinecraftServer(), this.desert_well);

        if (template == null) return false;

        // 防止生成在其他结构上
        if (world.getBlockState(center.down()).getBlock() != Blocks.SAND) return false;

        // 放置结构
        template.addBlocksToWorld(
            worldServer,
            center.down(3),
            new DesertWellProcessor(world, rand),
            settings,
            2
        );

        return true;
    }

// 沙漠水井放置方块处理器
    public static class DesertWellProcessor implements ITemplateProcessor {
        private int a;
        private final World world;
        private final Random random;

        private static final IBlockState sandStoneState = Blocks.SANDSTONE.getDefaultState();
        private static final IBlockState suspiciousSandState = BlockBase.SUSPICIOUS_SAND.getDefaultState();

        public DesertWellProcessor(World world, Random random) {
            this.a = 0;
            this.world = world;
            this.random = random;
        }

        @Override
        public Template.BlockInfo processBlock(World world, BlockPos pos, Template.BlockInfo blockInfo) {
            if (blockInfo.blockState.getBlock() == Blocks.GLASS) {
                IBlockState state = this.getRandomSuspiciousState();
                NBTTagCompound nbt = state == suspiciousSandState
                                    ? this.getTreasure(pos)
                                    : new NBTTagCompound();

                return new Template.BlockInfo(pos, state, nbt);
            }

            return blockInfo;
        }

        // 可疑方块
        private IBlockState getRandomSuspiciousState() {
            boolean b = (this.a < 3) && (this.random.nextFloat() < 0.3f);
            if (b) this.a++;
            return b 
                ? suspiciousSandState
                : sandStoneState;
        }
        private NBTTagCompound getTreasure(BlockPos pos) {
            BrushableTileEntity tile = new BrushableTileEntity();
            tile.initTreasure(this.world, pos, Structure.desertWell);
            return tile.writeToNBT(new NBTTagCompound());
        }
    }

    @Override
    public void readStructureFromNBT(NBTTagCompound a, TemplateManager b) {}

    @Override
    public void writeStructureToNBT(NBTTagCompound a) {}
}