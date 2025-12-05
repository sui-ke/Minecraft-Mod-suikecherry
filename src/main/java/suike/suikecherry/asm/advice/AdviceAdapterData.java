package suike.suikecherry.asm.advice;

import org.objectweb.asm.*;

public class AdviceAdapterData {
    public final MethodVisitor mv;
    public final int access;
    public final String name;
    public final String desc;

    public AdviceAdapterData() {
        this(new EmptyMethodVisitor(), 0, "<unknown>", "()V");
    }
    public AdviceAdapterData(MethodVisitor mv, int access, String name, String desc) {
        this.mv = mv;
        this.access = access;
        this.name = name;
        this.desc = desc;
    }

    static class EmptyMethodVisitor extends MethodVisitor {
        public EmptyMethodVisitor() {
            super(Opcodes.ASM5);
        }
    }
}