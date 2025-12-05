package suike.suikecherry.inter;

import java.util.Map;

import org.objectweb.asm.*;

import com.google.common.collect.ImmutableMap;

public interface IMethod {

    default Map<String, String[]> getMethods() {
        return ImmutableMap.<String, String[]>builder().build();
    }

    MethodVisitor setMethodType(String type);

    default String[] getInterfaces(String[] interfaces) {
        return interfaces;
    }

    default void addValue(ClassVisitor visitor) {}
}