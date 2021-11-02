/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.ClassVisitor
 *  org.objectweb.asm.ClassWriter
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.FieldInsnNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.LdcInsnNode
 *  org.objectweb.asm.tree.MethodInsnNode
 *  org.objectweb.asm.tree.MethodNode
 */
package tech.mmmax.loader.vm.antidump;

import java.util.ArrayList;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class DummyClass {
    static InsnList getDummyCode() {
        InsnList list = new InsnList();
        list.add((AbstractInsnNode)new FieldInsnNode(178, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        list.add((AbstractInsnNode)new LdcInsnNode((Object)"u dum asf"));
        list.add((AbstractInsnNode)new MethodInsnNode(182, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        list.add((AbstractInsnNode)new LdcInsnNode((Object)1));
        list.add((AbstractInsnNode)new MethodInsnNode(184, "java/lang/System", "exit", "(I)V"));
        return list;
    }

    public static byte[] makeDummyClass(String className) {
        ClassNode classNode = new ClassNode();
        classNode.name = className.replace(".", "/");
        classNode.access = 1;
        classNode.version = 52;
        classNode.superName = "java/lang/Object";
        InsnList dummyCode = DummyClass.getDummyCode();
        ArrayList<MethodNode> methods = new ArrayList<MethodNode>();
        MethodNode node = new MethodNode(1, "<init>", "()V", null, null);
        node.instructions = dummyCode;
        methods.add(node);
        classNode.methods = methods;
        ClassWriter writer = new ClassWriter(2);
        classNode.accept((ClassVisitor)writer);
        return writer.toByteArray();
    }
}

