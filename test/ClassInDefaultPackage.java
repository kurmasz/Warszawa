/**
 * Copyright (c) Zachary Kurmas 2007
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
/****************************************************************
 * @author Zachary Kurmas
 *
 *Just a simple class in the default package used to beta ClassFinder
 ****************************************************************/
// (C) 2008 Zachary Kurmas
// Created Jun 12, 2008
public class ClassInDefaultPackage
{
	public static class InnerPublic
	{
		public int innerPublic_classInDefaultPackage = 10;
	}
	
	protected static class InnerProtected
	{
		public int innerProtected_classInDefaultPackage = 11;
		
		public InnerProtected() {
			return;
		}
		
	}
	
	@SuppressWarnings("unused")
	private static class InnerPrivate
	{
		public int innerPrivate_classInDefaultPackage = 12;
		
		public InnerPrivate() {
			return;
		}
	}
	
	
	public int classInDefaultPackage = 9;
	protected String Dave = "Dave";
	
	public ClassInDefaultPackage()
	{
		return;
	}
}
