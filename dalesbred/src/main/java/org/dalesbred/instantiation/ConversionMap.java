/*
 * Copyright (c) 2015 Evident Solutions Oy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.dalesbred.instantiation;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;

import static org.dalesbred.internal.utils.Primitives.wrap;
import static org.dalesbred.internal.utils.TypeUtils.*;

final class ConversionMap {

    @NotNull
    private final Map<Type, List<TypeConversion<?,?>>> mappings = new HashMap<>();

    void register(@NotNull TypeConversion<?, ?> coercion) {
        Type source = wrap(coercion.getSource());

        mappings.computeIfAbsent(source, a -> new ArrayList<>()).add(coercion);
    }

    @NotNull
    Optional<TypeConversion<?,?>> findConversion(@NotNull Type source, @NotNull Type target) {
        for (Type cl = wrap(source); cl != null; cl = genericSuperClass(cl)) {
            Optional<TypeConversion<?,?>> conversion = findConversionsRegisteredFor(cl, target);
            if (conversion.isPresent())
                return conversion;
        }

        for (Type cl : genericInterfaces(source)) {
            Optional<TypeConversion<?,?>> conversion = findConversionsRegisteredFor(cl, target);
            if (conversion.isPresent())
                return conversion;
        }

        return Optional.empty();
    }

    @NotNull
    private Optional<TypeConversion<?,?>> findConversionsRegisteredFor(@NotNull Type source, @NotNull Type target) {
        List<TypeConversion<?,?>> candidates = mappings.get(source);
        if (candidates != null) {
            for (int i = candidates.size() - 1; i >= 0; i--) {
                TypeConversion<?, ?> conversion = candidates.get(i);
                if (isAssignable(rawType(target), rawType(conversion.getTarget())))
                    return Optional.of(conversion);
            }
        }

        return Optional.empty();
    }
}
