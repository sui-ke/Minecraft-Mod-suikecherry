package suike.suikecherry.client.render.item;

import java.util.List;
import java.util.Random;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ModItemPotterySherd;
import suike.suikecherry.tileentity.DecoratedPotTileEntity;
import suike.suikecherry.client.render.tileentity.TileEntityRender;
import suike.suikecherry.client.render.tileentity.DecoratedPotTileEntityRender;
import suike.suikecherry.client.render.tileentity.DecoratedPotTileEntityRender.ModelDecoratedPot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import net.minecraftforge.common.util.Constants;

public class DecoratedPotItemRender {
    // /give @s suikecherry:decorated_pot 1 0 {RandomSherdDisplay:1b}
    private static final long UPDATE_INTERVAL = 1400;
    private static final Random RANDOM = new Random();
    private static long lastUpdateTime = 0;
    private static String[] currentRandomSherds = null;

    private static final ModelDecoratedPot model = new ModelDecoratedPot(EnumFacing.SOUTH);

    static void renderItem(ItemStack stack) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        try {
            if (Minecraft.getMinecraft().currentScreen == null) {
                GlStateManager.enableCull();
            }
            GlStateManager.scale(1.13636F, -1.13636F, -1.13636F);
            GlStateManager.translate(0.0F, 0.55F, 0.0F);
            // 设置纹饰
            model.currentSherdIDs = getSherdIDs(stack);
            // 绑定基础纹理并渲染
            Minecraft.getMinecraft().getTextureManager().bindTexture(DecoratedPotTileEntityRender.BASE_TEXTURE);
            model.root.render(0.0625F);
        } finally {
            GlStateManager.disableCull();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }

    private static String[] getSherdIDs(ItemStack stack) {
        if (!stack.hasTagCompound()) return DecoratedPotTileEntity.getNullSherdIDs();

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) DecoratedPotTileEntity.getNullSherdIDs();

        if (nbt.getBoolean("RandomSherdDisplay")) {
            return getRandomSherdIDs();
        }

        if (nbt.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
            nbt = nbt.getCompoundTag("BlockEntityTag");
        }

        if (nbt.hasKey("Sherds", Constants.NBT.TAG_LIST)) {
            NBTTagList list = nbt.getTagList("Sherds", Constants.NBT.TAG_STRING);
            if (list.tagCount() == 4) {
                String[] sherdIDs = new String[4];
                for (int i = 0; i < 4; i++) {
                    sherdIDs[i] = list.getStringTagAt(i);
                }
                return sherdIDs;
            }
        }

        return DecoratedPotTileEntity.getNullSherdIDs();
    }

    private static String[] getRandomSherdIDs() {
        long currentTime = System.currentTimeMillis();

        // 检查是否需要更新纹样
        if (currentRandomSherds == null || currentTime - lastUpdateTime > UPDATE_INTERVAL) {
            String[] allSherds = ModItemPotterySherd.getDefaultPotteryTypes();
            currentRandomSherds = new String[4];

            // 随机选择4个纹样
            for (int i = 0; i < 4; i++) {
                currentRandomSherds[i] = allSherds[RANDOM.nextInt(allSherds.length)];
            }

            lastUpdateTime = currentTime;
        }
        return currentRandomSherds;
    }
}