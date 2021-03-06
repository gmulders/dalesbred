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

package org.dalesbred.internal.utils;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.dalesbred.internal.utils.CollectionUtils.arrayOfType;
import static org.dalesbred.internal.utils.CollectionUtils.mapToList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CollectionUtilsTest {

    @Test
    public void mapping() {
        assertThat(mapToList(asList("foo", "quux", "xyzzy"), String::length), is(asList(3, 4, 5)));
    }

    @Test
    public void createArrayOfObjectType() {
        Object array = arrayOfType(Integer.class, asList(1, 42, 7));
        assertThat(array, is(instanceOf(Integer[].class)));

        Integer[] intArray = (Integer[]) array;
        assertThat(intArray.length, is(3));
        assertThat(intArray[0], is(1));
        assertThat(intArray[1], is(42));
        assertThat(intArray[2], is(7));
    }

    @Test
    public void createArrayOfPrimitiveType() {
        Object array = arrayOfType(int.class, asList(1, 42, 7));
        assertThat(array, is(instanceOf(int[].class)));

        int[] intArray = (int[]) array;
        assertThat(intArray.length, is(3));
        assertThat(intArray[0], is(1));
        assertThat(intArray[1], is(42));
        assertThat(intArray[2], is(7));
    }
}
