package org.caesarj.compiler;

/**
 * @author Walter Augusto Werner
 */
public class CciConstants
	extends FjConstants
{
	public static final String IMPLEMENTATION_FIELD_NAME = "_implementation";	 
	public static final String BINDING_FIELD_NAME = "_binding";
	public static final String ACCESSOR_PREFIX = "_get";
	public static String toCollaborationInterfaceImplName(String name)
	{
		return name + PROXY_POSTFIX;
	}

	public static String toAccessorMethodName(String name)
	{
		if (name.startsWith("_"))
			name = name.substring(1);
		return ACCESSOR_PREFIX + name;
	}
	
}
