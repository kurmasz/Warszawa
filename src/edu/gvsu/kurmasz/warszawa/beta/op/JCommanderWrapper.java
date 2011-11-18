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
package edu.gvsu.kurmasz.warszawa.beta.op;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.ParameterException;
import joptsimple.internal.AbbreviationMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for <a href="http://jcommander.org" Cédric Beust's {@code JCommander} option parser</a> that adds
 * additional functionality.
 *
 * @author Zachary Kurmas
 */
// Created  11/14/11 at 5:41 PM
// (C) Zachary Kurmas 2011

public class JCommanderWrapper {

   private static List<String> getLongOptions(JCommander jc) {
      List<String> longOpts = new ArrayList<String>();

      for (ParameterDescription pd : jc.getParameters()) {
         for (String param : pd.getNames().split(",")) {
            String tparam = param.trim();
            if (tparam.startsWith("--")) {
               longOpts.add(tparam);
            }
         }
      }
      return longOpts;
   }

   private static void expandLongOpts(List<String> options, String[] args) {
      AbbreviationMap<String> map = new AbbreviationMap<String>();
      for (String option : options) {
         map.put(option, option);
      }

      for (int x = 0; x < args.length; x++) {
         if (args[x].startsWith("--")) {
            if (args[x].startsWith("--") && !map.contains(args[x])) {
               throw new ParameterException("No such option:  " + args[x]);
            } else {
               args[x] = map.get(args[x]);
            }
         }
      }
   }

   /**
    * Replaces unambiguously abbreviated option names with their full names before parsing the options.  This feature
    * allows users to abbreviate long option names. For example, given options "--alpha", "--beta", and "--best", users would need only type "--a", "--bet",
    * or "--bes" on the command line.  This wrapper also throws an exception when given an argument that doesn't exist.
    *
    * @param object The argument object containing {@code JCommander} {@code Parameter} annotations.
    * @param args   The command line arguments
    */
   public static void parse(Object object, String... args) {
      JCommander jc = new JCommander(object);
      List<String> longOpts = getLongOptions(jc);
      expandLongOpts(longOpts, args);
      jc.parse(args);
   }
}
