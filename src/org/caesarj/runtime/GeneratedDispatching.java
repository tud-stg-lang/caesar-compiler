package org.caesarj.runtime;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

import org.caesarj.compiler.FjConstants;

import de.fub.bytecode.Constants;
import de.fub.bytecode.classfile.Method;
import de.fub.bytecode.classfile.Utility;
import de.fub.bytecode.generic.ClassGen;
import de.fub.bytecode.generic.FieldGen;
import de.fub.bytecode.generic.IF_ACMPNE;
import de.fub.bytecode.generic.InstructionConstants;
import de.fub.bytecode.generic.InstructionFactory;
import de.fub.bytecode.generic.InstructionList;
import de.fub.bytecode.generic.MethodGen;
import de.fub.bytecode.generic.ObjectType;
import de.fub.bytecode.generic.PUSH;
import de.fub.bytecode.generic.Type;

/**
 * @author andreas
 */
public class GeneratedDispatching {
	protected static GeneratedDispatching instance;
	protected DefiningClassLoader classLoader;
	protected ClassBasedDelegation delegation;
	protected Set forbiddenMethods;	
	protected Hashtable classCache;
	protected WeakHashMap objectCache;

	public static GeneratedDispatching getInstance() {
		if( instance == null ) {
			instance = new GeneratedDispatching();
		}
		return instance;
	}	
	protected GeneratedDispatching() {
		forbiddenMethods = new HashSet();
		// a child's nethods
	    forbiddenMethods.add( FjConstants.GET_TARGET_METHOD_NAME );
	    forbiddenMethods.add( FjConstants.GET_TAIL_METHOD_NAME );
	    forbiddenMethods.add( FjConstants.GET_PARENT_METHOD_NAME );
	    forbiddenMethods.add( FjConstants.GET_FAMILY_METHOD_NAME );
	    forbiddenMethods.add( FjConstants.SET_FAMILY_METHOD_NAME + "/java.lang.Object" );
	    forbiddenMethods.add( FjConstants.IS_CHILD_OF_METHOD_NAME + "/org.caesarj.runtime.Child" );
	    forbiddenMethods.add( FjConstants.GET_DISPATCHER_METHOD_NAME + "/java.lang.Object" );
	    // an object's methods
	    forbiddenMethods.add("wait");
	    forbiddenMethods.add("wait/long/int");
	    forbiddenMethods.add("wait/long");
	    forbiddenMethods.add("getClass");
	    forbiddenMethods.add("notify");
	    forbiddenMethods.add("notifyAll");
	    forbiddenMethods.add("finalize");
	    //forbiddenMethods.add("toString");
	    //forbiddenMethods.add("clone");
	    //forbiddenMethods.add("equals/java.lang.Object");
	    //forbiddenMethods.add("hashCode");

	    classCache = new Hashtable();
	    objectCache = new WeakHashMap();
		classLoader = new DefiningClassLoader( GeneratedDispatching.class.getClassLoader() );
		delegation = new ClassBasedDelegation();
	}

