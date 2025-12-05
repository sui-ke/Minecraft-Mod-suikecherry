package suike.suikecherry.asm.advice;

import java.util.Map;

import suike.suikecherry.inter.IMethod;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import com.google.common.collect.ImmutableMap;

public class RenderItemMethod extends MethodVisitor implements IMethod {
    private String type = "0"; // 无类型

    public RenderItemMethod(AdviceAdapterData data) {
        super(Opcodes.ASM5, data.mv);
    }

// 需要修改的方法
    @Override
    public Map<String, String[]> getMethods() {
        return ImmutableMap.<String, String[]>builder()
            .put("a", new String[] {"(Laip;Lcfy;)V", "render"})
            .build();
    }

    @Override
    public MethodVisitor setMethodType(String type) {
        this.type = type;
        return this;
    }

// 在方法返回前注入
    @Override
    public void visitInsn(int opcode) {
        if (this.type.equals("render") && opcode == Opcodes.RETURN) {
            this.injectItmeRender();
        }
        super.visitInsn(opcode);
    }

    private void injectItmeRender() {
        mv.visitVarInsn(Opcodes.ALOAD, 1); // ItemStack stack

        // 注入调用物品渲染方法
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "suike/suikecherry/client/render/item/ItemRender",
            "render",
            "(Lnet/minecraft/item/ItemStack;)V",
            false
        );
    }
}