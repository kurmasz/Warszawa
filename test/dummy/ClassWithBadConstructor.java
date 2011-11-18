package dummy;

/****************************************************************
 * @author Zachary Kurmas
 *
 ****************************************************************/
// (C) 2010 Zachary Kurmas
// Created Jan 4, 2010
public class ClassWithBadConstructor
{
	public ClassWithBadConstructor()
	{
		throw new IllegalArgumentException("Just being difficult");
	}

}