	public Object get( Child child, Child parent ) {
		
		if( child == parent )
			return child;
		
		try {
			Object dispatchObject =
				objectCache.get( getObjectKey( child, parent ) );
			if( dispatchObject == null ) {
				Class dispatchClass = getClass( child, parent );
				dispatchObject = dispatchClass.getConstructors()[ 0 ].
					newInstance( new Object[]{ child, parent, this.delegation, this } );
				objectCache.put( getObjectKey( child, parent ), dispatchObject );
			}
			return dispatchObject;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	protected Class getClass( Child child, Child parent ) {
		Class dispatchClass = (Class)
			classCache.get( getClassName( child.getClass(), parent.getClass() ) );
		if( dispatchClass == null ) {
			dispatchClass = createClass( child, parent );
			classCache.put(
				getClassName( child.getClass(), parent.getClass() ),
				dispatchClass
			);			
		}
		return dispatchClass;
	}
		
	protected Class createClass( Child target, Child tail ) {		
	    ClassGen clazz = new ClassGen(
	    	getClassName( target.getClass(), tail.getClass() ),
	    	getSuperClassName( target.getClass(), tail.getClass() ),
			getFileName( target.getClass(), tail.getClass() ),
			getAccessFlags( target.getClass(), tail.getClass() ),
			getInterfaceNames( target.getClass(), tail.getClass() ) );
		InstructionFactory factory = new InstructionFactory( clazz );

		addField( clazz, target.getClass().getName(), FjConstants.TARGET_NAME, Constants.ACC_PRIVATE );
		addField( clazz, tail.getClass().getName(), FjConstants.TAIL_NAME, Constants.ACC_PRIVATE );
		addField( clazz, ClassBasedDelegation.class.getName(), FjConstants.CLASS_BASED_DELEGATION_NAME, Constants.ACC_PRIVATE );
		addField( clazz, GeneratedDispatching.class.getName(), FjConstants.GENERATED_DISPATCHING_NAME, Constants.ACC_PRIVATE );
		addChildAccessor( clazz, FjConstants.GET_TARGET_METHOD_NAME, FjConstants.TARGET_NAME, target.getClass().getName(), factory );
		addChildAccessor( clazz, FjConstants.GET_TAIL_METHOD_NAME, FjConstants.TAIL_NAME, tail.getClass().getName(), factory );
		addInterfaces( clazz, tail.getClass().getInterfaces() );
		addConstructor( clazz, target.getClass().getName(), tail.getClass().getName(), factory );
		addMethodsAndFields( clazz, computeDispatchMethods( clazz, target, tail, factory ) );
		
		byte b[] = clazz.getJavaClass().getBytes();
		// uncomment the following statement for a 
		// harddisk copy of the generated class
		/*
		try {
			clazz.getJavaClass().dump( clazz.getClassName() + ".class" );
		} catch( Exception e ) {}
		*/
		return classLoader.publicDefineClass( clazz.getClassName(), b, 0, b.length );
	}
	public boolean isGeneratedClass( Class clazz ) {
		return classCache.containsKey( clazz.getName() );
	}
	protected String getClassName( Class childClass, Class parentClass ) {
		String s = "Class_fj_" + childClass.getName() + "_fj_" + parentClass.getName();
		return s;
	}
	protected Object getObjectKey( Object child, Object parent ) {
		class Holder {
			Object child;
			Object parent;
			Holder( Object child, Object parent ) {
				this.child = child;
				this.parent = parent;
			}
			public boolean equals(Object obj) {
				if( !(obj instanceof Holder) )
					return false;
				return ( ((Holder) obj).child == this.child && ((Holder) obj).parent == this.parent );				
			}
			public int hashCode() {
				return child.hashCode() ^ parent.hashCode();
			}
		}
		return new Holder( child, parent );
	}
	protected String getSuperClassName( Object child, Object parent ) {
		return "java.lang.Object";
	}
	protected String getFileName( Object child, Object parent ) {
		return FjConstants.STD_TOKEN_REFERENCE.getFile();
	}
	protected int getAccessFlags( Object child, Object parent ) {
		return Constants.ACC_PUBLIC | Constants.ACC_SUPER;
	}
	protected String getFieldTypeName( Object child ) {
		return child.getClass().getName();
	}
	protected String[] getInterfaceNames( Object child, Object parent ) {
		return new String[0];
	}
	protected void addField( ClassGen clazz, String type, String name, int accessFlags ) {
	    FieldGen field = new FieldGen(
	    	accessFlags,
	    	new ObjectType( type ), name, clazz.getConstantPool() );
	    clazz.addField( field.getField() );
	}
	protected void addInterfaces( ClassGen clazz, Class[] interfaces ) {
		for( int i = 0; i < interfaces.length; i++ ) {
			clazz.addInterface( interfaces[ i ].getName() );
		}
	}
	protected Method addConstructor(
		ClassGen clazz,
		String childTypeName,
		String parentTypeName,
		InstructionFactory factory
		) {
			
		ObjectType targetType = new ObjectType( childTypeName );
		ObjectType tailType = new ObjectType( parentTypeName );
		InstructionList instructions = new InstructionList();
			
	    MethodGen constructor = new MethodGen(
	    	Constants.ACC_PUBLIC   ,// access flags
			Type.VOID,              // return type
			new Type[] {            // argument types
				targetType,
				tailType,
				new ObjectType( ClassBasedDelegation.class.getName() ),
				new ObjectType( GeneratedDispatching.class.getName() )
			},
			new String[] { "target", "tail", "delegation", "dispatching" }, // arg names
			"<init>", // methodname
			clazz.getClassName(), // classname
			instructions,
			clazz.getConstantPool() );
	
	    instructions.append( InstructionConstants.THIS ); // Push `this'
	    instructions.append( factory.createInvoke(
	    	"java.lang.Object",
	    	"<init>",
	    	Type.VOID,
	    	Type.NO_ARGS,
	    	Constants.INVOKESPECIAL ) );

	    instructions.append( InstructionConstants.THIS );
	    instructions.append( InstructionConstants.ALOAD_1 );
	    instructions.append( factory.createFieldAccess(
	    	clazz.getClassName(),
	    	FjConstants.TARGET_NAME,
	    	targetType,
	    	Constants.PUTFIELD ) );

	    instructions.append( InstructionConstants.THIS );
	    instructions.append( InstructionConstants.ALOAD_2 );
	    instructions.append( factory.createFieldAccess(
	    	clazz.getClassName(),
	    	FjConstants.TAIL_NAME,
	    	tailType,
	    	Constants.PUTFIELD ) );
	    
	    instructions.append( InstructionConstants.THIS );
	    instructions.append( InstructionFactory.createLoad(
	    	new ObjectType( ClassBasedDelegation.class.getName() ), 3 ) );
	    instructions.append( factory.createFieldAccess(
	    	clazz.getClassName(),
	    	FjConstants.CLASS_BASED_DELEGATION_NAME,
	    	new ObjectType( ClassBasedDelegation.class.getName() ),
	    	Constants.PUTFIELD ) );
	    
	    instructions.append( InstructionConstants.THIS );
	    instructions.append( InstructionFactory.createLoad(
	    	new ObjectType( GeneratedDispatching.class.getName() ), 4 ) );
	    instructions.append( factory.createFieldAccess(
	    	clazz.getClassName(),
	    	FjConstants.GENERATED_DISPATCHING_NAME,
	    	new ObjectType( GeneratedDispatching.class.getName() ),
	    	Constants.PUTFIELD ) );
	    
	    instructions.append( InstructionConstants.RETURN );
	    constructor.setMaxStack();
	    Method m = constructor.getMethod();
	    instructions.dispose();
	    clazz.addMethod( m );
	    return m;
	}

	protected Method addChildAccessor(
		ClassGen clazz,
		String methodName,
		String fieldName,
		String objectTypeName,
		InstructionFactory factory
		) {
			
		Type objectType = new ObjectType( objectTypeName );
		Type childType = new ObjectType( FjConstants.CHILD_TYPE_NAME );
			
		InstructionList instructions = new InstructionList();			
	    MethodGen accessor = new MethodGen(
	    	Constants.ACC_PUBLIC, // access flags
			childType, // return type
			new Type[] {},            // argument types
			new String[] {}, // arg names
			methodName,
			clazz.getClassName(), // classname
			instructions,
			clazz.getConstantPool() );
	
		// push child-object
	    instructions.append( InstructionConstants.THIS );
	    instructions.append( factory.createFieldAccess(
	    	clazz.getClassName(),
	    	fieldName,
	    	objectType,
	    	Constants.GETFIELD ) );

	    // return
	    instructions.append( InstructionFactory.createReturn( childType ) );   
	    accessor.setMaxStack();
	    Method m = accessor.getMethod();
	    instructions.dispose();
	    clazz.addMethod( m );
	    return m;
	}

	protected Method[] addMethodsAndFields( ClassGen clazz, DispatchMethod[] methods ) {
		Method[] addedMethods = new Method[ methods.length ];
		for( int i = 0; i < methods.length; i++ ) {
			addedMethods[ i ] = methods[ i ].getMethod();
			clazz.addMethod( addedMethods[ i ] );
			addField(
				clazz,
				"java/lang/Object", 
				receiverFieldName( methods[ i ].getReflectMethod() ),
				Constants.ACC_PRIVATE );
		}
		return addedMethods;
	}
	protected DispatchMethod[] computeDispatchMethods( ClassGen clazz, Child target, Child tail, InstructionFactory factory ) {
		Set computedMethodSet = new HashSet();
		// the dispatch methods
		for( int i = 0; i < tail.getClass().getMethods().length; i++ ) {
			java.lang.reflect.Method currentMethod =
				tail.getClass().getMethods()[ i ];
			if( scipMethod( currentMethod ) )
				continue;
			else
				computedMethodSet.add(
					createDispatchMethod(
						clazz,
						currentMethod,
						target,
						tail,
						factory	) );
		}		
		DispatchMethod[] computedMethods = new DispatchMethod[ computedMethodSet.size() ];
		Iterator it = computedMethodSet.iterator();
		for( int i = 0; it.hasNext(); i++ ) {
			computedMethods[ i ] = (DispatchMethod) it.next();
		}
		return computedMethods;
	}
	protected DispatchMethod createDispatchMethod(
		ClassGen clazz,
		java.lang.reflect.Method method,
		Child target,
		Child tail,
		InstructionFactory factory ) {

		return new DispatchMethod(
			clazz,
			method,
			target.getClass(),
			tail.getClass(),
			factory );
	}	
	protected boolean scipMethod( java.lang.reflect.Method method ) {
		if( Modifier.isStatic( method.getModifiers() ) )
			return true;
		else if( method.getName().endsWith( FjConstants.SELFCONTEXT_METHOD_SUFFIX ) )
			return true;
		else if( method.getName().endsWith( FjConstants.IMPLEMENTATION_METHOD_SUFFIX ) )
			return true;
		else if( forbiddenMethods.contains( uniqueMethodId( method ) ) )
			return true;
		else
			return false;
	}
	protected String uniqueMethodId( java.lang.reflect.Method method ) {
	    StringBuffer methodId = new StringBuffer(method.getName());
	    Class parTypes[] = method.getParameterTypes();
	    for (int j=0; j<parTypes.length; j++) {
		    methodId.append("/");
			methodId.append(parTypes[j].getName());
	    }
	    return methodId.toString();
	}
	protected String receiverFieldName( java.lang.reflect.Method method ) {
		String name = uniqueMethodId( method ) + "_receiver";
		name = name.replace( '.', '$' );
		name = name.replace( '/', '_' );
		return name;
	}

	class DispatchMethod {
	
		MethodGen method;
		InstructionList instructions;
		java.lang.reflect.Method reflectMethod;
	
		public DispatchMethod(
			ClassGen clazz,
			java.lang.reflect.Method method,
			Class targetClass,
			Class tailClass,
			InstructionFactory factory ) {
				
			this.reflectMethod = method;
	
			// compute arguments
		    Type[] argTypes = getOwnArgTypes( method );
		    String[] argNames = getOwnArgNames( argTypes );
	
			// open the method
			instructions = new InstructionList();
		    this.method = new MethodGen(
		    	Constants.ACC_PUBLIC, // | Constants.ACC_FINAL,// access flags
				toType(method.getReturnType()),              // return type
				argTypes,
				argNames,
				getMethodName( method ),
				clazz.getClassName(), // classname
				instructions,
				clazz.getConstantPool() );
			
			// add exceptions
		    Class exceptions[] = method.getExceptionTypes();
		    for (int i = 0; i < exceptions.length; i++)
		    	this.method.addException(exceptions[i].getName());

			IF_ACMPNE ifBranch =
				addReceiverInitialization( clazz, method, instructions, targetClass.getName(), tailClass.getName(), factory );
	
			// push dispatch-object
		    ifBranch.setTarget( instructions.append( InstructionConstants.THIS ) );
		    instructions.append( factory.createFieldAccess(
		    	clazz.getClassName(),
		    	receiverFieldName( method ),
		    	new ObjectType( "java/lang/Object" ),
		    	Constants.GETFIELD ) );
	
			// push arguments
			appendSelfParameter( clazz, targetClass.getName(), method, instructions, factory );		
		    int j = getFirstArg() + 1;
		    for (int i = getFirstArg(); i < argTypes.length; i++) {
		     instructions.append( InstructionFactory.createLoad(argTypes[i],j));
		     j += argTypes[i].getSize();
		    }
		    
		    // invoke, only search in class' clean-interface
			Class dispatchClass = tailClass.getInterfaces()[ 0 ];
		    instructions.append( factory.createInvoke(
		    	getDeclaringClass( dispatchClass, resolveMethodName( method.getName() ), method ),
				resolveMethodName( method.getName() ),
		    	toType(method.getReturnType()),
		    	getForwardArgTypes( argTypes ),
		    	Constants.INVOKEINTERFACE ) );
		    	
		    // return
		    instructions.append( InstructionFactory.createReturn( toType( method.getReturnType() ) ) );   
		    this.method.setMaxStack();
		}	
		
		public Method getMethod() {
			Method m = method.getMethod();
			instructions.dispose();
			return m;
		}
		
		public java.lang.reflect.Method getReflectMethod() {
			return reflectMethod;
		}
		
		protected IF_ACMPNE addReceiverInitialization(
			ClassGen clazz,
			java.lang.reflect.Method method,
			InstructionList instructions,
			String targetClassName,
			String tailClassName,
			InstructionFactory factory ) {
				
		    Type objectType = new ObjectType( "java/lang/Object" );
		    Type childType = new ObjectType( FjConstants.CHILD_TYPE_NAME );
			
		    instructions.append( InstructionConstants.THIS ); // push receiver
		    instructions.append( factory.createFieldAccess(
		    	clazz.getClassName(),
		    	receiverFieldName( method ),
		    	objectType,
		    	Constants.GETFIELD ) );

		    instructions.append( InstructionFactory.createNull( objectType ) );	// push null

			IF_ACMPNE ifBranch = new IF_ACMPNE(null);	// receiver == null?
    		instructions.append(ifBranch);
    		
		    instructions.append( InstructionConstants.THIS );	// push this for putfield later

		    instructions.append( InstructionConstants.THIS );	// push delegation field
		    instructions.append( factory.createFieldAccess(
		    	clazz.getClassName(),
		    	FjConstants.CLASS_BASED_DELEGATION_NAME,
		    	new ObjectType( ClassBasedDelegation.class.getName() ),
		    	Constants.GETFIELD ) );

		    instructions.append( InstructionConstants.THIS );	// push target
		    instructions.append( factory.createFieldAccess(
		    	clazz.getClassName(),
		    	FjConstants.TARGET_NAME,
		    	new ObjectType( targetClassName ),
		    	Constants.GETFIELD ) );
		    	
		    instructions.append( InstructionConstants.THIS ); // push tail
		    instructions.append( factory.createFieldAccess(
		    	clazz.getClassName(),
		    	FjConstants.TAIL_NAME,
		    	new ObjectType( tailClassName ),
		    	Constants.GETFIELD ) );

			instructions.append(	// push the method to be called as string
				new PUSH( factory.getConstantPool(), uniqueMethodId( method ) ) );
		    
		    instructions.append( factory.createInvoke( // call delegation.M( Child, Child, String )
		    	ClassBasedDelegation.class.getName(),
				FjConstants.CLASS_BASED_DELEGATION_M,
		    	childType,
		    	new Type[] {
		    		childType,
		    		childType,
		    		new ObjectType( "java.lang.String" )
		    	},
		    	Constants.INVOKEVIRTUAL ) );
		    
		    instructions.append( factory.createFieldAccess( // set the result of M as receiver
		    	clazz.getClassName(),
		    	receiverFieldName( method ),
		    	objectType,
		    	Constants.PUTFIELD ) );

			return ifBranch;
		}		
		protected Type toType(Class c) {
			String sig;
			if (c.isArray()) {
				sig = c.getName().replace('.', '/');
			} else {
				sig = Utility.getSignature(c.getName());
			}
			return Type.getType(sig);
		}
		protected int getFirstArg() {
			return 0;
		}
		protected String getMethodName( java.lang.reflect.Method method ) {
			return method.getName();
		}
		protected Type[] getOwnArgTypes( java.lang.reflect.Method method ) {
		    Class paramTypes[] = method.getParameterTypes();
		    Type argTypes[] = new Type[ paramTypes.length ];
		    for (int i=0; i < argTypes.length; i++) {
		      argTypes[ i ] = toType( paramTypes[i] );
		    }
		    return argTypes;
		}
		protected String[] getOwnArgNames( Type[] argTypes ) {
		    String argNames[] = new String[ argTypes.length ];
		    for (int i=0; i < argTypes.length; i++) {
		      argNames[ i ] = "v" + (i+1);
		    }
		    return argNames;
		}
		protected Type[] getForwardArgTypes( Type[] argTypes ) {
		    Type forwardArgTypes[] = new Type[ argTypes.length + 1 ];
		    forwardArgTypes[ 0 ] = Type.OBJECT;
		    for (int i=0; i < argTypes.length; i++) {
		      forwardArgTypes[ i+1 ] = argTypes[ i ];
		    }
		    return forwardArgTypes;
		}
		protected String resolveMethodName( String methodName ) {
			return FjConstants.selfContextMethodName( methodName );
	
		}
		protected String getDeclaringClass(
			Class c,
			String methodName,
			java.lang.reflect.Method m) {

			try {
				Class params[] = m.getParameterTypes();
				Class extendedParams[] = new Class[params.length + 1];
				extendedParams[0] = Object.class;
				for (int i = 0; i < params.length; i++)
					extendedParams[i + 1] = params[i];
				java.lang.reflect.Method m2 = c.getMethod(methodName, extendedParams);
				java.lang.reflect.Method mTemp = m2;
				while( mTemp != null ) {
					m2 = mTemp;
					if( c.getInterfaces().length > 1 ) {
						c = c.getInterfaces()[1];
						try {
							mTemp = c.getMethod(methodName, extendedParams);
						} catch( NoSuchMethodException e ) {
							mTemp = null;
						}
					} else {
						mTemp = null;
					}
				}
				Class c2 = m2.getDeclaringClass();
				return c2.getName();
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
		}
		protected void appendSelfParameter(
			ClassGen clazz,
			String targetTypeName,
			java.lang.reflect.Method method,
			InstructionList instructions,
			InstructionFactory factory ) {
	
			Type theChildType = new ObjectType( FjConstants.CHILD_TYPE_NAME );
			Type objectType = new ObjectType( "java/lang/Object" );
			Type targetType = new ObjectType( targetTypeName );
	
		    instructions.append( InstructionConstants.THIS ); // push dispatching
		    instructions.append( factory.createFieldAccess(
		    	clazz.getClassName(),
		    	FjConstants.GENERATED_DISPATCHING_NAME,
		    	new ObjectType( GeneratedDispatching.class.getName() ),
		    	Constants.GETFIELD ) );
	
		    instructions.append( InstructionConstants.THIS ); // push _target
		    instructions.append( factory.createFieldAccess(
		    	clazz.getClassName(),
		    	FjConstants.TARGET_NAME,
		    	targetType,
		    	Constants.GETFIELD ) );

		    instructions.append( InstructionConstants.THIS ); // push receiver as new tail
		    instructions.append( factory.createFieldAccess(
		    	clazz.getClassName(),
		    	receiverFieldName( method ),
		    	objectType,
		    	Constants.GETFIELD ) );
	
		    instructions.append( factory.createInvoke( // invoke instance.get( ... )
		    	GeneratedDispatching.class.getName(),
		    	FjConstants.GENERATED_DISPATCHING_GET,
		    	Type.OBJECT,
				new Type[] { theChildType, theChildType },
		    	Constants.INVOKEVIRTUAL ) );		}
	}

	// makes protected defineClass() method public
	class DefiningClassLoader extends java.lang.ClassLoader {
		public DefiningClassLoader(java.lang.ClassLoader parent) {
			super(parent);
		}
		public Class publicDefineClass(String name, byte[] b, int off, int len)
			throws ClassFormatError {
			return super.defineClass(name, b, off, len);
		}
	}
}