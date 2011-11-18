/**
 * Copyright (c) Zachary Kurmas 2011
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.gvsu.kurmasz.warszawa.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * @author Zachary Kurmas
 */
// Created  11/8/11 at 3:52 PM
// (C) Zachary Kurmas 2011
@RunWith(PowerMockRunner.class)
@PrepareForTest({ArrayUtils.class})
public class ArrayUtilsTest {

   //
   // shrinkToFit T[]
   //

   @Test(expected = IllegalArgumentException.class)
   public void shrinkToFit_throwsExceptionIfSizeTooLarge() throws Exception {
      ArrayUtils.shrinkToFit(new String[]{"HI", "There"}, 3);
   }

   @Test
   public void shrinkToFit_returnsSameObjectifSizeZero() throws Throwable {
      String[] input = {};
      assertSame(input, ArrayUtils.shrinkToFit(input, 0));
   }

   @Test
   public void shrinkToFit_createsNewArrayIfSizeGreaterThanZero() throws Throwable {
      String[] input = {null};
      String[] output = ArrayUtils.shrinkToFit(input, 0);
      assertNotSame(input, output);
   }

   @Test
   public void shrinkToFit_createsNewArrayIfSizeSame() throws Throwable {
      String[] input = {"Blister"};
      String[] output = ArrayUtils.shrinkToFit(input, 1);
      assertNotSame(input, output);
   }

   @Test
   public void shrinkToFit_createsDesiredSizedArray() throws Throwable {
      String[] input = {"A", "B", "C", "D", "E", "F", "G"};
      String[] output = ArrayUtils.shrinkToFit(input, 3);
      assertEquals(3, output.length);
   }

   @Test
   public void shrinkToFit_copiesDesiredData() throws Throwable {
      String[] input = {"A", "B", "C", "D", "E", "F", "G"};
      String[] output = ArrayUtils.shrinkToFit(input, 3);
      assertArrayEquals(new String[]{"A", "B", "C"}, output);
   }

   //
   // shrinkToFit int
   //

   @Test(expected = IllegalArgumentException.class)
   public void shrinkToFit_int_throwsExceptionIfSizeTooLarge() throws Exception {
      ArrayUtils.shrinkToFit(new int[]{1, 2}, 3);
   }

   @Test
   public void shrinkToFit_int_returnsSameObjectifSizeZero() throws Throwable {
      int[] input = {};
      assertSame(input, ArrayUtils.shrinkToFit(input, 0));
   }

   @Test
   public void shrinkToFit_int_createsNewArrayIfSizeGreaterThanZero() throws Throwable {
      int[] input = {0};
      int[] output = ArrayUtils.shrinkToFit(input, 0);
      assertNotSame(input, output);
   }

   @Test
   public void shrinkToFit_int_createsNewArrayIfSizeSame() throws Throwable {
      int[] input = {38};
      int[] output = ArrayUtils.shrinkToFit(input, 1);
      assertNotSame(input, output);
   }

   @Test
   public void shrinkToFit_int_createsDesiredSizedArray() throws Throwable {
      int[] input = {8, 6, 7, 5, 3, 0, 9};
      int[] output = ArrayUtils.shrinkToFit(input, 3);
      assertEquals(3, output.length);
   }

   @Test
   public void shrinkToFit_int_copiesDesiredData() throws Throwable {
      int[] input = {8, 6, 7, 5, 3, 0, 9};
      int[] output = ArrayUtils.shrinkToFit(input, 3);
      assertArrayEquals(new int[]{8, 6, 7}, output);
   }

   //
   // shrinkToFit long
   //

   @Test(expected = IllegalArgumentException.class)
   public void shrinkToFit_long_throwsExceptionIfSizeTooLarge() throws Exception {
      ArrayUtils.shrinkToFit(new long[]{1, 2}, 3);
   }

   @Test
   public void shrinkToFit_long_returnsSameObjectifSizeZero() throws Throwable {
      long[] input = {};
      assertSame(input, ArrayUtils.shrinkToFit(input, 0));
   }

