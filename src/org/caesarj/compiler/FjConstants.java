package org.caesarj.compiler;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeContext;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.util.InconsistencyException;

public class FjConstants {
	public static final String SEPERATOR = "_".intern();
	public static final String THIS_NAME = "this".intern();
	public static final String OUTER_THIS_NAME = "outerThis".intern();
	public static final String SUPER = "fjSuper".intern();
	public static final String SUB = "fjSub".intern();
	public static final String SELF_NAME = (SEPERATOR + "self").intern();
	public static final String TARGET_NAME = (SEPERATOR + "target").intern();
	public static final String TAIL_NAME = (SEPERATOR + "tail").intern();
	public static final String PARENT_NAME = (SEPERATOR + "parent").intern();
	public static final String ACCESSOR_PREFIX = (SEPERATOR + "private" + SEPERATOR).intern();
	public static final String GENERATED_DISPATCHING_INSTANCE = "getInstance".intern();
	public static final String GENERATED_DISPATCHING_GET = "get".intern();
	public static final String CLASS_BASED_DELEGATION_INSTANCE = "getInstance".intern();
	public static final String CLASS_BASED_DELEGATION_M = "M".intern();
	public static final String CLASS_BASED_DELEGATION_NAME = (SEPERATOR + "delegation").intern();
	public static final String CLASS_BASED_DELEGATION_TYPE = "org/caesarj/runtime/ClassBasedDelegation".intern();
	public static final String CAST_IMPL_NAME = "org/caesarj/runtime/CastImpl".intern();
	public static final String CLASS_BASED_DELEGATION_SUPER = "fjSuper".intern();
	public static final String CLASS_BASED_DELEGATION_SUB = "fjSub".intern();
	public static final String GENERATED_DISPATCHING_NAME = (SEPERATOR + "dispatching").intern();
	public static final String IS_CHILD_OF_METHOD_NAME = (SEPERATOR + "isChildOf").intern();
	public static final String GET_TARGET_METHOD_NAME = (SEPERATOR + "getTarget").intern();
	public static final String GET_TAIL_METHOD_NAME = (SEPERATOR + "getTail").intern();
	public static final String CHECK_FAMILY_METHOD_NAME = "checkFamily".intern();
	public static final String GET_FAMILY_METHOD_NAME = (SEPERATOR + "getFamily").intern();
	public static final String SET_FAMILY_METHOD_NAME = (SEPERATOR + "setFamily").intern();
	public static final String GET_PARENT_METHOD_NAME = (SEPERATOR + "getParent").intern();
	public static final String GET_DISPATCHER_METHOD_NAME = (SEPERATOR + "getDispatcher").intern();
	public static final String IDENTITY_TYPE_NAME = ("org/caesarj/runtime/IdentityImpl").intern();
	public static final String IS_IDENTICAL_METHOD_NAME = (SEPERATOR + "identical").intern();
	public static final String CHILD_TYPE_NAME = ("org/caesarj/runtime/Child").intern();
	public static final CReferenceType CHILD_TYPE = new CClassNameType( CHILD_TYPE_NAME );
	public static final String CHILD_IMPL_TYPE_NAME = ("org/caesarj/runtime/ChildImpl").intern();
	public static final String UNKNOWN_TYPE_NAME = ("familyj/compiler/TypeYetUnknown").intern();
	private static final String FACTORY_PREFIX = "create".intern();
	private static final String PARAM_PREFIX = "renamed".intern();
	private static final String IMPL_POSTFIX = "_Impl".intern();
	protected static final String PROXY_POSTFIX = "_Proxy".intern();
	public static final String IMPLEMENTATION_METHOD_SUFFIX = ( SEPERATOR + "implementation").intern();
	public static String implementationMethodName( String methodName ) {
		return (SEPERATOR + methodName + IMPLEMENTATION_METHOD_SUFFIX ).intern();
	}
	public static final String SELFCONTEXT_METHOD_SUFFIX = ( SEPERATOR + "selfContext").intern();
	public static String selfContextMethodName( String methodName ) {
		return (SEPERATOR + methodName + SELFCONTEXT_METHOD_SUFFIX ).intern();
	}
	public static String baseName( String className ) {
		return typeName(className + IMPL_POSTFIX);
	}
	public static String toImplName( String className ) {
		if( isIfcImplName( className ) )
			return baseName( className.substring( 0, className.lastIndexOf( PROXY_POSTFIX ) ) );
		else if( isIfcName( className ) )
			return baseName( className );
		else if( isBaseName( className ) )
			return className;
		else
			return null;
	}
	public static String toFullQualifiedBaseName( String className, CTypeContext context ) throws UnpositionedError {		
		boolean mostOuterIsClean = true;		
		if( className.indexOf( "$" ) >= 0 ) {
			String mostOuterName = className.substring( 0, className.indexOf( '$' ) );		
			CReferenceType mostOuterType =
				(CReferenceType) new CClassNameType( mostOuterName ).checkType( context );
			if( !mostOuterType.getCClass().isInterface() )
				mostOuterIsClean = false;
		}			
		className = className.replaceAll( PROXY_POSTFIX, "" );
		className = className.replaceAll( IMPL_POSTFIX, "" );
		className = className.replaceAll( "\\$", IMPL_POSTFIX + "\\$" );
		className = className.replaceAll( "$", IMPL_POSTFIX );
		if( !mostOuterIsClean )
			className = className.replaceFirst( IMPL_POSTFIX, "" );
		return className;
	}
	public static String toIfcName( String className ) {
		if( isIfcImplName( className ) )
			return cleanInterfaceName( className.substring( 0, className.lastIndexOf( PROXY_POSTFIX ) ) );
		else if( isBaseName( className ) )
			return cleanInterfaceName( className.substring( 0, className.lastIndexOf( IMPL_POSTFIX ) ) );
		else if( isIfcName( className ) )
			return typeName( className );
		else
			return null;
	}
	public static String toProxyName( String className ) {
		if( isBaseName( className ) )
			return cleanInterfaceImplementationName( className.substring( 0, className.lastIndexOf( IMPL_POSTFIX ) ) );
		else if( isIfcName( className ) )
			return cleanInterfaceImplementationName( className );
		else if( isIfcImplName( className ) )
			return typeName( className );
		else
			return null;
	}
	public static boolean isIfcName( String className ) {
		return !isBaseName( className ) && !isIfcImplName( className );
	}
	public static boolean isBaseName( String className ) {
		return className.endsWith( IMPL_POSTFIX );
	}
	public static boolean isIfcImplName( String className ) {
		return className.endsWith( PROXY_POSTFIX );
	}
	public static boolean isFactoryMethodName( String methodName ) {
		return methodName.startsWith( SEPERATOR + FACTORY_PREFIX );
	}
	public static String cleanInterfaceName( String className ) {
		return typeName( className );
	}
	public static String cleanInterfaceImplementationName( String className ) {
		return typeName(className + PROXY_POSTFIX);
	}
	public static String factoryMethodName( String className ) {
		return typeName(SEPERATOR + FACTORY_PREFIX + toIfcName( className ));
	}
	public static String renameParameter( String paramName ) {
		return typeName(SEPERATOR + PARAM_PREFIX + SEPERATOR + ( paramName ));
	}
	protected static String typeName( String nearlyTypeName ) {
		return nearlyTypeName.replace( '.', '/' ).intern();
	}
	public static boolean isImplementationMethodName( String methodName ) {
		return methodName.startsWith( SEPERATOR )
			&& methodName.endsWith( IMPLEMENTATION_METHOD_SUFFIX );
	}
	public static boolean isSelfContextMethodName( String methodName ) {
		return methodName.startsWith( SEPERATOR )
			&& methodName.endsWith( SELFCONTEXT_METHOD_SUFFIX );
	}
	public static TokenReference STD_TOKEN_REFERENCE = new TokenReference( "<generated>", 0 );
	
