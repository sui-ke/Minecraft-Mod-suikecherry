package suike.suikecherry.asm.advice;

import java.util.Map;

import suike.suikecherry.inter.IMethod;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import com.google.common.collect.ImmutableMap;

public class LanternBlockMethod extends AdviceAdapter implements IMethod {
    private String type = "0"; // 无类型

    public LanternBlockMethod(AdviceAdapterData data) {
        super(Opcodes.ASM5, data.mv, data.access, data.name, data.desc);
    }

// 需要修改的方法
    @Override
    public Map<String, String[]> getMethods() {
        return ImmutableMap.<String, String[]>builder()
            .put("isValidPos", new String[] {"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", "validPos"})
            .build();
    }

    @Override
    public MethodVisitor setMethodType(String type) {
        this.type = type;
        return this;
    }

// 在方法头部注入
    @Override
    public void onMethodEnter() {
        if (this.type.equals("validPos")) {
            this.isValidPos();
        }
    }

    private void isValidPos() {
        mv.visitVarInsn(ALOAD, 1); // World
        mv.visitVarInsn(ALOAD, 2); // BlockPos
        mv.visitVarInsn(ALOAD, 3); // EnumFacing

        // 调用静态方法
        mv.visitMethodInsn(
            INVOKESTATIC, 
            "suike/suikecherry/expand/futuremc/Lantern", 
            "canPlaceLantern", 
            "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Ljava/lang/Boolean;", 
            false
        );

        // 处理返回值
        Label skipReturn = new Label();
        mv.visitInsn(DUP);
        mv.visitJumpInsn(IFNULL, skipReturn);
        mv.visitMethodInsn(
            INVOKEVIRTUAL, 
            "java/lang/Boolean", 
            "booleanValue", 
            "()Z", 
            false
        );
        mv.visitInsn(IRETURN);

        // 如果返回 null 则继续执行原方法
        mv.visitLabel(skipReturn);
        mv.visitInsn(POP);
    }
}