/*/
package suike.suikecherry.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;

public class LayerArmorBaseASM implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.contains("net.minecraft.client.renderer.entity.layers.LayerBipedArmor")) {
            System.out.println("[ASM] 正在转换 LayerBipedArmor");
            ClassReader cr = new ClassReader(basicClass);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new LayerArmorBaseVisitor(cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
        return basicClass;
    }

    static class LayerArmorBaseVisitor extends ClassVisitor {
        LayerArmorBaseVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if ("renderArmorLayer".equals(name) && 
                "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V".equals(desc)) {
                return new RenderArmorLayerMethodVisitor(mv);
            }
            return mv;
        }
    }

    static class RenderArmorLayerMethodVisitor extends MethodVisitor {
        RenderArmorLayerMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            // 检测到 ModelBase.render() 调用
            if (opcode == Opcodes.INVOKEVIRTUAL &&
                "net/minecraft/client/model/ModelBase".equals(owner) &&
                "render".equals(name) &&
                "(Lnet/minecraft/entity/Entity;FFFFFF)V".equals(desc)) {

                // 在调用后注入装饰渲染
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                injectArmorTrimRender();
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

        private void injectArmorTrimRender() {
            // 加载方法参数和局部变量
            mv.visitVarInsn(Opcodes.ALOAD, 1);    // EntityLivingBase entity
            mv.visitVarInsn(Opcodes.FLOAD, 2);    // float limbSwing
            mv.visitVarInsn(Opcodes.FLOAD, 3);    // float limbSwingAmount
            mv.visitVarInsn(Opcodes.FLOAD, 5);    // float ageInTicks (跳过 partialTicks)
            mv.visitVarInsn(Opcodes.FLOAD, 6);    // float netHeadYaw
            mv.visitVarInsn(Opcodes.FLOAD, 7);    // float headPitch
            mv.visitVarInsn(Opcodes.FLOAD, 8);    // float scale
            mv.visitVarInsn(Opcodes.ALOAD, 9);    // EntityEquipmentSlot slot
            mv.visitVarInsn(Opcodes.ALOAD, 10);   // ItemStack stack
            mv.visitVarInsn(Opcodes.ALOAD, 12);   // ModelBase model

            // 调用纹饰渲染方法
            System.out.println("尝试调用纹饰渲染方法");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                "suike/suikecherry/client/render/trim/ArmorTrimRender",
                "renderArmorTrim",
                "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/model/ModelBase;)V",
                false
            );
        }
    }
}//*/