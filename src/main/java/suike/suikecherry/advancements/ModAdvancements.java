package suike.suikecherry.achievement;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.block.BlockBase;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import net.minecraftforge.fml.common.FMLCommonHandler;

import com.google.common.collect.ImmutableMap;

public enum ModAdvancements {
    SALVAGE_SHERD("salvage_sherd", ItemBase.BRUSH, "adventure/root", "adventure.salvage_sherd", FrameType.TASK, 0.0f, 4.5f),
    SNIFFER_EGG("sniffer_egg", BlockBase.SNIFFER_EGG, "husbandry/root", "husbandry.obtain_sniffer_egg", FrameType.TASK, 0.0f, 2.0f),
    CRAFT_BUILDING_POT("craft_decorated_pot", BlockBase.DECORATED_POT, SALVAGE_SHERD, "adventure.craft_decorated_pot_using_only_sherds", FrameType.TASK, -1.0f, 4.5f),
    CHISELED_BOOKSHELF("read_power_from_chiseled_bookshelf", BlockBase.CHISELED_BOOKSHELF, "adventure/root", "adventure.read_power_from_chiseled_bookshelf", FrameType.TASK, 0.0f, 3.5f);

    private final String id;                       // 成就 id
    private final ItemStack icon;                  // 成就图片
    private final String typeAndName;              // 成就图片
    private final FrameType frameType;             // 成就等级
    private final ResourceLocation advancementLoc; // 成就 Loc
    private final ResourceLocation parentLoc;      // 父成就 Loc
    private Advancement advancement;               // 成就
    private ModAdvancements parentAdvancement;     // 父成就枚举
    private final float x;
    private final float y;

    private ModAdvancements(String id, Item item, Object parent, String typeAndName, FrameType frameType, float x, float y) {
        this(id, new ItemStack(item), parent, typeAndName, frameType, x, y);
    }
    private ModAdvancements(String id, Block block, Object parent, String typeAndName, FrameType frameType, float x, float y) {
        this(id, new ItemStack(block), parent, typeAndName, frameType, x, y);
    }
    private ModAdvancements(String id, ItemStack icon, Object parent, String typeAndName, FrameType frameType, float x, float y) {
        this.id = id;
        this.icon = icon;
        this.frameType = frameType;
        this.typeAndName = typeAndName;
        this.advancementLoc = new ResourceLocation(SuiKe.MODID, id);
        this.x = x;
        this.y = y;

        if (parent instanceof ModAdvancements) {
            this.parentAdvancement = (ModAdvancements) parent;
            this.parentLoc = ((ModAdvancements) parent).advancementLoc;
        } else {
            this.parentLoc = new ResourceLocation((String) parent);
        }
    }

    private Advancement createAdvancement(ItemStack icon, String typeAndName, FrameType frameType) {
        // 创建显示信息
        DisplayInfo display = new DisplayInfo(
            icon,
            new TextComponentTranslation("advancements." + typeAndName + ".title"),
            new TextComponentTranslation("advancements." + typeAndName + ".description"),
            new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/husbandry.png"),
            frameType,
            true,  // 显示提示
            true,  // 公告到聊天
            false  // 初始隐藏进度
        );

        display.setPosition(this.x, this.y);

        // 父进度
        Advancement parent = this.parentLoc.toString().startsWith("suike") 
            ? this.parentAdvancement.advancement
            : FMLCommonHandler.instance().getMinecraftServerInstance().getAdvancementManager().getAdvancement(
                this.parentLoc
            );

        Map<String, Criterion> criteria = new HashMap<>();
        criteria.put("manual", new Criterion(new ImpossibleTrigger.Instance()));
        String[][] requirements = {{"manual"}};

        // 创建成就对象
        return new Advancement(
            this.advancementLoc,
            parent,
            display,
            AdvancementRewards.EMPTY,
            ImmutableMap.copyOf(criteria),
            requirements
        );
    }

    private Advancement getAdvancement() {
        this.advancement = createAdvancement(this.icon, this.typeAndName, this.frameType);
        return this.advancement;
    }

    public static void registerAll(AdvancementManager manager) {
        Map<ResourceLocation, Advancement> modAdvancementMap = new HashMap<>();
        for (ModAdvancements adv : ModAdvancements.values()) {
            modAdvancementMap.put(adv.advancementLoc, adv.getAdvancement());
        }

        try {
            // 通过反射获取 AdvancementList
            Field advancementListField = AdvancementManager.class.getDeclaredField("field_192784_c");
            advancementListField.setAccessible(true);
            AdvancementList advancementList = (AdvancementList) advancementListField.get(manager);

            // 通过反射获取 advancementsMap
            Field advancementsMapField = AdvancementList.class.getDeclaredField("field_192092_b");
            advancementsMapField.setAccessible(true);
            Map<ResourceLocation, Advancement> advancementsMap = (Map<ResourceLocation, Advancement>) advancementsMapField.get(advancementList);

            // 注入自定义进度
            advancementsMap.putAll(modAdvancementMap);

            Field nonRootAdvancementsField = AdvancementList.class.getDeclaredField("field_192094_d");
            nonRootAdvancementsField.setAccessible(true);
            Set<Advancement> nonRootAdvancements = (Set<Advancement>) nonRootAdvancementsField.get(advancementList);

            for (Advancement advancement : modAdvancementMap.values()) {
                nonRootAdvancements.add(advancement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void grant(EntityPlayer player, ModAdvancements adv) {
        if (player == null || player.world.isRemote) return;

        Advancement advancement = player.getServer().getAdvancementManager().getAdvancement(
            adv.advancementLoc
        );

        if (advancement != null && player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).getAdvancements().grantCriterion(advancement, "manual");
        }
    }
}