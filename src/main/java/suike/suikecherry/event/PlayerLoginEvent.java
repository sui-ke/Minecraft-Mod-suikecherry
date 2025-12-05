package suike.suikecherry.event;

import java.util.*;

import suike.suikecherry.SuiKe;
import suike.suikecherry.data.TreasureData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

// 玩家登录事件
@Mod.EventBusSubscriber(Side.CLIENT)
public class PlayerLoginEvent {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        Set<String> invalidItems = TreasureData.getInvalidItems();
        String warning = TextFormatting.RED + "[Cherry] ";

        if (!invalidItems.isEmpty()) {
            // 发送警告消息给玩家
            player.sendMessage(new TextComponentString(
                warning + getWarning(invalidItems.size())
            ));

            for (String item : invalidItems) {
                player.sendMessage(new TextComponentString(
                    warning + item
                ));
            }

            TreasureData.clearInvalidItems();
        }
    }

    private static String getWarning(int size) {
        if (SuiKe.isZhCn) {
            return "[警告] 检测到 " + size + " 个无效的物品配置!";
        }
        return "[WARNING] " + size + " invalid item config were detected!";
    }
}