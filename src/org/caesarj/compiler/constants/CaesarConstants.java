package org.caesarj.compiler.constants;



/**
 * Several constants used in Caesar.
 * 
 * @author Jürgen Hallpap
 */
public interface CaesarConstants extends Constants {

	//Caesar names for generated methods, fields and parameters

	public static final String DEPLOY_METHOD = "$deploy";

	public static final String UNDEPLOY_METHOD = "$undeploy";

	public static final String SET_DEPLOYMENT_THREAD_METHOD =
		"$setDeploymentThread";

	public static final String GET_DEPLOYMENT_THREAD_METHOD =
		"$getDeploymentThread";

	public static final String DEPLOY_SELF_METHOD =
		"$deploySelf";
	
	public static final String UNDEPLOY_SELF_METHOD =
		"$undeploySelf";

	public static final String GET_DEPLOYED_INSTANCES_METHOD =
		"$getDeployedInstances";

	public static final String GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD =
		"$getThreadLocalDeployedInstances";

	public static final String INSTANCE_TO_DEPLOY = "$instanceToDeploy";

	public static final String DEPLOYMENT_THREAD = "$deploymentThread";

	public static final String DEPLOYED_INSTANCES = "$deployedInstances";

	public static final String ASPECT_INSTANCE = "$aspectInstance";

	public static final String ASPECT_TO_DEPLOY = "$aspectToDeploy";

	public static final String PER_THREAD_DEPLOYED_INSTANCES =
		"$perThreadDeployedInstances";

	public static final String ADVICE_METHOD = "ADVICE METHOD";

	//AspectJ names		

	public static final String PER_SINGLETON_INSTANCE_FIELD =
		"ajc$perSingletonInstance";

	public static final String AJC_CLINIT_METHOD = "ajc$clinit";

	public static final String ASPECT_OF_METHOD = "aspectOf";

	public static final String HAS_ASPECT_METHOD = "hasAspect";

	public static final String AROUND_CLOSURE_PARAMETER = "aroundClosure";

	public static final String THIS_JOIN_POINT = "thisJoinPoint";

	public static final String THIS_JOIN_POINT_STATIC_PART =
		"thisJoinPointStaticPart";

	public static final String THIS_ENCLOSING_JOIN_POINT_STATIC_PART =
		"thisEnclosingJoinPointStaticPart";

	public static final String PROCEED_METHOD = "proceed";

	//Some qualified class names

	public static final String QUALIFIED_ITERATOR_CLASS = "java/util/Iterator";

	public static final String QUALIFIED_THREAD_CLASS = "java/lang/Thread";

	public static final String AROUND_CLOSURE_CLASS =
		"org/aspectj/runtime/internal/AroundClosure";

	public static final String CONVERSIONS_CLASS =
		"org/aspectj/runtime/internal/Conversions";

	public static final String JOIN_POINT_CLASS = "org/aspectj/lang/JoinPoint";

	public static final String JOIN_POINT_STATIC_PART_CLASS =
		"org/aspectj/lang/JoinPoint$StaticPart";

	public static final String CAESAR_DEPLOYABLE_IFC =
		"org/caesarj/runtime/Deployable";

	public static final String CAESAR_SINGLETON_ASPECT_IFC_CLASS =
		"org/caesarj/runtime/AspectRegistry";
        
    public static final String CAESAR_DEPLOY_SUPPORT_CLASS =  
        "org/caesarj/runtime/DeploySupport";

    public static final String CAESAR_ASPECT_IFC =  
        "org/caesarj/runtime/AspectIfc";
    
    public static final String CAESAR_OBJECT =  
        "org/caesarj/runtime/CaesarObject";

	//Caesar extension for the generated deployment support classes

	public static final String ASPECT_IFC_EXTENSION = "$Ifc";

	public static final String MULTI_INSTANCE_CONTAINER_EXTENSION =
		"$MultiInstanceContainer";

	public static final String THREAD_MAPPER_EXTENSION = "$ThreadMapper";

	public static final String REGISTRY_EXTENSION = "$Registry";
	
	// Constants for Advice-Attributes taken from ...aspectj.advice
	public static final int ExtraArgument = 1;
	public static final int ThisJoinPoint = 2;
	public static final int ThisJoinPointStaticPart = 4;
	public static final int ThisEnclosingJoinPointStaticPart = 8;
	public static final int ParameterMask = 0xf;


}
