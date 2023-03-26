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

package pl.asie.foamfix;

import com.google.common.cache.CacheBuilder;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class LaunchWrapperRuntimeFix {
    private LaunchWrapperRuntimeFix() {

    }

    public static void weakenResourceCache() {
        if (!(LaunchWrapperRuntimeFix.class.getClassLoader() instanceof LaunchClassLoader)) return;

        try {
            LaunchClassLoader loader = (LaunchClassLoader) LaunchWrapperRuntimeFix.class.getClassLoader();

            Field resourceCacheField = ReflectionHelper.findField(LaunchClassLoader.class, "resourceCache");
            Map oldResourceCache = (Map) resourceCacheField.get(loader);
            Map newResourceCache = CacheBuilder.newBuilder().weakValues().build().asMap();
            newResourceCache.putAll(oldResourceCache);
            resourceCacheField.set(loader, newResourceCache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removePackageManifestMap() {
        if (!(LaunchWrapperRuntimeFix.class.getClassLoader() instanceof LaunchClassLoader)) return;

        try {
            LaunchClassLoader loader = (LaunchClassLoader) LaunchWrapperRuntimeFix.class.getClassLoader();

            Field pmField = ReflectionHelper.findField(LaunchClassLoader.class, "packageManifests");
            pmField.set(loader, new Map<Package, Manifest>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return true;
                }

                @Override
                public boolean containsKey(Object o) {
                    return false;
                }

                @Override
                public boolean containsValue(Object o) {
                    return false;
                }

                @Override
                public Manifest get(Object o) {
                    return null;
                }

                @Override
                public Manifest put(Package o, Manifest o2) {
                    return o2;
                }

                @Override
                public Manifest remove(Object o) {
                    return null;
                }

                @Override
                public void putAll(Map map) {

                }

                @Override
                public void clear() {

                }

                @Override
                public Set keySet() {
                    return Collections.emptySet();
                }

                @Override
                public Collection values() {
                    return Collections.emptySet();
                }

                @Override
                public Set<Entry<Package, Manifest>> entrySet() {
                    return Collections.emptySet();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
