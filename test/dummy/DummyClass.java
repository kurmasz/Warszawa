package dummy;

/****************************************************************
 * @author kurmasz
 * 
 ****************************************************************/
// (C) 2008 Zachary Kurmas
// Created Jun 12, 2008
public class DummyClass
{
	public static class InnerStatic
	{
		public int dummyClass_InnerStatic = 934;

		public int getMagicNumber()
		{
			return dummyClass_InnerStatic;
		}
	}

	protected static class InnerProtectedStatic
	{
		public int dummyClass_InnerProtectedStatic = 222;

	}

	private static class InnerPrivateStatic
	{
		public int dummyClass_InnerPrivateStatic = 242;

	}

	public class InnerNonStatic
	{
		public int innerNonStatic;

		public InnerNonStatic() {
			innerNonStatic = 8934;
		}
	}

	public int dummyClass_DummyPackage = 90;

	public DummyClass() {
		return;
	}

	public int getMagicNumber()
	{
		return dummyClass_DummyPackage;
	}

	public String toString()
	{
		return "Running class " + this.getClass().getName();
	}

	public static Class<?> getPrivateChild()
	{
		return PrivateChildOfDummy.class;
	}

	public static Class<?> getIPS()
	{
		return InnerProtectedStatic.class;
	}

	public static Class<?> getInnerPrivateStatic()
	{
		return InnerPrivateStatic.class;
	}

}
