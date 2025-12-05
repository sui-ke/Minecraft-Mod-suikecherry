package suike.suikecherry.client.gui;

import java.util.Map;
import java.util.Deque;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.io.IOException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;

import suike.suikecherry.SuiKe;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.SignTextUpdatePacket;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity;
import suike.suikecherry.data.SignGuiTexturesData;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

public class GuiEditSign extends GuiScreen {
    private final int editingSlot;
    private final String titleText;
    private final HasBackSideSignTileEntity tile;
    private int currentTextField = 0; // 正在编辑的行
    private GuiTextField[] textFields = new GuiTextField[4];
    private Deque<String[]> undoStack = new ArrayDeque<>(); // 备份字符

    private long lastCheckTime = 0;
    private static final double MAX_DISTANCE_SQ = 5.0 * 5.0;

    // GUI 背景配置
    private final ResourceLocation BACKGROUND_TEXTURE;
    private static final float SCALE_FACTOR = 10.0F; // 缩放
    private static final int ORIGINAL_TEXTURE_SIZE = 14; // 14x14 背景

    // 布局参数
    private static final int BUTTON_Y_OFFSET = 45;  // 按钮距离屏幕底部偏移量
    private static final int BUTTON_WIDTH = 100;   // 按钮宽度
    private static final int BUTTON_HEIGHT = 20;   // 按钮高度

    // 文字
    private static final float CONTENT_OFFSET_FACTOR = 0.2F; // 内容区域占贴图高度的比例
    private static final int TEXT_COLOR = 0xFF000000;   // 文字颜色
    private static final int LINE_SPACING = 15;         // 行间距
    private static final int TEXT_FIELD_HEIGHT = 14;    // 输入框高度

    // 普通告示牌
    private static final int SIGN_BACKGROUND_WIDTH = 100;    // 普通告示牌输入框宽度
    private static final int SIGN_TITLE_Y_OFFSET = -85;      // 标题距离中心偏移（负值向上）
    private static final int SIGN_INPUT_FIELD_Y_OFFSET = -22; // 输入框相对贴图顶部的偏移量（负值向上）

    // 悬挂告示牌
    private static final int HANGING_SIGN_BACKGROUND_WIDTH = 100;    // 悬挂告示牌输入框宽度
    private static final int HANGING_SIGN_TITLE_Y_OFFSET = -100;     // 标题距离中心偏移（负值向上）
    private static final int HANGING_SIGN_INPUT_FIELD_Y_OFFSET = 32; // 输入框相对贴图顶部的偏移量（负值向上）

    // 输入框
    private final int maxTextWidth;         // 文字最大长度
    private final int BACKGROUND_WIDTH;     // 输入框宽度
    private final int INPUT_FIELD_Y_OFFSET; // 输入框相对贴图顶部的偏移量

    // 计算后的布局参数
    private int TITLE_Y_OFFSET; // 标题距离中心偏移（负值向上）
    private int scaledWidth;    // 缩放后贴图宽度
    private int scaledHeight;   // 缩放后贴图高度
    private int contentAreaY;   // 内容区域起始Y坐标
    private boolean needSave;   // 是否保存