   @Test
   public void shrinkToFit_long_createsNewArrayIfSizeGreaterThanZero() throws Throwable {
      long[] input = {0};
      long[] output = ArrayUtils.shrinkToFit(input, 0);
      assertNotSame(input, output);
   }

   @Test
   public void shrinkToFit_long_createsNewArrayIfSizeSame() throws Throwable {
      long[] input = {38};
      long[] output = ArrayUtils.shrinkToFit(input, 1);
      assertNotSame(input, output);
   }

   @Test
   public void shrinkToFit_long_createsDesiredSizedArray() throws Throwable {
      long[] input = {8, 6, 7, 5, 3, 0, 9};
      long[] output = ArrayUtils.shrinkToFit(input, 3);
      assertEquals(3, output.length);
   }

   @Test
   public void shrinkToFit_long_copiesDesiredData() throws Throwable {
      long[] input = {8, 6, 7, 5, 3, 0, 9};
      long[] output = ArrayUtils.shrinkToFit(input, 3);
      assertArrayEquals(new long[]{8, 6, 7}, output);
   }

   //
   // Permute  T
   //
   @Test
   public void permuteUsesCollectionsShuffle() throws Throwable {
      mockStatic(Arrays.class);
      mockStatic(Collections.class);
      @SuppressWarnings("unchecked")
      List<String> list = mock(List.class);

      String[] array = {};

      when(Arrays.asList(array)).thenReturn(list);

      ArrayUtils.permute(array);
      verifyStatic();
      Arrays.asList(array);
      Collections.shuffle(list);
   }

   @Test
   public void permute_rnd_UsesCollectionsShuffle() throws Throwable {
      mockStatic(Arrays.class);
      mockStatic(Collections.class);
      @SuppressWarnings("unchecked")
      List<String> list = mock(List.class);

      String[] array = {};
      Random r = new Random();

      when(Arrays.asList(array)).thenReturn(list);

      ArrayUtils.permute(array, r);
      verifyStatic();
      Arrays.asList(array);
      Collections.shuffle(list, r);
   }

   //
   // Permute  int
   //

   @Test
   public void permute_int_handlesEmptyArray() throws Throwable {
      int[] empty = {};
      ArrayUtils.permute(empty);
      ArrayUtils.permute(empty, new Random());
   }

   @Test
   public void permute_int_handlesOneElement() throws Throwable {
      int[] one = {3};
      ArrayUtils.permute(one);
      assertArrayEquals(new int[]{3}, one);
   }

   @Test
   public void permute_int_rand_handlesOneElement() throws Throwable {
      int[] one = {3};
      ArrayUtils.permute(one, new Random());
      assertArrayEquals(new int[]{3}, one);
   }

   @Test
   public void permute_int_rand_permutes() throws Throwable {
      int size = 1000;
      int[] array = new int[size];
      for (int i = 0; i < size; i++) {
         array[i] = i;
      }
      int[] orig_array = new int[size];
      System.arraycopy(array, 0, orig_array, 0, size);

      ArrayUtils.permute(array, new Random());
      // array should differ
      int i = 0;
      while (array[i] == orig_array[i]) {
         if (++i >= size) {
            fail("Arrays not permuted.  (Could fail randomly.");
         }
      }
      // permuted array should have the same data
      Arrays.sort(array);
      assertArrayEquals(orig_array, array);
   }

   @Test
   public void permute_int_random_uses_static_rand() throws Throwable {
      spy(ArrayUtils.class);
      Random r = new Random();
      when(ArrayUtils.getRandom()).thenReturn(r);
      int[] array = {1, 2, 3, 4};
      ArrayUtils.permute(array);

      verifyStatic();
      ArrayUtils.getRandom();
      ArrayUtils.permute(array, r);
   }

   @Test
   public void getRandomAlwaysReturnsSameObject() throws Throwable {
      Random expected = ArrayUtils.getRandom();
      assertSame(expected, ArrayUtils.getRandom());
      assertSame(expected, ArrayUtils.getRandom());
      assertSame(expected, ArrayUtils.getRandom());
   }


}


