package org.caesarj.compiler;

import org.caesarj.kjc.Constants;

/**
 * Several constants used in Caesar.
 * 
 * @author Jürgen Hallpap
 */
public interface CaesarConstants extends Constants {

	//Caesar names for generated methods, fields and parameters

	public static final String DEPLOY_METHOD = "_deploy";

	public static final String UNDEPLOY_METHOD = "_undeploy";

	public static final String SET_DEPLOYMENT_THREAD_METHOD =
		"_setDeploymentThread";

	public static final String GET_DEPLOYMENT_THREAD_METHOD =
		"_getDeploymentThread";

	public static final String GET_SINGLETON_ASPECT_METHOD =
		"_getSingletonAspect";

	public static final String GET_DEPLOYED_INSTANCES_METHOD =
		"_getDeployedInstances";

	public static final String GET_THREAD_LOCAL_DEPLOYED_INSTANCES_METHOD =
		"_getThreadLocalDeployedInstances";

	public static final String INSTANCE_TO_DEPLOY = "_instanceToDeploy";

	public static final String DEPLOYMENT_THREAD = "_deploymentThread";

	public static final String DEPLOYED_INSTANCES = "_deployedInstances";

	public static final String ASPECT_INTANCE = "_aspectInstance";

	public static final String ASPECT_TO_DEPLOY = "_aspectToDeploy";

	public static final String PER_THREAD_DEPLOYED_INSTANCES =
		"_perThreadDeployedInstances";

	public static final String ADVICE_METHOD = "ADVICE METHOD";

	//AspectJ names		

	public static final String PER_SINGLETON_INSTANCE_FIELD =
		"ajc$perSingletonInstance";

	public static final String AJC_CLINIT_METHOD = "ajc$clinit";

	public static final String ASPECT_OF_METHOD = "aspectOf";

	public static final String HAS_ASPECT_MEHTOD = "hasAspect";

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

	public static final String CAESAR_ASPECT_IFC_CLASS =
		"org/caesarj/runtime/Deployable";

	public static final String CAESAR_SINGLETON_ASPECT_IFC_CLASS =
		"org/caesarj/runtime/AspectRegistry";

	//Caesar extension for the generated deployment support classes

	public static final String ASPECT_IFC_EXTENSION = "_Ifc";

	public static final String MULTI_INSTANCE_ASPECT_EXTENSION =
		"_MultiInstances";

	public static final String MULTI_THREAD_ASPECT_EXTENSION = "_MultiThreads";

	public static final String SINGLETON_ASPECT_EXTENSION = "_AspectRegistry";

}
