package suike.suikecherry.block;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import suike.suikecherry.SuiKe;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItemSmithingTemplate;
import suike.suikecherry.data.TrimData;
import suike.suikecherry.expand.Examine;
import suike.suikecherry.client.gui.GuiID;
import suike.suikecherry.achievement.ModAdvancements;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;

import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.registry.GameRegistry;

// 锻造台类
public class ModBlockSmithingTable extends Block {
    public ModBlockSmithingTable(String name) {
        super(Material.WOOD);
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.DECORATIONS);
        /*设置硬度*/this.setHardness(2.5F);
        /*设置抗爆性*/this.setResistance(2.5F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(0);
        /*设置声音*/this.setSoundType(ModBlockPlanks.EnumType.getEnumType(name).getWoodSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

// 锻造台配方
    public static void registerRecipe() {
        GameRegistry.addShapedRecipe(
            new ResourceLocation(SuiKe.MODID, "smithing_table"),
            new ResourceLocation(SuiKe.MODID),
            new ItemStack(BlockBase.SMITHING_TABLE),
            "AA",
            "BB", 
            "BB",
            'A', new OreIngredient("ingotIron"),
            'B', new OreIngredient("plankWood")
        );
    }

// 方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos clickPos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing clickFacing, float hitX, float hitY, float hitZ) {
        player.openGui(SuiKe.instance, GuiID.GUI_SMITHING_TABLE, world, 0, 0, 0);
        return true;
    }

// 容器
    public static class SmithingTableContainer extends Container {
        static { addNetheriteRecipe(); }
        private static final int inventoryStartX = 8;
        private static final int inventoryStartY = 84;
        private static final int hotbarStartY = 142;

        private final EntityPlayer player;
        private final InventoryBasic craftMatrix;
        private final InventoryBasic craftResult;
        private final List<String> craftPatterns = new ArrayList<>();

    // 创建容器
        public SmithingTableContainer(EntityPlayer player) {
            this.player = player;

            // 输入
            this.craftMatrix = new InventoryBasic("smithing", false, 3); // 创建 3x1 的合成矩阵 (模板, 盔甲, 材料)
            this.addSlotToContainer(new Slot(this.craftMatrix, 0, 8, 48) {     // 模板槽 (0)
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return stack.getItem() instanceof ModItemSmithingTemplate; // 只允许模板
                }
            });
            this.addSlotToContainer(new Slot(this.craftMatrix, 1, 26, 48) {    // 盔甲槽 (1)
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return (stack.getItem() instanceof ItemArmor) || isDiamondItem(stack);
                }
            });
            this.addSlotToContainer(new Slot(this.craftMatrix, 2, 44, 48) {    // 材料槽 (2)
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return TrimData.getMaterial(stack) != null; // 只允许有效材料
                }
            });

