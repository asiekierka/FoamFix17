/*
 * Copyright (c) 2015 Vincent Lee
 * Copyright (c) 2020, 2021 Adrian "asie" Siekierka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pl.asie.foamfix.bugfixmod.coremod.patchers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import pl.asie.foamfix.bugfixmod.coremod.BugfixModClassTransformer;

import java.util.Iterator;

/**
 * Created by Vincent on 6/6/2014.
 */
public abstract class AbstractPatcher {
    public String patcherName;
    protected String targetClassName;
    protected String targetMethodName;
    protected String targetMethodDesc;
    protected boolean successful;

    public AbstractPatcher(String name, String targetClassName, String targetMethodName, String targetMethodDesc) {
        this.patcherName = name;
        this.targetClassName = targetClassName.replace('/', '.');
        this.targetMethodName = targetMethodName;
        this.targetMethodDesc = targetMethodDesc;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    protected void patchMethodNode(MethodNode method) {
        if (this instanceof GlobalModificationPatcher) {
            ((GlobalModificationPatcher) this).modifyInsnsGlobal(method.instructions);
        } else {
            AbstractInsnNode currentInstruction;
            Iterator<AbstractInsnNode> instructionSet = method.instructions.iterator();
            while (instructionSet.hasNext()) {
                currentInstruction = instructionSet.next();
                if (this instanceof ModificationPatcher) {
                    ((ModificationPatcher) this).modifyInsns(currentInstruction, instructionSet, method.instructions);
                } else {
                    InsnList toInject = buildNewInsns(currentInstruction, instructionSet);
                    if (toInject != null && toInject.size() > 0) {
                        method.instructions.insert(currentInstruction, toInject);
                    }
                }
            }
        }
    }

    protected void patchClassNode(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals(targetMethodName) && (targetMethodDesc == null || method.desc.equals(targetMethodDesc))) {
                printMessage("Found target method");
                patchMethodNode(method);
            }
        }
    }

    public byte[] patch(String transformedName, byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = null;
        try {
            classReader = new ClassReader(bytes);
        } catch (NullPointerException e) { // Thrown when LiteLoader classes are read.
            // Silently ignore.
            return bytes;
        }
        classReader.accept(classNode, 0);
        successful = false;

        patchClassNode(classNode);

        if (!successful) {
            if (!targetClassName.isEmpty()) printMessage("Failed to apply transform!");
            return bytes;
        } else {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            if (!targetClassName.isEmpty()) printMessage(successful ? "Applied transform!" : "Failed to apply transform!");
            return writer.toByteArray();
        }
    }

    public abstract InsnList buildNewInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet);

    public String getPatcherName() {
        return patcherName;
    }

    public void printMessage(String message) {
        BugfixModClassTransformer.instance.logger.info("[" + getPatcherName() + "] " + message);
    }
}
