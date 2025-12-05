package suike.suikecherry.asm.advice;

import java.util.Map;

import suike.suikecherry.inter.IMethod;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import com.google.common.collect.ImmutableMap;

public class ProjectileMethod extends MethodVisitor implements IMethod {
    private String type = "0"; // 无类型

    public ProjectileMethod(AdviceAdapterData data) {
        super(Opcodes.ASM5, data.mv);
    }

// 需要修改的方法 IMethod
    @Override
    public Map<String, String[]> getMethods() {
        return ImmutableMap.<String, String[]>builder()
            .put("a", new String[] {"(Lbhc;)V", "onImpact"})
            .build();
    }

    @Override
    public MethodVisitor setMethodType(String type) {
        this.type = type;
        return this;
    }

// 在方法中注入
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (this.type.equals("onImpact") && opcode == Opcodes.INVOKEVIRTUAL && "X".equals(name) && "()V".equals(desc)) {
            this.injectCollidedBlock();
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    private void injectCollidedBlock() {
        mv.visitVarInsn(Opcodes.ALOAD, 0); // this (EntityEgg)
        mv.visitVarInsn(Opcodes.ALOAD, 1); // RayTraceResult result

        // 注入方法
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "suike/suikecherry/block/ModBlockDecoratedPot",
            "tryBreakPot",
            "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/RayTraceResult;)V",
            false
        );
    }
}