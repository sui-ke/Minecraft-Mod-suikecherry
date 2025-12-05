package suike.suikecherry.achievement;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import suike.suikecherry.SuiKe;
import suike.suikecherry.data.TrimData;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.item.ModItemSmithingTemplate;
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
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.common.FMLCommonHandler;

import com.google.common.collect.ImmutableMap;

public enum ModAdvancements {
    SALVAGE_SHERD("salvage_sherd", ItemBase.BRUSH, "adventure/root", "adventure.salvage_sherd", FrameType.TASK),
    SNIFFER_EGG("sniffer_egg", BlockBase.SNIFFER_EGG, "husbandry/root", "husbandry.obtain_sniffer_egg", FrameType.TASK),
    CRAFT_BUILDING_POT("craft_decorated_pot", BlockBase.DECORATED_POT.createRandomSherdPot(), SALVAGE_SHERD, "adventure.craft_decorated_pot_using_only_sherds", FrameType.TASK, 0),
    CHISELED_BOOKSHELF("read_power_from_chiseled_bookshelf", BlockBase.CHISELED_BOOKSHELF, "adventure/root", "adventure.read_power_from_chiseled_bookshelf", FrameType.TASK),
    ARMOR_PATTERN("trim_with_any_armor_pattern", ItemBase.DUNE_ATST, "adventure/root", "adventure.trim_with_any_armor_pattern", FrameType.TASK),
    ALL_ARMOR_PATTERN("trim_with_all_exclusive_armor_patterns", ItemBase.SILENCE_ATST, ARMOR_PATTERN, "adventure.trim_with_all_exclusive_armor_patterns", FrameType.CHALLENGE, 200);

    private final String id;                       // 成就 id
    private final ItemStack icon;                  // 成就图片
    private final String typeAndName;              // 成就名
    private final FrameType frameType;             // 成就等级
    private final int xpReward;                    // 经验奖励
    private final ResourceLocation advancementRes; // 成就 Res
    private final ResourceLocation parentRes;      // 父成就 Res
    private Advancement advancement;               // 成就
    private ModAdvancements parentAdvancement;     // 父成就枚举

