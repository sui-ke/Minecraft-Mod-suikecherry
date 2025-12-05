package suike.suikecherry.asm.advice;

import java.util.Map;

import suike.suikecherry.inter.IMethod;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import com.google.common.collect.ImmutableMap;

public class RenderArmorLayerMethod extends MethodVisitor implements IMethod {
    private String type = "0"; // 无类型

    public RenderArmorLayerMethod(AdviceAdapterData data) {
        super(Opcodes.ASM5, data.mv);
    }

// 需要修改的方法
    @Override
    public Map<String, String[]> getMethods() {
        return ImmutableMap.<String, String[]>builder()
            .put("a", new String[] {"(Lvp;FFFFFFFLvl;)V", "render"})
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
        super.visitMethodInsn(opcode, owner, name, desc, itf);
        if (this.type.equals("render") && opcode == Opcodes.INVOKEVIRTUAL && "a".equals(name) && "(Lvg;FFFFFF)V".equals(desc)) {
            this.injectArmorTrimRender();
        }
    }

    private void injectArmorTrimRender() {
        // 加载方法参数和局部变量
        mv.visitVarInsn(Opcodes.ALOAD, 1);  // EntityLivingBase entity
        mv.visitVarInsn(Opcodes.FLOAD, 2);  // float limbSwing
        mv.visitVarInsn(Opcodes.FLOAD, 3);  // float limbSwingAmount
        mv.visitVarInsn(Opcodes.FLOAD, 4);  // float partialTicks
        mv.visitVarInsn(Opcodes.FLOAD, 5);  // float ageInTicks
        mv.visitVarInsn(Opcodes.FLOAD, 6);  // float netHeadYaw
        mv.visitVarInsn(Opcodes.FLOAD, 7);  // float headPitch
        mv.visitVarInsn(Opcodes.FLOAD, 8);  // float scale
        mv.visitVarInsn(Opcodes.ALOAD, 9);  // EntityEquipmentSlot slot
        mv.visitVarInsn(Opcodes.ALOAD, 10); // ItemStack stack
        mv.visitVarInsn(Opcodes.ALOAD, 12); // ModelBase model

        // 注入调用纹饰渲染方法
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "suike/suikecherry/client/render/armor/ArmorTrimRender",
            "renderArmorTrim",
            "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/model/ModelBase;)V",
            false
        );
    }
}