    public GuiEditSign(HasBackSideSignTileEntity tile) {
        this.tile = tile;
        this.needSave = true;
        this.editingSlot = tile.getEditedSlot();

        // 获取背景贴图
        SignGuiTexturesData texturesData = tile.getEditedBlockType();
        this.BACKGROUND_TEXTURE = new ResourceLocation(SuiKe.MODID, "textures/gui/" + texturesData.getType() + "/" + texturesData.getTextures() + ".png");

        // 判断是是否是悬挂告示牌
        boolean isHangIng = "hanging_sign".equals(texturesData.getType());

        // 获取输入框位置
        this.INPUT_FIELD_Y_OFFSET =  isHangIng ? HANGING_SIGN_INPUT_FIELD_Y_OFFSET : SIGN_INPUT_FIELD_Y_OFFSET;

        // 获取输入框限制
        this.BACKGROUND_WIDTH =  isHangIng ? HANGING_SIGN_BACKGROUND_WIDTH : SIGN_BACKGROUND_WIDTH;
        this.maxTextWidth = this.BACKGROUND_WIDTH - 4 * 2;

        // 获取标题文字
        String slotDescription = this.editingSlot == 0 ? 
            I18n.format("suikecherry.sign.isFront") : 
            I18n.format("suikecherry.sign.isBack");
        this.titleText = I18n.format("sign.edit") + " - " + slotDescription;

        // 设置标题位置
        this.TITLE_Y_OFFSET = isHangIng ? HANGING_SIGN_TITLE_Y_OFFSET : SIGN_TITLE_Y_OFFSET;

        // 设置背景位置
        this.scaledWidth = (int) (ORIGINAL_TEXTURE_SIZE * SCALE_FACTOR);
        this.scaledHeight = (int) (ORIGINAL_TEXTURE_SIZE * SCALE_FACTOR);
    }

// 初始化 GUI
    @Override
    public void initGui() {
        super.initGui();
        ITextComponent[] text = this.tile.getTextForSlot(editingSlot);
        if (text == null) {
            text = new ITextComponent[4];
        }

        int bgY = (height - scaledHeight) / 2;
        this.contentAreaY = bgY + (int)(scaledHeight * CONTENT_OFFSET_FACTOR);

        int textWidth = this.BACKGROUND_WIDTH;
        int x = width/2 - textWidth/2; 

        for (int i = 0; i < 4; i++) {
            int yPos = contentAreaY + INPUT_FIELD_Y_OFFSET + i * LINE_SPACING;
            this.textFields[i] = new GuiTextField(i, fontRenderer, x, yPos, textWidth, TEXT_FIELD_HEIGHT);
            this.textFields[i].setEnableBackgroundDrawing(false);
            this.textFields[i].setTextColor(TEXT_COLOR);
            if (text[i] != null)
                this.textFields[i].setText(text[i].getUnformattedText());
            else
                this.textFields[i].setText("");
        }
        this.textFields[currentTextField].setFocused(true);

        int buttonY = height - BUTTON_Y_OFFSET - 8;
        int halfScreen = width / 2;

        this.buttonList.add(new GuiButton(0, halfScreen - BUTTON_WIDTH, buttonY, BUTTON_WIDTH - 1, BUTTON_HEIGHT, "×"));
        this.buttonList.add(new GuiButton(1, halfScreen + 2, buttonY, BUTTON_WIDTH - 1, BUTTON_HEIGHT, "↺"));
        this.buttonList.add(new GuiButton(2, halfScreen - BUTTON_WIDTH, buttonY + 23, BUTTON_WIDTH - 1, BUTTON_HEIGHT, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(3, halfScreen + 2, buttonY + 23, BUTTON_WIDTH - 1, BUTTON_HEIGHT, I18n.format("gui.cancel")));

        this.backupText();
    }

// 渲染
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        // 计算背景位置
        int bgX = (width - scaledWidth) / 2;
        int bgY = (height - scaledHeight) / 2;

        // 绘制贴图背景
        GlStateManager.pushMatrix();
        GlStateManager.translate(bgX, bgY, 0);
        GlStateManager.scale(SCALE_FACTOR, SCALE_FACTOR, 1.0f);
        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 
            ORIGINAL_TEXTURE_SIZE, ORIGINAL_TEXTURE_SIZE,
            ORIGINAL_TEXTURE_SIZE, ORIGINAL_TEXTURE_SIZE
        );
        GlStateManager.popMatrix();

        // 绘制标题
        drawCenteredString(fontRenderer, this.titleText, (width / 2), (height / 2 + TITLE_Y_OFFSET), 0xFFFFFF);

        // 绘制文本框内容
        for (GuiTextField field : textFields) {
            // 绘制文本
            String text = field.getText();
            int textWidth = fontRenderer.getStringWidth(text);
            int xPos = field.x + (field.width - textWidth)/2;
            int yPos = field.y + (field.height - 8)/2;
            fontRenderer.drawString(text, xPos, yPos, TEXT_COLOR);

            // 绘制光标
            if (field.isFocused()) {
                int cursorPos = Math.min(field.getCursorPosition(), text.length());
                int cursorX = xPos + fontRenderer.getStringWidth(text.substring(0, cursorPos));
                this.drawVerticalLine(cursorX, yPos, yPos + 8, TEXT_COLOR);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

// 处理文本框输入
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.needSave = false; // 不保存文本
            this.mc.displayGuiScreen(null); // 关闭GUI
        }

        super.keyTyped(typedChar, keyCode);

        GuiTextField currentField = textFields[currentTextField];
        // 处理删除操作（始终允许）
        if (keyCode == Keyboard.KEY_BACK) {
            this.backupText();
            currentField.textboxKeyTyped(typedChar, keyCode);
            return;
        }

        // 上下键切换输入框
        if (keyCode == Keyboard.KEY_UP) {
            currentTextField = (currentTextField - 1 + 4) % 4;
            updateFocus();
            return;
        } else if (keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_TAB) {
            currentTextField = (currentTextField + 1) % 4;
            updateFocus();
            return;
        }

        // 自动换行
        if (keyCode == Keyboard.KEY_RETURN && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            currentTextField = Math.min(currentTextField + 1, 3);
            updateFocus();
        }
        
        // 处理输入
        String originalText = currentField.getText();

        // 处理粘贴操作（Ctrl+V）
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.backupText();
            String clipboard = "";
            try {
                clipboard = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
            } catch (Exception ignored) {}

            // 计算可粘贴的文本长度
            String newText = truncateText(originalText + clipboard);
            currentField.setText(newText);
            return;
        }