    private ModAdvancements(String id, Item item, Object parent, String typeAndName, FrameType frameType) {
        this(id, new ItemStack(item), parent, typeAndName, frameType, 0);
    }
    private ModAdvancements(String id, Block block, Object parent, String typeAndName, FrameType frameType) {
        this(id, new ItemStack(block), parent, typeAndName, frameType, 0);
    }
    private ModAdvancements(String id, Item item, Object parent, String typeAndName, FrameType frameType, int xpReward) {
        this(id, new ItemStack(item), parent, typeAndName, frameType, xpReward);
    }
    private ModAdvancements(String id, Block block, Object parent, String typeAndName, FrameType frameType, int xpReward) {
        this(id, new ItemStack(block), parent, typeAndName, frameType, xpReward);
    }
    private ModAdvancements(String id, ItemStack icon, Object parent, String typeAndName, FrameType frameType, int xpReward) {
        this.id = id;
        this.icon = icon;
        this.frameType = frameType;
        this.xpReward = xpReward;
        this.typeAndName = typeAndName;
        this.advancementRes = new ResourceLocation(SuiKe.MODID, id);

        if (parent instanceof ModAdvancements) {
            this.parentAdvancement = (ModAdvancements) parent;
            this.parentRes = ((ModAdvancements) parent).advancementRes;
        } else {
            this.parentRes = new ResourceLocation((String) parent);
        }
    }

// 创建成就
    private Advancement createAdvancement(AdvancementManager manager) {
        // 创建显示信息
        DisplayInfo display = new DisplayInfo(
            this.icon,
            new TextComponentTranslation("advancements." + this.typeAndName + ".title"),
            new TextComponentTranslation("advancements." + this.typeAndName + ".description"),
            new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/husbandry.png"),
            this.frameType,
            true,  // 显示提示
            true,  // 公告到聊天
            false
        );

        // 父进度
        Advancement parent = this.parentRes.toString().startsWith("suike") 
            ? this.parentAdvancement.advancement
            : manager.getAdvancement(this.parentRes);

        Map<String, Criterion> criteria = ImmutableMap.of(
            "manual", new Criterion(new ImpossibleTrigger.Instance())
        );
        String[][] requirements = {{"manual"}};

        // 创建奖励对象
        AdvancementRewards rewards = this.xpReward > 0 
            ? new AdvancementRewards(this.xpReward, new ResourceLocation[0], new ResourceLocation[0], null)
            : AdvancementRewards.EMPTY;

        // 创建成就对象
        this.advancement = new Advancement(
            this.advancementRes,
            parent,
            display,
            rewards,
            ImmutableMap.copyOf(criteria),
            requirements
        );
        return this.advancement;
    }

// 注册所有成就
    public static void registerAll(AdvancementManager manager) {
        Map<ResourceLocation, Advancement> modAdvancementMap = new HashMap<>();
        for (ModAdvancements adv : ModAdvancements.values()) {
            modAdvancementMap.put(adv.advancementRes, adv.createAdvancement(manager));
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

            // 注入非根进度列表
            Field nonRootAdvancementsField = AdvancementList.class.getDeclaredField("field_192094_d");
            nonRootAdvancementsField.setAccessible(true);
            Set<Advancement> nonRootAdvancements = (Set<Advancement>) nonRootAdvancementsField.get(advancementList);
            for (Advancement advancement : modAdvancementMap.values()) {
                nonRootAdvancements.add(advancement);
            }

            // 获取根进度集合
            Field rootAdvancementsField = AdvancementList.class.getDeclaredField("field_192093_c");
            rootAdvancementsField.setAccessible(true);
            Set<Advancement> rootAdvancements = (Set<Advancement>) rootAdvancementsField.get(advancementList);

            // 进度树重布
            Method layoutMethod = AdvancementTreeNode.class.getDeclaredMethod("func_192323_a", Advancement.class);
            layoutMethod.setAccessible(true);
            for (Advancement root : rootAdvancements) {
                for (ModAdvancements adv : ModAdvancements.values()) {
                    // 仅重布添加过的根进度
                    if (root.equals(manager.getAdvancement(adv.parentRes))) {
                        layoutMethod.invoke(null, root);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// 给玩家成就
    public static void grant(EntityPlayer player, ModAdvancements adv) {
        if (adv == ALL_ARMOR_PATTERN) return;
        grantAdv(player, adv);
    }

    private static void grantAdv(EntityPlayer player, ModAdvancements adv) {
        if (player == null || player.world == null || player.world.isRemote) return;

        AdvancementManager manager = player.getServer().getAdvancementManager();
        if (manager == null) return;

        Advancement advancement = manager.getAdvancement(
            adv.advancementRes
        );

        if (advancement != null && player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).getAdvancements().grantCriterion(advancement, "manual");
        }
    }

// 给玩家纹饰成就
    public static void grantOnAllPattern(EntityPlayer player, String... patternTypes) {
        if (player == null || player.world == null || player.world.isRemote) return;

        // 是否已完成成就
        if (hasPlayerAdvancement(player, ALL_ARMOR_PATTERN.advancement)) return;

        List<String> allPatternType = ModItemSmithingTemplate.getAllPatternType();

        for (String patternType : patternTypes) {
            if (allPatternType.contains(patternType)) { // 确保有效
                TrimData.addPatternTypeToPlayer(player, patternType); // 添加到玩家 nbt
            }
        }

        Set<String> playerPatterns = TrimData.getPlayerPatternTypes(player);
        if (playerPatterns.containsAll(allPatternType)) {
            grantAdv(player, ALL_ARMOR_PATTERN);
        }
    }

// 是否已完成成就
    private static boolean hasPlayerAdvancement(EntityPlayer player, Advancement advancement) {
        AdvancementProgress progress = ((EntityPlayerMP) player).getAdvancements().getProgress(advancement);
        return progress != null && progress.isDone();
    }
}