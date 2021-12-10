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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import pl.asie.foamfix.bugfixmod.coremod.MappingRegistry;
import pl.asie.foamfix.bugfixmod.coremod.patchers.AbstractPatcher;
import pl.asie.foamfix.bugfixmod.coremod.patchers.ModificationPatcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

public class Log4JLoggerWrapperPatcher extends AbstractPatcher  {
	public Log4JLoggerWrapperPatcher(String name) {
		super(name, "", "", "");
	}

	@Override
	public String getPatcherName() {
		return patcherName;
	}

	@Override
	public InsnList buildNewInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet) {
		return null;
	}

	@Override
	protected void patchClassNode(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			AbstractInsnNode currentInstruction;
			ListIterator<AbstractInsnNode> instructionSet = method.instructions.iterator();
			while (instructionSet.hasNext()) {
				currentInstruction = instructionSet.next();
				if (currentInstruction.getOpcode() == Opcodes.INVOKESTATIC
					&& ((MethodInsnNode) currentInstruction).owner.equals("org/apache/logging/log4j/LogManager")
					&& ((MethodInsnNode) currentInstruction).name.equals("getLogger")
					&& ((MethodInsnNode) currentInstruction).desc.endsWith(")Lorg/apache/logging/log4j/Logger;")
				) {
					printMessage("Applying Log4j exploit mitigation to " + classNode.name + "/" + ((MethodInsnNode) currentInstruction).name + "!");
					MethodInsnNode methodInsnNode = new MethodInsnNode(Opcodes.INVOKESTATIC,
							"pl/asie/foamfix/HackyMessageFactoryWrapper", "fixLogger", "(Lorg/apache/logging/log4j/Logger;)Lorg/apache/logging/log4j/Logger;", false);
					instructionSet.add(methodInsnNode);

					successful = true;
				}
			}
		}
	}
}