        // 处理普通输入
        String testText = originalText + typedChar;
        if (handleSystemShortcuts(keyCode) || fontRenderer.getStringWidth(testText) <= maxTextWidth) {
            this.backupText();
            currentField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    // 文本粘贴截断方法
    private String truncateText(String input) {
        if (fontRenderer.getStringWidth(input) <= maxTextWidth) {
            return input;
        }

        // 从右向左查找合适位置
        int cutPosition = input.length();
        while (cutPosition > 0 && fontRenderer.getStringWidth(input.substring(0, cutPosition)) > maxTextWidth) {
            cutPosition--;
        }
        return input.substring(0, cutPosition);
    }

    private boolean handleSystemShortcuts(int keyCode) {
        boolean ctrlDown = GuiScreen.isCtrlKeyDown();
        
        if (ctrlDown) {
            switch(keyCode) {
                case Keyboard.KEY_A: // Ctrl+A
                    return true;
                case Keyboard.KEY_C: // Ctrl+C
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void updateFocus() {
        for (int i = 0; i < textFields.length; i++) {
            textFields[i].setFocused(i == currentTextField);
        }
        // 将光标移动到行末
        textFields[currentTextField].setCursorPositionEnd();
    }

// 激活点击的文本框
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean textFieldClicked = false;
    
        for (int i = 0; i < textFields.length; i++) {
            GuiTextField field = textFields[i];
            if (field.mouseClicked(mouseX, mouseY, mouseButton)) {
                currentTextField = i; // 关键：更新当前选中行索引
                textFieldClicked = true;
                break; // 找到点击的文本框后跳出循环
            }
        }
        
        // 统一更新焦点状态
        if (textFieldClicked) {
            for (int i = 0; i < textFields.length; i++) {
                textFields[i].setFocused(i == currentTextField);
            }
        }
    }

// 更新
    @Override
    public void updateScreen() {
        super.updateScreen();
        for (GuiTextField field : textFields) {
            field.updateCursorCounter();
        }

        // 每20 tick (1秒) 检查一次距离
        if (System.currentTimeMillis() - lastCheckTime > 1000) {
            EntityPlayer player = this.mc.player;

            double distSq = player.getDistanceSq(
                tile.getPos().getX() + 0.5, 
                tile.getPos().getY() + 0.5, 
                tile.getPos().getZ() + 0.5
            );

            if (distSq > MAX_DISTANCE_SQ) {
                this.mc.displayGuiScreen(null); // 关闭GUI
            }

            lastCheckTime = System.currentTimeMillis();
        }
    }

// 按钮点击处理
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            // 在清空前备份
            this.backupText();
            // 清空所有文本框
            for (GuiTextField field : textFields) {
                field.setText("");
            }
        }
        else if (button.id == 1 && !this.undoStack.isEmpty()) {
            // 撤回操作
            String[] prevState = this.undoStack.pop();
            for (int i = 0; i < textFields.length; i++) {
                textFields[i].setText(prevState[i]);
            }
        }
        else if (button.id == 2) {
            this.needSave = true; // 保存文本
            this.mc.displayGuiScreen(null); // 关闭GUI
        }
        else if (button.id == 3) {
            this.needSave = false; // 不保存文本
            this.mc.displayGuiScreen(null); // 关闭GUI
        }
    }

    private void backupText() {
        // 在添加新状态前检查栈容量
        if (this.undoStack.size() >= 500) {
            this.undoStack.removeLast(); // 移除最旧的（栈底元素）
        }

        // 添加备份
        this.undoStack.push(Arrays.stream(textFields)
            .map(f -> f.getText())
            .toArray(String[]::new));
    }

// 关闭 GUI
    @Override
    public void onGuiClosed() {
        ITextComponent[] newText = new ITextComponent[4];
        if (this.needSave) {
            for (int i = 0; i < 4; i++) {
                String input = textFields[i].getText();
                newText[i] = input.isEmpty() ? new TextComponentString("") : new TextComponentString(input);
            }
            // 更新本地文本
            tile.updateText(editingSlot, newText, false);
            // 尝试发送更新包到多人游戏服务器服务端
            PacketHandler.INSTANCE.sendToServer(
                new SignTextUpdatePacket(this.tile.getPos(), editingSlot, newText, false)
            );
            return;
        }

        // 解锁告示牌
        tile.updateText(-1, newText, false);
        // 尝试发送更新包到多人游戏服务器服务端 - 解锁告示牌
        PacketHandler.INSTANCE.sendToServer(
            new SignTextUpdatePacket(this.tile.getPos(), -1, newText, false)
        );
    }
}