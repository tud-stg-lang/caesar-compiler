package org.caesarj.compiler.constants;



/**
 * Several constants used in Caesar.
 * 
 * @author Jürgen Hallpap
 */
public interface CaesarConstants extends Constants {

	//Caesar names for generated methods, fields and parameters

	public static final String DEPLOY_SELF_METHOD =
		"$deploySelf";
	
	public static final String UNDEPLOY_SELF_METHOD =
		"$undeploySelf";

	public static final String ADVICE_METHOD = "ADVICE METHOD";

	//AspectJ names		

	public static final String PER_SINGLETON_INSTANCE_FIELD =
		"ajc$perSingletonInstance";
	
	public static final String ASPECT_CONTAINER_FIELD =
		"$aspectContainer";

	public static final String AJC_CLINIT_METHOD = "ajc$clinit";
	
	public static final String ASPECT_TO_DEPLOY = "$aspectToDeploy";

	public static final String ASPECT_OF_METHOD = "aspectOf";

	public static final String AROUND_CLOSURE_PARAMETER = "aroundClosure";

	public static final String THIS_JOIN_POINT = "thisJoinPoint";

	public static final String THIS_JOIN_POINT_STATIC_PART =
		"thisJoinPointStaticPart";

	public static final String THIS_ENCLOSING_JOIN_POINT_STATIC_PART =
		"thisEnclosingJoinPointStaticPart";

	public static final String PROCEED_METHOD = "proceed";

	//Some qualified class names
	
	public static final String SRC_AROUND_CLOSURE_CLASS =
		"org.aspectj.runtime.internal.AroundClosure";

	public static final String SRC_ASPECT_CONTAINER_IFC =
		"org.caesarj.runtime.aspects.AspectContainerIfc";
	
	public static final String SRC_ASPECT_DEPLOYER_IFC =
		"org.caesarj.runtime.aspects.AspectDeployerIfc";

	public static final String QUALIFIED_ITERATOR_CLASS = "java/util/Iterator";

	public static final String QUALIFIED_THREAD_CLASS = "java/lang/Thread";

	public static final String AROUND_CLOSURE_CLASS =
		"org/aspectj/runtime/internal/AroundClosure";
	
	public static final String CONVERSIONS_CLASS =
		"org/aspectj/runtime/internal/Conversions";

	public static final String JOIN_POINT_CLASS = "org/aspectj/lang/JoinPoint";

	public static final String JOIN_POINT_STATIC_PART_CLASS =
		"org/aspectj/lang/JoinPoint$StaticPart";

	public static final String CAESAR_ASPECT_REGISTRY_IFC_CLASS =
		"org/caesarj/runtime/aspects/AspectRegistryIfc";
        
    public static final String CAESAR_DEPLOY_SUPPORT_CLASS =  
        "org/caesarj/runtime/DeploySupport";

    public static final String CAESAR_ASPECT_IFC =  
        "org/caesarj/runtime/aspects/AspectIfc";
    
    public static final String CAESAR_OBJECT =  
        "org/caesarj/runtime/CaesarObject";
    
    public static final String CAESAR_OBJECT_IFC =
        "org/caesarj/runtime/CaesarObjectIfc";
    
    public static final String ASPECT_CONTAINER_IFC =  
    	"org/caesarj/runtime/aspects/AspectContainerIfc";

    public static final String
    	WRAPPER_WRAPPEE_FIELD = "$wrappee",
    	WRAPPER_WRAPPEE_INIT  = "$initWrappee",
    	
    	OUTER_ACCESS          = "outer",
    	OUTER_FIELD           = "$outer";
    
	//Caesar extension for the generated deployment support classes

	public static final String ASPECT_IFC_EXTENSION = "$Ifc";

	public static final String REGISTRY_EXTENSION = "$Registry";
	
	public static final String MULTI_INST_CLOSURE_EXTENSION = "$MultiInstClosure";
	
	// Constants for Advice-Attributes taken from ...aspectj.advice
	public static final int ExtraArgument = 1;
	public static final int ThisJoinPoint = 2;
	public static final int ThisJoinPointStaticPart = 4;
	public static final int ThisEnclosingJoinPointStaticPart = 8;
	public static final int ParameterMask = 0xf;


}
