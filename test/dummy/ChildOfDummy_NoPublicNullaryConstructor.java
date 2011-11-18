package dummy;

/****************************************************************
 * @author Zachary Kurmas
 * 
 ****************************************************************/
// (C) 2010 Zachary Kurmas
// Created Jan 4, 2010
public class ChildOfDummy_NoPublicNullaryConstructor extends DummyClass
{
	@SuppressWarnings("unused")
	private ChildOfDummy_NoPublicNullaryConstructor() {
		childOfDummy_NoPublicConstructor_DummyPackage = 89;
		return;
	}

	public ChildOfDummy_NoPublicNullaryConstructor(int foo) {
		childOfDummy_NoPublicConstructor_DummyPackage = foo;
		return;
	}
	
	public int childOfDummy_NoPublicConstructor_DummyPackage;
}
