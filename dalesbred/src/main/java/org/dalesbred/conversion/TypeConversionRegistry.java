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

package org.dalesbred.conversion;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Registry containing the type-conversions used when converting database values
 * to model values and vice versa.
 */
public interface TypeConversionRegistry {

    /**
     * Registers conversion from given source database type to given target model type.
     */
    <S, T> void registerConversionFromDatabase(@NotNull Class<S> source, @NotNull Class<T> target, @NotNull Function<S, T> conversion);

    /**
     * Registers conversion from given source model type to database type.
     */
    <T> void registerConversionToDatabase(@NotNull Class<T> source, @NotNull Function<T, ?> conversion);

    /**
     * Registers conversions from database type to model type and back.
     */
    default <D, J> void registerConversions(@NotNull Class<D> databaseType,
                                            @NotNull Class<J> javaType,
                                            @NotNull Function<D, J> fromDatabase,
                                            @NotNull Function<J, D> toDatabase) {
        registerConversionFromDatabase(databaseType, javaType, fromDatabase);
        registerConversionToDatabase(javaType, toDatabase);
    }

    /**
     * Registers simple enum conversion that uses keyFunction to produce saved value and uses
     * same function on enum constants to convert values back.
     */
    <T extends Enum<T>,K> void registerEnumConversion(@NotNull Class<T> enumType, @NotNull Function<T,K> keyFunction);

    /**
     * Returns given enum-type to be saved as database native enum of given type name.
     */
    default <T extends Enum<T>> void registerNativeEnumConversion(@NotNull Class<T> enumType, @NotNull String typeName) {
        registerNativeEnumConversion(enumType, typeName, Enum::name);
    }

    /**
     * Returns given enum-type to be saved as database native enum of given type name. Given function
     * can be used to map the enum to the stored value.
     */
    <T extends Enum<T>, K> void registerNativeEnumConversion(@NotNull Class<T> enumType, @NotNull String typeName, @NotNull Function<T,K> keyFunction);
}