	public static String uniqueMethodId( CMethod method ) {
		String name = method.getIdent();
		String[] paramTypes = new String[ method.getParameters().length ];
		for( int i = 0; i < paramTypes.length; i++ ) {
			try {
				paramTypes[ i ] = method.getParameters()[ i ].getCClass().getQualifiedName();
			} catch( InconsistencyException e ) {
				// primitive types
				paramTypes[ i ] = method.getParameters()[ i ].toString();
			}
		}
		return uniqueMethodId( name, paramTypes );
	}
	public static String uniqueMethodId( String methodName, JFormalParameter[] params ) {
		CType[] paramTypes = new CType[ params.length ];
		for( int i = 0; i < paramTypes.length; i++ ) {
			paramTypes[ i ] = params[ i ].getType();
		}
		return uniqueMethodId( methodName, paramTypes );
	}
	public static String uniqueMethodId( String methodName, CType[] params ) {
		String[] paramTypes = new String[ params.length ];
		for( int i = 0; i < paramTypes.length; i++ ) {
			try {
				paramTypes[ i ] = params[ i ].getCClass().getQualifiedName();
			} catch( InconsistencyException e ) {
				// primitive types
				paramTypes[ i ] = params[ i ].toString();
			}
		}
		return uniqueMethodId( methodName, paramTypes );
	}
	public static String uniqueMethodId( String methodName, String[] paramTypes ) {
		StringBuffer s = new StringBuffer( methodName );
		for( int i = 0; i < paramTypes.length; i++ ) {
			s.append( "," );
			s.append( paramTypes[ i ] );
		}
		return s.toString();
	}
	public static boolean isPrivateAccessorId( String name ) {
		return name.startsWith( ACCESSOR_PREFIX );
	}
	public static String privateAccessorId(
		String methodName,
		String cleanTypeName,
		String uniqueMethodId ) {
			
		StringBuffer s = new StringBuffer();
		s.append( ACCESSOR_PREFIX );
		s.append( methodName );
		s.append( SEPERATOR );
		s.append( hash( cleanTypeName + uniqueMethodId ) );
		return s.toString().intern();
	}
	public static String hash( String message ) {
		MessageDigest digester = null;
		try {
			digester = MessageDigest.getInstance( "SHA" );
		} catch( NoSuchAlgorithmException e ) {
			throw new RuntimeException( e.getMessage() );
		}
		digester.digest( message.getBytes() );
		BigInteger i = new BigInteger( digester.digest() );
		String s = i.toString( 16 );
		if( s.charAt( 0 ) == '-' )
			s = s.substring( 1 );
		return s;
	}
	public static String removeFamilyJ( String message ) {
		message = message.replaceAll( SEPERATOR+FACTORY_PREFIX, "" );
		message = message.replaceAll( IMPL_POSTFIX, "" );
		message = message.replaceAll( PROXY_POSTFIX, "" );
		return message;
	}
}
