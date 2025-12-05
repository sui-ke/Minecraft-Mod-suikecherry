package suike.suikecherry.asm;

import java.util.Map;
import java.nio.file.*;

import suike.suikecherry.inter.IMethod;
import suike.suikecherry.asm.advice.AdviceAdapterData;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;

public class ClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.startsWith("suike.")) return basicClass;

        ASMData data = ASMData.getDataByName(transformedName);
        if (data == null) return basicClass;

        ClassReader cr = new ClassReader(basicClass);
        ClassWriter cw = new ClassWriter(data.classWriterType);
        ClassVisitor cv = new Visitor(cw, data);
        cr.accept(cv, data.classReaderType);

        // if (transformedName.startsWith("net.minecraft")) toFile(cw, getName(transformedName));

        return cw.toByteArray();
    }

    public static class Visitor extends ClassVisitor {
        public final ASMData data;
        public final IMethod iMethod;
        public final Map<String, String[]> METHOD_DESC;

        public Visitor(ClassVisitor cv, ASMData data) {
            super(Opcodes.ASM5, cv);
            this.data = data;
            this.iMethod = (IMethod) data.createMethod.apply(new AdviceAdapterData());
            this.METHOD_DESC = this.iMethod.getMethods();
        }

        // 添加接口
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, this.iMethod.getInterfaces(interfaces));
        }

        // 寻找注入的方法
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

            String[] transformedDesc = this.METHOD_DESC.get(name);
            if (transformedDesc != null && transformedDesc[0].equals(desc)) {
                IMethod iMethod = this.data.createMethod.apply(new AdviceAdapterData(mv, access, name, desc));
                return iMethod.setMethodType(transformedDesc[1]);
            }

            return mv;
        }

        // 添加变量, 方法
        @Override
        public void visitEnd() {
            super.visitEnd();
            this.iMethod.addValue(this);
        }
    }

/*
    public static void toFile(ClassWriter cw, String name) {
        try {
            String outputPath = "F:/Minecraft/mods/MC-code/.ASMClass/" + name + ".class";
            Files.createDirectories(Paths.get(outputPath).getParent());
            Files.write(Paths.get(outputPath), cw.toByteArray());
            // System.out.println("按任意键继续...");
            // System.in.read();
        } catch (Exception e) {}
    }

    public static String getName(String transformed) {
        for (int i = transformed.length() - 1; i >= 0; i--) {
            if (transformed.charAt(i) == '.') {
                return transformed.substring(i + 1);
            }
        }
        return transformed;
    }//*/
}