            // 输出
            this.craftResult = new InventoryBasic("result", false, 1);
            this.addSlotToContainer(new Slot(this.craftResult, 0, 98, 48) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return false; // 禁止放入物品
                }
                @Override
                public boolean canTakeStack(EntityPlayer player) {
                    return true; // 允许取出
                }
            });

            // 添加玩家物品栏
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    this.addSlotToContainer(new Slot(player.inventory, col + row * 9 + 9, 
                        inventoryStartX + col * 18, 
                        inventoryStartY + row * 18)
                    );
                }
            }
            // 玩家快捷栏
            for (int slot = 0; slot < 9; ++slot) {
                this.addSlotToContainer(new Slot(player.inventory, slot, 
                    inventoryStartX + slot * 18, 
                    hotbarStartY)
                );
            }

            // 添加合成矩阵监听器
            this.craftMatrix.addInventoryChangeListener(inventory -> onCraftMatrixChanged());
            onCraftMatrixChanged();
        }

    // 转移物品
        @Override
        public ItemStack transferStackInSlot(EntityPlayer player, int index) {
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = this.inventorySlots.get(index);

            if (slot != null && slot.getHasStack()) {
                ItemStack itemstack1 = slot.getStack();
                itemstack = itemstack1.copy();

                // 处理输出槽转移 (index=3)
                if (index == 3) {
                    // 尝试转移物品到玩家物品栏
                    if (!this.mergeItemStack(itemstack1, 4, 40, true)) {
                        return ItemStack.EMPTY;
                    }
                }
                // 处理输入槽转移 (0,1,2)
                else if (index < 3) {
                    if (!this.mergeItemStack(itemstack1, 4, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } 
                // 处理玩家物品栏转移 (4-39)
                else {
                    // 尝试放入输入槽 (0=模板, 1=盔甲, 2=材料)
                    if (!this.mergeItemStack(itemstack1, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                // 清理空物品栈
                if (itemstack1.isEmpty()) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }
            }
            return itemstack;
        }

    // 配方检测
        public void onCraftMatrixChanged() {
            ItemStack templateItme = this.craftMatrix.getStackInSlot(0);
            ItemStack armor = this.craftMatrix.getStackInSlot(1);
            ItemStack materialItme = this.craftMatrix.getStackInSlot(2);

            String material = TrimData.getMaterial(materialItme);

            // 盔甲 及 盔甲 槽位必须有物品 || 非锻造材料 
            if (armor.isEmpty() || materialItme.isEmpty() || material == null) {
                this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
                return;
            }

            // 处理下界合金升级
            if (templateItme.isEmpty() && "netherite".equals(material) && isDiamondItem(armor)) {
                Item netheriteItme = NETHER_RECIPE.getOrDefault(armor.getItem(), null);
                if (netheriteItme == null) return;

                ItemStack result = new ItemStack(netheriteItme);
                // 保留 NBT
                if (armor.hasTagCompound()) {
                    result.setTagCompound(armor.getTagCompound().copy());
                }

                this.craftResult.setInventorySlotContents(0, result);
            }
            // 处理模板
            else if (!templateItme.isEmpty() && canAddPattern(armor)) {
                String template = ((ModItemSmithingTemplate) templateItme.getItem()).getPatternType();

                ItemStack result = armor.copy();
                // 保留 NBT
                NBTTagCompound tag = result.hasTagCompound() ? result.getTagCompound().copy() : new NBTTagCompound();
                tag.setString("Pattern", template);
                tag.setString("Material", material);
                result.setTagCompound(tag);

                this.craftResult.setInventorySlotContents(0, result);
            }
            // 无效配方
            else {
                this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
            }
        }

    // 检查
        public boolean isDiamondItem(ItemStack stack) {
            return NETHER_RECIPE.getOrDefault(stack.getItem(), null) != null;
        }
        public boolean canAddPattern(ItemStack stack) {
            // 必须是盔甲 && 没有纹饰 才能添加
            return stack.getItem() instanceof ItemArmor
                && (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Material"));
        }

    // 减少所有输入槽物品
        @Override
        public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
            if (slotId == 3) {
                // 禁止数字键切换 及 禁止鼠标有物品时操作
                if (clickType == ClickType.SWAP || !player.inventory.getItemStack().isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }

            ItemStack result = this.craftResult.getStackInSlot(0).copy();
            ItemStack stack = super.slotClick(slotId, dragType, clickType, player);

            if (slotId == 3) {
                if (!result.isEmpty()) {
                    this.addCraftPatterns();
                    this.shrinkMaterial();
                    Sound.playSound(this.player.world, this.player.getPosition(), "block.smithing_table");
                }
            }

            return stack;
        }

        // 添加到已制作列表
        public void addCraftPatterns() {
            Item item = this.craftMatrix.getStackInSlot(0).getItem();
            if (item instanceof ModItemSmithingTemplate) {
                this.craftPatterns.add(((ModItemSmithingTemplate) item).getPatternType());
            }
        }

        // 减少物品
        public void shrinkMaterial() {
            for (int i = 0; i < 3; i++) {
                Slot inputSlot = this.inventorySlots.get(i);
                if (inputSlot.getHasStack()) {
                    inputSlot.decrStackSize(1);
                }
            }
        }

    // 关闭容器
        @Override
        public void onContainerClosed(EntityPlayer player) {
            ItemStack template = this.craftMatrix.getStackInSlot(0);
            ItemStack armor = this.craftMatrix.getStackInSlot(1);
            ItemStack material = this.craftMatrix.getStackInSlot(2);

            ItemBase.givePlayerItem(player, false, template, armor, material);
            this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
            this.tryGiveAchievement();
        }

        public ItemStack getOutputStack() {
            return craftResult.getStackInSlot(0);
        }

        public void tryGiveAchievement() {
            if (!this.craftPatterns.isEmpty()) {
                ModAdvancements.grant(this.player, ModAdvancements.ARMOR_PATTERN);
                ModAdvancements.grantOnAllPattern(this.player, this.craftPatterns.toArray(new String[0]));
            }
        }

        @Override public boolean canInteractWith(EntityPlayer player) { return true; }
    }

// 下界合金配方
    private static final Map<Item, Item> NETHER_RECIPE = new HashMap<>();
    private static void addNetheriteRecipe() {
        if (Examine.FuturemcID || Examine.UNBID) {
            addNetherRecipe("diamond_sword", "netherite_sword");
            addNetherRecipe("diamond_pickaxe", "netherite_pickaxe");
            addNetherRecipe("diamond_axe", "netherite_axe");
            addNetherRecipe("diamond_shovel", "netherite_shovel");
            addNetherRecipe("diamond_hoe", "netherite_hoe");
            addNetherRecipe("diamond_helmet", "netherite_helmet");
            addNetherRecipe("diamond_chestplate", "netherite_chestplate");
            addNetherRecipe("diamond_leggings", "netherite_leggings");
            addNetherRecipe("diamond_boots", "netherite_boots");
        }
    }
    public static void addNetherRecipe(String a, String b) {
        NETHER_RECIPE.put(getItem(a), getNetItem(b));
    }
    private static Item getItem(String a) {
        return Item.getByNameOrId("minecraft:" + a);
    }
    private static Item getNetItem(String a) {
        return Item.getByNameOrId((Examine.FuturemcID ? "futuremc:" : "nb:") + a);
    }

    public static Map<Item, Item> getNetherRecipe() {
        return new HashMap<>(NETHER_RECIPE);
    }

// 物品提示
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (Examine.FuturemcID || Examine.UNBID) {
            tooltip.add(I18n.format("tooltip.suikecherry.smithing_table.netherite.up"));
        }
    }
}