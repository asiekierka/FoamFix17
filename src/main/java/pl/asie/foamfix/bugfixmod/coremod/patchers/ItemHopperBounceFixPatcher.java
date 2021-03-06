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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import pl.asie.foamfix.bugfixmod.coremod.MappingRegistry;

import java.util.Iterator;

/**
 * Created by Vincent on 6/12/2014.
 *
 * This fix removes the setblockbounds to a full size block, preventing items from boinging around on locked hoppers.
 */
public class ItemHopperBounceFixPatcher extends AbstractPatcher implements ModificationPatcher {
    public ItemHopperBounceFixPatcher(String name, String targetClassName, String targetMethodName, String targetMethodDesc) {
        super(name, targetClassName, targetMethodName, targetMethodDesc);
    }

    @Override
    public InsnList buildNewInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet) {
        return null;
    }

    @Override
    public void modifyInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet, InsnList instructions) {
        if (currentInstruction instanceof MethodInsnNode && currentInstruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
            String sbbMethodName = "BlockHopper.setBlockBounds";

            if (((MethodInsnNode) currentInstruction).name.equals(sbbMethodName)) {
                if (currentInstruction.getPrevious().getOpcode() == Opcodes.FCONST_1           // Much tedium, very dirty
                        && currentInstruction.getPrevious().getPrevious().getOpcode() == Opcodes.FCONST_1
                        && currentInstruction.getPrevious().getPrevious().getPrevious().getOpcode() == Opcodes.FCONST_1) {
                    AbstractInsnNode temp = currentInstruction.getPrevious().getPrevious().getPrevious().getPrevious();
                    if (temp.getOpcode() == Opcodes.FCONST_0
                            && temp.getPrevious().getOpcode() == Opcodes.FCONST_0
                            && temp.getPrevious().getPrevious().getOpcode() == Opcodes.FCONST_0) {
                        instructions.remove(currentInstruction);
                        successful = true;
                    }
                }
            }
        }
    }
}
