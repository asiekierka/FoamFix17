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

package pl.asie.foamfix.coremod.patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import pl.asie.foamfix.FoamFixMod;
import pl.asie.foamfix.bugfixmod.coremod.patchers.AbstractPatcher;
import pl.asie.foamfix.bugfixmod.coremod.patchers.ModificationPatcher;
import pl.asie.foamfix.coremod.util.ClassSplicingUtil;

import java.io.File;
import java.util.Iterator;

public class FileRCEPatcher extends AbstractPatcher implements ModificationPatcher {
    public FileRCEPatcher(String name, String targetClassName) {
        super(name, targetClassName, null, null);
    }

    @Override
    public InsnList buildNewInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet) {
        return null;
    }

    @Override
    public void modifyInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet, InsnList instructions) {
        if (currentInstruction.getOpcode() == Opcodes.INVOKESPECIAL && currentInstruction instanceof MethodInsnNode) {
            MethodInsnNode currentMethodInsn = (MethodInsnNode) currentInstruction;
            if ("java/io/File".equals(currentMethodInsn.owner) && "<init>".equals(currentMethodInsn.name)) {
                if ("(Ljava/lang/String;Ljava/lang/String;)V".equals(currentMethodInsn.desc) || "(Ljava/io/File;Ljava/lang/String;)V".equals(currentMethodInsn.desc)) {
                    printMessage("Wrapping File constructor...");
                    String newDesc = currentMethodInsn.desc.substring(0, currentMethodInsn.desc.length() - 1) + "Ljava/io/File;";
                    instructions.insertBefore(currentInstruction, new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "pl/asie/foamfix/ProxyCommon",
                            "createFileSafe",
                            newDesc, false
                    ));
                    instructions.insertBefore(currentInstruction, new InsnNode(Opcodes.SWAP));
                    instructions.insertBefore(currentInstruction, new InsnNode(Opcodes.POP));
                    instructions.insertBefore(currentInstruction, new InsnNode(Opcodes.SWAP));
                    instructions.insertBefore(currentInstruction, new InsnNode(Opcodes.POP));
                    instructions.remove(currentMethodInsn);
                    successful = true;
                }
            }
        }
    }
}
