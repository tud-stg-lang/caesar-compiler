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
	public static final String SETTING_PREFIX = "_set";
	public static String toCollaborationInterfaceImplName(String name)
	{
		return name + PROXY_POSTFIX;
	}
	
	private static String fixName(String name)
	{
		if (name.startsWith("_"))
			name = name.substring(1);
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	public static String toAccessorMethodName(String name)
	{
		return ACCESSOR_PREFIX + fixName(name);
	}
	public static String toSettingMethodName(String name)
	{
		return SETTING_PREFIX + fixName(name);
	}
	
	public static boolean isSettingMethodName(String name)
	{
		return toSettingMethodName(IMPLEMENTATION_FIELD_NAME).equals(name)
				|| toSettingMethodName(BINDING_FIELD_NAME).equals(name);
	}
}
