/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: JClassDeclaration.java,v 1.4 2004-02-23 11:46:39 klose Exp $
 */

package org.caesarj.compiler.ast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.aspectj.CaesarScope;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.CciConstants;
import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.context.CVariableInfo;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.joinpoint.DeploymentPreparation;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.CWarning;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;
import org.caesarj.util.Utils;

/**
 * This class represents a caesarj class in the syntax tree. 
 * It now includes the CciClassDeclaration&FjClassDeclaration code. (Karl Klose)
 */
public class JClassDeclaration extends JTypeDeclaration implements CaesarConstants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class declaration node in the syntax tree.
   *
   * @param	where		the line of this node in the source code
   * @param	modifiers	the list of modifiers of this class
   * @param	ident		the simple name of this class
   * @param	superClass	the super class of this class
   * @param	interfaces	the interfaces implemented by this class
   * @param	fields		the fields defined by this class
   * @param	methods		the methods defined by this class
   * @param	inners		the inner classes defined by this class
   * @param	initializers	the class and instance initializers defined by this class
   * @param	javadoc		java documentation comments
   * @param	comment		other comments in the source code
   */

  public JClassDeclaration(TokenReference where,
			   int modifiers,
			   String ident,
                           CTypeVariable[] typeVariables,
			   CReferenceType superClass,
			   CReferenceType[] interfaces,
			   JFieldDeclaration[] fields,
			   JMethodDeclaration[] methods,
			   JTypeDeclaration[] inners,
			   JPhylum[] initializers,
			   JavadocComment javadoc,
			   JavaStyleComment[] comment)
  {
    super(where, modifiers, ident, typeVariables, interfaces, fields, methods, inners, initializers, javadoc, comment);
    this.superClass = superClass;
 }

 public JClassDeclaration(
	 TokenReference where,
	 int modifiers,
	 String ident,
	 CTypeVariable[] typeVariables,
	 CReferenceType superClass,
	 CReferenceType binding,
	 CReferenceType providing,
	 CReferenceType wrappee,
	 CReferenceType[] interfaces,
	 JFieldDeclaration[] fields,
	 JMethodDeclaration[] methods,
	 JTypeDeclaration[] inners,
	 JPhylum[] initializers,
	 JavadocComment javadoc,
	 JavaStyleComment[] comment)
 {
	 this(
		 where,
		 modifiers,
		 ident,
		 typeVariables,
		 superClass,
		 binding,
		 providing,
		 wrappee,
		 interfaces,
		 fields,
		 methods,
		 inners,
		 initializers,
		 javadoc,
		 comment,
		 PointcutDeclaration.EMPTY,
		 AdviceDeclaration.EMPTY,
		 null);
 }

 public JClassDeclaration(
	 TokenReference where,
	 int modifiers,
	 String ident,
	 CTypeVariable[] typeVariables,
	 CReferenceType superClass,
	 CReferenceType binding,
	 CReferenceType providing,
	 CReferenceType wrappee,
	 CReferenceType[] interfaces,
	 JFieldDeclaration[] fields,
	 JMethodDeclaration[] methods,
	 JTypeDeclaration[] inners,
	 JPhylum[] initializers,
	 JavadocComment javadoc,
	 JavaStyleComment[] comment,
	 PointcutDeclaration[] pointcuts,
	 AdviceDeclaration[] advices,
	 CaesarDeclare[] declares)
 {
	 super(where, modifiers, ident, typeVariables, interfaces, fields, methods, inners, initializers, javadoc, comment);
	 this.superClass = superClass;
	 this.providing = providing;
	 this.binding = binding;
	 this.wrappee = wrappee;
	 this.pointcuts = pointcuts;
	 this.advices = advices;
	 this.declares = declares;
	 // structural detection of crosscutting property
	 if ((advices.length > 0) || (pointcuts.length > 0))
		  this.modifiers |= ACC_CROSSCUTTING;
 }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Sets the super class
   */
  public void setSuperClass(CReferenceType superClass) {
    this.superClass = superClass;
  }

  /**
   * Sets the super class
   */
  public void setInterfaces(CReferenceType[] interfaces) {
    this.interfaces = interfaces;
  }


 


  /**
   * Check that initializers are correct
   * @exception	PositionedError	an error with reference to the source file
   */
  public void checkInitializers(CContext context) throws PositionedError {

  	//Walter start:
  	 //self = new CClassContext(context, context.getEnvironment(), sourceClass, this);
	 self = constructContext(context); 
  	 //Walter end
  	 

    if (assertMethod != null) {
      getCClass().addMethod(assertMethod.checkInterface(self));
      assertMethod.checkBody1(self);
    }

    compileStaticInitializer(self);

    // Check inners
    for (int i = inners.length - 1; i >= 0 ; i--) {
      inners[i].checkInitializers(self);
    }

    super.checkInitializers(context);
  }

  public void compileStaticInitializer(CClassContext context)
    throws PositionedError
  {
    if (statInit != null) {
      statInit.checkInitializer(context);

      // check that all final class fields are initialized
      CField[]	classFields = context.getCClass().getFields();

      for (int i = 0; i < classFields.length; i++) {
	if (classFields[i].isStatic() && !CVariableInfo.isInitialized(context.getFieldInfo(i))) {
	  check(context,
		!classFields[i].isFinal() || classFields[i].isSynthetic(),
		KjcMessages.UNINITIALIZED_FINAL_FIELD,
		classFields[i].getIdent());

	  context.reportTrouble(new CWarning(getTokenReference(),
					     KjcMessages.UNINITIALIZED_FIELD,
					     classFields[i].getIdent()));
	}
      }

      // mark all static fields initialized
      self.markAllFieldToInitialized(true);
    }
  }

  /**
   * Compiles the constructors of this class.
   *
   * @param	context		the analysis context
   * @return	the variable state after each constructor
   * @exception	PositionedError	an error with reference to the source file
   */
  private CVariableInfo[] compileConstructors(CContext context)
    throws PositionedError
  {
    JMethodDeclaration[]	constructors;
    CVariableInfo[]		variableInfos;

    // ------------------------------------------------------------------
    // create an array containing the constructors

    if (getDefaultConstructor() != null) {
      // if there is a default constructor, there are no other constructors.
      constructors = new JMethodDeclaration[1];
      constructors[0] = getDefaultConstructor();
    } else {
      int		count;

      // count the number of constructors ...
      count = 0;
      for (int i = 0; i < methods.length; i++) {
	if (methods[i] instanceof JConstructorDeclaration) {
	  count += 1;
	}
      }
      // ... and put them into an array
      constructors = new JMethodDeclaration[count];
      count = 0;
      for (int i = 0; i < methods.length; i++) {
	if (methods[i] instanceof JConstructorDeclaration) {
	  constructors[count] = methods[i];
	  count += 1;
	}
      }
    }

    // ------------------------------------------------------------------
    // compile each constructor

    variableInfos = new CVariableInfo[constructors.length];
    for (int i = 0; i < constructors.length; i++) {
      try {
	constructors[i].checkBody1(self);
     } catch (CBlockError e) {
	context.reportTrouble(e);
      }
    }

    // ------------------------------------------------------------------
    // mark all instance fields initialized

    self.markAllFieldToInitialized(false);

    // ------------------------------------------------------------------
    // check for cycles in constructor calls

    CMethod[]			callers;
    CMethod[]			callees;

    callers = new CMethod[constructors.length];
    callees = new CMethod[constructors.length];

    for (int i = 0; i < constructors.length; i++) {
      callers[i] = constructors[i].getMethod();
      callees[i] = ((JConstructorDeclaration)constructors[i]).getCalledConstructor();
    }

    for (int i = 0; i < constructors.length; i++) {
      if (callees[i] != null) {
	boolean		found = false;

	for (int j = 0; !found && j < constructors.length; j++) {
	  if (callees[i].equals(callers[j])) {
	    found = true;
	  }
	}
	if (! found) {
	  callees[i] = null;
	}
      }
    }

  _scan_:
    for (;;) {
      for (int i = 0; i < constructors.length; i++) {
	// find the first constructor that does not call
	// another constructor : it cannot be part of a
	// cycle
	if (callers[i] != null && callees[i] == null) {
	  // remove it as successor
	  for (int j = 0; j < constructors.length; j++) {
	    if (j != i && callees[j] != null && callees[j].equals(callers[i])) {
	      callees[j] = null;
	    }
	  }

	  // remove it
	  callers[i] = null;

	  // start search again
	  continue _scan_;
	}
      }

      // if we come here, nothing has been done
      break _scan_;
    }

    // if there are remaining constructors, they are part of a cycle
    for (int i = 0; i < constructors.length; i++) {
      if (callers[i] != null) {
	context.reportTrouble(new PositionedError(constructors[i].getTokenReference(),
						  KjcMessages.CYCLE_IN_CONSTRUCTOR_CALL));
	// signal only one
	break;
      }
    }

    return variableInfos;
  }

  /**
   * Constructs the default constructor with no arguments.
   */
  private JConstructorDeclaration constructDefaultConstructor(KjcEnvironment environment) {
    int         modifier;
    CClass      owner = getCClass();
    TypeFactory factory = environment.getTypeFactory();

    if (owner.isPublic()) {
      /* JLS 8.8.7 : If the class is declared public, then the default constructor 
         is implicitly given the access modifier public (?6.6); */
      modifier = ACC_PUBLIC;
    } else if (owner.isProtected()) {
      /* JLS 8.8.7 : If the class is declared protected, then the default 
         constructor is implicitly given the access modifier protected (?6.6); */
      modifier = ACC_PROTECTED;
    } else if (owner.isPrivate()) {
      /* JLS 8.8.7 : If the class is declared private, then the default constructor is 
         implicitly given the access modifier private (?6.6);*/
      modifier = ACC_PRIVATE;
    } else {
      /* JLS 8.8.7 : otherwise, the default constructor has the default 
         access implied by no  access modifier. */
      modifier = 0;
    }    
    return new JConstructorDeclaration(getTokenReference(),
				       modifier,
				       ident,
				       JFormalParameter.EMPTY,
				       CReferenceType.EMPTY,
				       new JConstructorBlock(getTokenReference(), null, new JStatement[0]),
				       null,
				       null,
                       factory);
  }

  /**
   * Collects all initializers and builds a single method.
   * @param	isStatic	class or instance initializers ?
   */
  protected JInitializerDeclaration constructInitializers(boolean isStatic, boolean always, TypeFactory factory) {
    ArrayList		elems = new ArrayList();
    boolean		needGen = false;

    for (int i = 0; i < body.length; i++) {
      if ((body[i] instanceof JClassBlock)
	  && (((JClassBlock)body[i]).isStaticInitializer() == isStatic)) {
	elems.add(body[i]);
	needGen = true;
      } else {
	if ((body[i] instanceof JFieldDeclaration)
	    && (((JFieldDeclaration)body[i]).getVariable().isStatic() == isStatic)) {
	  needGen |= ((JFieldDeclaration)body[i]).needInitialization();
	  elems.add(new JClassFieldDeclarator(getTokenReference(), (JFieldDeclaration)body[i]));
	}
      }
    }

    if (elems.size() > 0 || always) {
      JStatement[]	stmts = (JStatement[])elems.toArray(new JStatement[elems.size()]);

      return new JInitializerDeclaration(getTokenReference(),
					 new JBlock(getTokenReference(), stmts, null),
					 isStatic,
					 !needGen && !always,
                                         factory);
    } else {
      return null;
    }
  }


  public void analyseConditions()  throws PositionedError {
    // first super
    if (superClass != null) {
      superClass.getCClass().analyseConditions();
    }
    for (int i = 0; i < methods.length; i++) {
      methods[i].analyseConditions();
    }
    super.analyseConditions();
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    super.accept(p);

    p.visitClassDeclaration(this,
			    modifiers,
			    ident,
                            typeVariables,
			    superClass != null ? superClass.toString() : null,
			    interfaces,
			    body,
			    methods,
			    inners);
  }

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void genInnerJavaCode(KjcPrettyPrinter p) {
    super.accept(p);

    p.visitInnerClassDeclaration(this,
				 modifiers,
				 ident,
                 superClass != null ? superClass.toString() : null,
				 interfaces,
				 inners,
				 body,
				 methods);
  }
  
  public void setMethods(JMethodDeclaration[] methods) {
  	this.methods = methods;
  } 

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  // andreas start
  //private CReferenceType		superClass;
  protected CReferenceType		superClass;
  // andreas end
  
  protected CReferenceType		wouldBeSuperClass;
  
  //  private CClassContext		self;

  JMethodDeclaration            assertMethod;
  
  /* 
   * Integration of CciClassDeclaration code (Karl Klose)
   */
   
  /** 
   * The CI that the class binds.
   */
  protected CReferenceType binding;
  /** 
   * The CIs that the class provides.
   */
  protected CReferenceType providing;

  /** 
   * The reference of the wrappee.
   */
  protected CReferenceType wrappee;
		
  /**
   * The owner reference. It was pulled up.
   */
  protected JClassDeclaration ownerDecl;
	
  /**
   * Does it have super class?
   * @return boolean
   */
  public boolean hasSuperClass()
  {
	  return getSuperClass() != null
		  && ! (getSuperClass().getQualifiedName().equals(
				  FjConstants.CHILD_IMPL_TYPE_NAME))
		  && ! (getSuperClass().getQualifiedName().equals(
				  Constants.JAV_OBJECT));
  }

  /**
   * @return CReferenceType the Collaboration Interface which it binds.
   */
  public CReferenceType getBinding()
  {
	  return binding;
  }

  /**
   * @return CReferenceType the Collaboration Interface which it implements.
   */
  public CReferenceType getProviding()
  {
	  return providing;
  }

  /**
   * @return CReferenceType the Wrappee type.
   */
  public CReferenceType getWrappee()
  {
	  return wrappee;
  }
		
  /**
   * @return CReferenceType the super class of the class.
   */
  public CReferenceType getSuperClass()
  {
	  return superClass;
  }	
  
  /**
   * Returns the InnerClasses. This method was pulled up. 
   * @return JTypeDeclaration[]
   */
  public JTypeDeclaration[] getInners()
  {
	  return inners;
  }

  /**
   * Returns all constructors. This method was pulled up. 
   * @return FjConstructorDeclaration[]
   */
  protected FjConstructorDeclaration[] getConstructors()
  {
	  Vector contructors = new Vector(methods.length);
	  for (int i = 0; i < methods.length; i++)
	  {
		  if (methods[i] instanceof FjConstructorDeclaration)
			  contructors.add(methods[i]);
	  }
	  return (FjConstructorDeclaration[]) Utils.toArray(
		  contructors,
		  FjConstructorDeclaration.class);
  }

  /**
   * Returns the qualified type name of the binding.
   * @return String
   */
  public String getBindingTypeName()
  {
	  return ownerDecl != null 
			  ? ownerDecl.getBindingTypeName() + 
				  (binding == null ? "" : "$" + binding.toString())
			  : binding == null ? "" : binding.toString();
  }
  

  /**
   * Sets the owner declaration. This method was pulled up.
   * @param ownerDecl
   */
  public void setOwnerDeclaration(JClassDeclaration ownerDecl)
  {
	this.ownerDecl = ownerDecl; 
  }
	
  /**
   * Returns the owner declaration. This method was pulled up.
   * @return FjClassDeclaration
   */
  public JClassDeclaration getOwnerDeclaration()
  {
	  return ownerDecl;
  }
  /**
   * Returns the ident of the class
   * @return String
   */
  public String getIdent()
  {
	  return ident;
  }

  /**
   * Sets the inner classes.
   * This method was pulled up from FjClassDeclaration.
   * @param type
   */
  public void setInners(JTypeDeclaration[] inners)
  {
	  this.inners = inners;
  }

  /**
   * Appends an inner class.
   * This method was pulled up from FjClassDeclaration.
   * @param type
   */
/*  public void append(JTypeDeclaration type)
  {
	  JTypeDeclaration[] newInners = new JTypeDeclaration[inners.length + 1];
		
	  System.arraycopy(inners, 0, newInners, 0, inners.length);

	  newInners[inners.length] = type;
	  setInners(newInners);
  }
*/

  /**
   * Adds a method to the class. This method was pulled up. 
   * @param newMethod
   */
  public void addMethod(JMethodDeclaration newMethod)
  {
	  JMethodDeclaration[] newMethods =
		  new JMethodDeclaration[methods.length + 1];
	
	  System.arraycopy(methods, 0, newMethods, 0, methods.length);
	
	  newMethods[methods.length] = newMethod;
	
	  methods = newMethods;
  }

  /**
   * Adds a field in the class.
   * @param newField field to be inserted
   */
  public void addField(JFieldDeclaration newField)
  {
	  JFieldDeclaration[] newFields =
		  new JFieldDeclaration[fields.length + 1];
	
	  System.arraycopy(fields, 0, newFields, 0, fields.length);
	
	  newFields[fields.length] = newField;
	
	  fields = newFields;
  }

  /**
   * Adds fields in the class.
   * @param newFields fields to be inserted
   */
  public void addFields(ArrayList newFields)
  {
	  List tempList = Arrays.asList(fields);
	  ArrayList oldFields = new ArrayList(tempList.size() + newFields.size());
	  oldFields.addAll(tempList);
	  oldFields.addAll(newFields);
	  fields = 
		  (JFieldDeclaration[]) 
			  oldFields.toArray(new JFieldDeclaration[oldFields.size()]);
  }
  public void addMethods(ArrayList methodsToAdd)
  {
	  addMethods(
		  (JMethodDeclaration[])
			  methodsToAdd.toArray(
				  new JMethodDeclaration[methodsToAdd.size()]));

  }

  public void addMethods(JMethodDeclaration[] methodsToAdd)
  {
	  JMethodDeclaration[] newMethods =
		  new JMethodDeclaration[methods.length + methodsToAdd.length];

	  System.arraycopy(methods, 0, newMethods, 0, methods.length);
	  System.arraycopy(
		  methodsToAdd,
		  0,
		  newMethods,
		  methods.length,
		  methodsToAdd.length);

	  methods = newMethods;
  }
   /**
   * Resolves the collaboration interface passed as parameter.
   * Returns the ci checked.
   * @param context
   * @param ci
   * @return CReferenceType 
   * @throws PositionedError
   */
  protected CReferenceType resolveCollabortationInterface(
	  CContext context, CReferenceType ci)
	  throws PositionedError		
  {
	  try
	  {
		  ci = (CReferenceType) ci.checkType(context);
	  }
	  catch (UnpositionedError e)
	  {
		  if (e.getFormattedMessage().getDescription()
			  != KjcMessages.CLASS_AMBIGUOUS)
			  throw e.addPosition(getTokenReference());
					
		  CClass[] candidates = (CClass[]) 
			  e.getFormattedMessage().getParams()[1];
				
		  ci = candidates[0].getAbstractType();
	  }
	  return ci;	
  }
	
  /**
   * Transforms the inner classes in overriden types. The current class
   * must be a providing class (getProviding() != null).
   * @return JTypeDeclaration[] the new nested classes.
   */
  public JTypeDeclaration[] transformInnerProvidingClasses()
  {
	  for (int i = 0; i < inners.length; i++)
	  {
		  if (inners[i] instanceof JClassDeclaration)
		  {
			  inners[i] = 
				  ((JClassDeclaration)inners[i]) 
				    .createOverrideClassDeclaration(this);
		  }
	  }
	  return inners;
  }
	
  /**
   * Transforms the inner classes which bind some CI in virtual types. 
   * The current class must be a binding class (getBinding() != null).
   * @return JTypeDeclaration[] the new nested classes.
   */
  public JTypeDeclaration[] transformInnerBindingClasses(
	  JClassDeclaration owner)
  {
	  for (int i = 0; i < inners.length; i++)
	  {
		  if (inners[i] instanceof JClassDeclaration)
		  {
			  JClassDeclaration innerClass = (JClassDeclaration)inners[i];
			  if (innerClass.getBinding() != null)
			  {
				  innerClass.setOwnerDeclaration(owner);
				  inners[i] = innerClass.createVirtualClassDeclaration(
					  owner);
			  }
			  else
				  innerClass.transformInnerBindingClasses(this);
		  }
	  }
	  return inners;
  }		
  /**
   * Creates an override type. This is done when the compiler finds a 
   * providing class, so it has to change its inners for an overriding classe.
   * @param owner
   * @return FjOverrideClassDeclaration
   */
  public FjOverrideClassDeclaration createOverrideClassDeclaration(
	  JClassDeclaration owner)
  {
	  providing = new CClassNameType(owner.getProviding().getQualifiedName() 
		  + "$" + ident);
	  return 
		  new FjOverrideClassDeclaration(
			  getTokenReference(),
			  modifiers | CCI_PROVIDING,
			  ident,
			  typeVariables,
			  null,
			  null,
			  providing,
			  wrappee,
			  interfaces,
			  fields,
			  methods,
			  transformInnerProvidingClasses(),
			  this.body,
			  null,
			  null);
  }


  /**
   * Creates an virtual type. This is done when the compiler finds a 
   * binding class, so it has to change its inners for virtual classes.
   * @param owner
   * @return FjOverrideClassDeclaration
   */
  public FjVirtualClassDeclaration createVirtualClassDeclaration(
	  JClassDeclaration owner)
  {
	  String superClassName = getBindingTypeName();

	  FjVirtualClassDeclaration result =
		  new FjVirtualClassDeclaration(
			  getTokenReference(),
			  (modifiers | CCI_BINDING) & ~FJC_CLEAN,
			  ident,
			  typeVariables,
			  new CClassNameType(superClassName),
			  new CClassNameType(superClassName),
			  null,
			  wrappee,
			  interfaces,
			  fields,
			  methods,
			  transformInnerBindingClasses(this),
			  this.body,
			  null,
			  null);

	  result.addProvidingAcessor();
		
	  return result;
  }	

  /**
   * Adds the providing reference accessor. The class must be a binding class.
   * The method will actually return a dispatcher of self in this context.
   */
  public void addProvidingAcessor()
  {
	  TokenReference ref = getTokenReference();
	  //Adds the implementation accessor.
	  addMethod(
		  createAccessor(
			  CciConstants.PROVIDING_REFERENCE_NAME,
			  new JMethodCallExpression(
				  ref,
				  new JThisExpression(ref),
				  FjConstants.GET_DISPATCHER_METHOD_NAME,
				  new JExpression[]
				  {
					  new FjNameExpression(
							  ref,
							  FjConstants.SELF_NAME)
				  }),
			  FjConstants.CHILD_TYPE));	
  }
	

  /**
   * Creates an accessor method.
   * @param accessedName The name to be accessed
   * @param returnExpression The return expression
   * @param returnType The return type
   * @return FjCleanMethodDeclaration
   */
  protected FjCleanMethodDeclaration createAccessor(
	  String accessedName, 
	  JExpression returnExpression, 
	  CReferenceType returnType)
  {
	  JStatement[] statements =
		  new JStatement[] {
			   new JReturnStatement(
				  getTokenReference(),
				  returnExpression,
				  null)};
					
	  JBlock body = new JBlock(getTokenReference(), statements, null);
	
	  return new FjCleanMethodDeclaration(
		  getTokenReference(),
		  ACC_PUBLIC,
		  new CTypeVariable[0],
		  returnType,
		  CciConstants.toAccessorMethodName(accessedName),
		  new JFormalParameter[0],
		  new CReferenceType[0],
		  body,
		  null,
		  null);
  }
	
  /* DEBUG
   * (non-Javadoc)
   * @see at.dms.kjc.JTypeDeclaration#print()
   */
  public void print()
  {
	  System.out.print(CModifier.toString(modifiers));
	  System.out.print("class ");
	  super.print();
	  if (superClass != null)
		  System.out.print(" extends " + superClass );
	  if (interfaces.length > 0)
	  {
		  System.out.print(" implements ");
		  for (int i = 0; i < interfaces.length; i++)
		  {
			  if (i > 0)
				  System.out.print(", ");
					
			  System.out.print(interfaces[i]);
		  }
	  }
		
	  if (providing != null)
	  {
		  System.out.print(" provides ");

		  System.out.print(providing);
	  }
			
		
	  if (binding != null)
	  {
		  System.out.print(" binds ");
		  System.out.print(binding);
	  }
		
	  System.out.println();
  }
	/*
	 * Integration of FjClassDeclaration (Karl Klose)
	 */

	/** The declared advices */
	protected AdviceDeclaration[] advices;

	/** e.g. declare precedence */
	protected CaesarDeclare[] declares;

	/** e.g. perSingleton, perCflow,..*/
	protected CaesarPointcut perClause;

	/** The declared pointcuts */
	protected PointcutDeclaration[] pointcuts;

	public JMethodDeclaration[] getMethods()
	{
		return methods;
	}

	protected void checkModifiers(final CContext context)
		throws PositionedError
	{
		int modifiers = getModifiers();

		// Syntactically valid class modifiers
		check(context, CModifier.isSubsetOf(modifiers, getAllowedModifiers()),
			KjcMessages.NOT_CLASS_MODIFIERS,
			CModifier.toString(CModifier.notElementsOf(modifiers, 
				getAllowedModifiers())));
		// FJLS 1 : modifiers virtual and override pertain only to member classes
		check(
			context,
			!(CModifier.contains(modifiers, FJC_VIRTUAL)
				|| CModifier.contains(modifiers, FJC_OVERRIDE))
				|| isNested() & CModifier.contains(modifiers, FJC_VIRTUAL)
				|| isNested() & CModifier.contains(modifiers, FJC_OVERRIDE),
			CaesarMessages.MODIFIERS_INNER_CLASSES_ONLY,
			CModifier.toString(
				CModifier.getSubsetOf(modifiers, FJC_VIRTUAL | FJC_OVERRIDE)));
		// andreas end

		// JLS 8.1.1 : The access modifier public pertains only to top level
		// classes and to member classes.
		check(
			context,
			(!isNested() || !(context instanceof CBodyContext))
				|| !CModifier.contains(modifiers, ACC_PUBLIC),
			KjcMessages.INVALID_CLASS_MODIFIERS,
			CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_PUBLIC)));

		// JLS 8.1.1 : The access modifiers protected and private pertain only to
		// member classes within a directly enclosing class declaration.
		check(
			context,
			(isNested()
				&& getOwner().getCClass().isClass()
				&& !(context instanceof CBodyContext))
				|| !CModifier.contains(modifiers, ACC_PROTECTED | ACC_PRIVATE),
			KjcMessages.INVALID_CLASS_MODIFIERS,
			CModifier.toString(
				CModifier.getSubsetOf(modifiers, ACC_PROTECTED | ACC_PRIVATE)));

		// JLS 8.1.1 : The access modifier static pertains only to member classes.
		check(
			context,
			isNested() || !CModifier.contains(modifiers, ACC_STATIC),
			KjcMessages.INVALID_CLASS_MODIFIERS,
			CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_STATIC)));

		// JLS 8.1.1.2 : A compile-time error occurs if a class is declared both
		// final and abstract.
		check(
			context,
			CModifier.getSubsetSize(modifiers, ACC_FINAL | ACC_ABSTRACT) <= 1,
			KjcMessages.INCOMPATIBLE_MODIFIERS,
			CModifier.toString(
				CModifier.getSubsetOf(modifiers, ACC_FINAL | ACC_ABSTRACT)));

		// JLS 9.5 : A member type declaration in an interface is implicitly
		// static and public.
		if (isNested() && getOwner().getCClass().isInterface())
		{
			setModifiers(modifiers | ACC_STATIC | ACC_PUBLIC);
		}
		if (getCClass().isNested()
			&& getOwner().getCClass().isClass()
			&& !getCClass().isStatic()
			&& context.isStaticContext())
		{
			setModifiers(modifiers | ACC_STATIC);
		}
	}

	public void generateInterface(
		ClassReader classReader,
		CClass owner,
		String prefix)
	{
		sourceClass =
			new FjSourceClass(
				owner,
				getTokenReference(),
				modifiers,
				ident,
				prefix + ident,
				typeVariables,
				isDeprecated(),
				false,
				this,
				perClause);

		setInterface(sourceClass);

		CReferenceType[] innerClasses = new CReferenceType[inners.length];
		for (int i = 0; i < inners.length; i++)
		{
			inners[i].generateInterface(
				classReader,
				sourceClass,
				sourceClass.getQualifiedName() + "$");
			innerClasses[i] = inners[i].getCClass().getAbstractType();
		}

		sourceClass.setInnerClasses(innerClasses);
		uniqueSourceClass = classReader.addSourceClass(sourceClass);
	}

	public void append(JTypeDeclaration type)
	{
		JTypeDeclaration[] newInners =
			(JTypeDeclaration[]) Array.newInstance(
				JTypeDeclaration.class,
				inners.length + 1);
		System.arraycopy(inners, 0, newInners, 0, inners.length);
		newInners[inners.length] = type;
		setInners(newInners);
	}

	protected JTypeDeclaration getCleanInterfaceOwner()
	{
		return this;
	}
	
	public CTypeContext getTypeContext()
	{
		return self;
	}
	
	private JPhylum[] cacheInitializers;
	protected JPhylum[] getInitializers()
	{
		return cacheInitializers;
	}
	private JavadocComment cacheJavadoc;
	protected JavadocComment getJavadoc()
	{
		return cacheJavadoc;
	}
	private JavaStyleComment[] cacheComment;
	protected JavaStyleComment[] getComment()
	{
		return cacheComment;
	}

	/** 
	 * In the pass the superclass of this class the interfaces must be set, 
	 * so that they are  available for the next pass.
	 * It is not possible to check the interface of methods, fields, ... in 
	 * the same pass.
	 */
	public void join(CContext context) throws PositionedError
	{
		try
		{
			CReferenceType      objectType;

			objectType = context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT);

			// construct the CClassContext; should be the first thing!
			if (self == null) {
			  self = constructContext(context);
			}

			if (superClass == null) {
			  if (sourceClass.getQualifiedName() == JAV_OBJECT) {
			// java/lang/Object
			// superClass = null;
			// superClass1 = null;
			  } else {
			superClass = objectType;
			  }
			} else {
			  try {
			superClass =(CReferenceType) superClass.checkType(self);
			  } catch (UnpositionedError e) {
			throw e.addPosition(getTokenReference());
			  }
			}

			// check access
			if (superClass != null) {
			  CClass	clazz = superClass.getCClass();

			  check(context, 
					clazz.isAccessible(getCClass()),
				KjcMessages.CLASS_ACCESSPARENT, superClass.getQualifiedName());
			  check(context,
				!clazz.isFinal(),
				KjcMessages.CLASS_PARENT_FINAL, superClass.getQualifiedName());
			  check(context,
				!clazz.isInterface(),
				KjcMessages.CLASS_EXTENDS_INTERFACE, superClass.getQualifiedName());
			}
			sourceClass.setSuperClass(superClass);

			super.join(context);
		}
		catch (PositionedError e)
		{
			// non clean classes may not inherrit
			// clean, virtual or override classes
			if (e.getFormattedMessage().getDescription()
				== KjcMessages.CLASS_EXTENDS_INTERFACE)
			{
				String ifcName =
					e.getFormattedMessage().getParams()[0].toString();
				FjTypeSystem fjts = new FjTypeSystem();
				if (fjts.isCleanIfc(context, getSuperClass().getCClass()))
					throw new PositionedError(
						getTokenReference(),
						CaesarMessages.NON_CLEAN_INHERITS_CLEAN,
						ifcName);
			}
			if (e.getFormattedMessage().getDescription()
				== KjcMessages.TYPE_UNKNOWN
				&& !(this instanceof FjCleanClassDeclaration))
			{

				JTypeDeclaration ownerDecl = getOwnerDeclaration();
				CType familyType = null;
				if (ownerDecl != null)
				{
					String superName = getSuperClass().toString();
					FjTypeSystem fjts = new FjTypeSystem();
					String[] splitName = fjts.splitQualifier(superName);
					if (splitName != null)
					{
						String qualifier = splitName[0];
						String remainder = splitName[1];
						JFieldDeclaration familyField = null;
						int i = 0;
						for (; i < ownerDecl.getFields().length; i++)
						{
							familyField = ownerDecl.getFields()[i];
							if (familyField
								.getVariable()
								.getIdent()
								.equals(qualifier))
							{
								familyType =
									familyField.getVariable().getType();
								break;
							}
						}
						if (familyType != null)
						{
							try
							{
								familyType = familyType.checkType(context);
								if (familyType.isReference())
									new CClassNameType(
										familyType
											.getCClass()
											.getQualifiedName()
											+ "$"
											+ remainder).checkType(
										context);
								// a virtual type is referenced!
								throw new PositionedError(
									getTokenReference(),
									CaesarMessages.MUST_BE_VIRTUAL,
									getIdent());
							}
							catch (UnpositionedError e2)
							{
							}
						}
					}
				}
			}
			throw e;
		} 

	}

	/**
	 * Resolves the binding and providing references. Of course it calls the
	 * super implementation of the method also.
	 */
	public void checkInterface(CContext context) throws PositionedError
	{

		// register type at CaesarBcelWorld!!!
		CaesarBcelWorld.getInstance().resolve(getCClass());

		//statically deployed classes are considered as aspects
		if (isStaticallyDeployed())
		{
			DeploymentPreparation.prepareForStaticDeployment(context, (JClassDeclaration)this);

			modifiers |= ACC_FINAL;
		}


		checkModifiers(context); 

		statInit = constructInitializers(true,
										 context.getEnvironment().getSourceVersion() >= KjcEnvironment.SOURCE_1_4,
										 context.getTypeFactory());
//	   lackner 11-11-01: done in JTypeDeclaration 
//		   if (statInit != null) {
//			 statInit.checkInterface(self);
//		   }

		int	i = 0;
		for (; i < methods.length; i++) {
		  if (methods[i] instanceof JConstructorDeclaration) {
		break;
		  }
		}
		if (i == methods.length && getDefaultConstructor() == null) {
		  setDefaultConstructor(constructDefaultConstructor(context.getEnvironment()));
		}

		instanceInit = constructInitializers(false,
											 false,
											 context.getTypeFactory());
		if (instanceInit != null) {
		  instanceInit.checkInterface(self);
		}
	
		if (getCClass().getSuperClass() != null) {
		  check(context,
				!getCClass().getSuperClass().descendsFrom(getCClass()),
			KjcMessages.CLASS_CIRCULARITY,
			ident);
		}
		super.checkInterface(context, superClass);
    
		// Check inners
		for (int k = 0; k  < inners.length; k++) {
		  if (!inners[k].getCClass().isStatic()) {
			inners[k].addOuterThis();
		  }
		}

		if (binding != null)
			binding = resolveCollabortationInterface(context, binding);
	
		if (providing != null)
			providing = resolveCollabortationInterface(context, providing);


		if (isPrivileged() || isStaticallyDeployed())
		{
			getFjSourceClass().setPerClause(
				CaesarPointcut.createPerSingleton()
				);
		}

		//ckeckInterface of the pointcuts
		for (int j = 0; j < pointcuts.length; j++)
		{
			pointcuts[j].checkInterface(self);
		}
	}

	public FjSourceClass getFjSourceClass()
	{
		return (FjSourceClass) sourceClass;
	}

	/**
	 * Does the class have a clean interface?
	 * @return
	 */
	public boolean isClean()
	{
		return (modifiers & (FJC_CLEAN | FJC_VIRTUAL | FJC_OVERRIDE)) != 0;
	}


	public boolean isPrivileged()
	{
		return (modifiers & ACC_PRIVILEGED) != 0;
	}


	public boolean isStaticallyDeployed()
	{
		return (modifiers & ACC_DEPLOYED) != 0;
	}

	public void setFields(JFieldDeclaration[] fields)
	{
		this.fields = fields;
	}

	public PointcutDeclaration[] getPointcuts()
	{
		return pointcuts;
	}

	public AdviceDeclaration[] getAdvices()
	{
		return advices;
	}

	public void setPointcuts(PointcutDeclaration[] pointcuts)
	{
		this.pointcuts = pointcuts;
	}

	public void setAdvices(AdviceDeclaration[] advices)
	{
		this.advices = advices;
	}

	/**
	 * Returns the precedenceDeclaration.
	 * @return Declare
	 */
	public CaesarDeclare[] getDeclares()
	{
		return declares;
	}

	/**
	 * Sets the precedenceDeclaration.
	 * @param precedenceDeclaration The precedenceDeclaration to set
	 */
	public void setDeclares(CaesarDeclare[] declares)
	{
		this.declares = declares;
	}

	/**
	 * Sets the perClause.
	 * @param perClause The perClause to set
	 */
	public void setPerClause(CaesarPointcut perClause)
	{
		this.perClause = perClause;
	}

	public boolean isCrosscutting() {
		return CModifier.contains(modifiers, ACC_CROSSCUTTING);
	}

	/**
	 * Initilizes the family in the class. It does almost everything that is
	 * done during the checkInterface again.
	 * 
	 * @param context
	 * @throws PositionedError
	 */
	public void initFamilies(CClassContext context) throws PositionedError
	{
		int generatedFields = getCClass().hasOuterThis() ? 1 : 0;

		//Initializes the families of the fields.
		Hashtable hashField =
			new Hashtable(fields.length + generatedFields + 1);
		for (int i = fields.length - 1; i >= 0; i--)
		{
			CSourceField field =
				((FjFieldDeclaration) fields[i]).initFamily(context);

			field.setPosition(i);

			hashField.put(field.getIdent(), field);
		}
		if (generatedFields > 0)
		{
			CSourceField field = outerThis.checkInterface(self);

			field.setPosition(hashField.size());

			hashField.put(JAV_OUTER_THIS, field);
		}

		int generatedMethods = 0;

		if (getDefaultConstructor() != null)
			generatedMethods++;

		if (statInit != null)
			generatedMethods++;

		if (instanceInit != null)
			generatedMethods++;

		// Initializes the families of the methods.
		CMethod[] methodList = new CMethod[methods.length + generatedMethods];
		int i = 0;
		for (; i < methods.length; i++)
		{
			if (methods[i] instanceof FjMethodDeclaration)
				methodList[i] =
					((FjMethodDeclaration) methods[i]).initFamilies(context);
			else
				methodList[i] = methods[i].getMethod();

		}

		JConstructorDeclaration defaultConstructor = getDefaultConstructor();
		if (defaultConstructor != null)
		{
			if (defaultConstructor instanceof FjConstructorDeclaration)
				methodList[i++] =
					((FjConstructorDeclaration) defaultConstructor)
								.initFamilies(context);
			else
				methodList[i++] = defaultConstructor.getMethod();
		}
		if (statInit != null)
			methodList[i++] = statInit.getMethod();
		
		if (instanceInit != null)
			methodList[i++] = instanceInit.getMethod();

		sourceClass.close(
			interfaces,
			sourceClass.getSuperType(),
			hashField,
			methodList);
		

		//ckeckInterface of the advices
		for (int j = 0; j < advices.length; j++)
		{
			advices[j].checkInterface(self);
			//during the following compiler passes
			//the advices should be treated like methods
			getFjSourceClass().addMethod((CaesarAdvice) advices[j].getMethod());
		}

		//consider declares
		if (declares != null)
		{
			for (int j = 0; j < declares.length; j++)
			{
				declares[j].resolve(
					new CaesarScope(
						(FjClassContext) constructContext(context),
						getFjSourceClass()));
			}

			getFjSourceClass().setDeclares(declares);
		}		
	}

	public void append(JMethodDeclaration newMethod)
	{
		Vector methods = new Vector(Arrays.asList(this.methods));
		methods.add(newMethod);
		this.methods =
			(JMethodDeclaration[]) Utils.toArray(
				methods,
				JMethodDeclaration.class);
	}

	public void addClassBlock(JClassBlock initializerDeclaration)
	{
		JPhylum[] newBody = new JPhylum[body.length + 1];
		System.arraycopy(body, 0, newBody, 0, body.length);
		newBody[body.length] = initializerDeclaration;
		body = newBody;
	}

	public void addInterface(CReferenceType newInterface)
	{
		CReferenceType[] newInterfaces =
			new CReferenceType[interfaces.length + 1];

		System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
		newInterfaces[interfaces.length] = newInterface;

		interfaces = newInterfaces;
	}
	
	/**
	 * checkTypeBody
	 * Check expression and evaluate and alter context
	 * @param context the actual context of analyse
	 * @return  a pure java expression including promote node
	 * @exception	PositionedError	an error with reference to the source file
	 */
	public void checkTypeBody(CContext context) throws PositionedError
	{

		if (advices != null)
		{
			for (int i = 0; i < advices.length; i++)
			{
				advices[i].checkBody1(self);
			}
		}

		// JSR 41 2.2
		// A parameterized type may not inherit directly or indirectly form 
		// java.lang.Throwable
		check(self,!getCClass().isGenericClass() || !getCClass().descendsFrom(context.getTypeFactory().createReferenceType(TypeFactory.RFT_THROWABLE).getCClass()), KjcMessages.GENERIC_THROWABLE); 
            
		if (getCClass().isNested() && getOwner().getCClass().isClass() 
			&& !getCClass().isStatic() && !context.isStaticContext()) {
		  addOuterThis();
		}
		try {
		  CVariableInfo	instanceInfo = null;
		  CVariableInfo[]	constructorsInfo;

		  if (instanceInit != null) {
		instanceInit.checkInitializer(self);
		  }

		  for (int i = fields.length - 1; i >= 0 ; i--) {
			((CSourceField)fields[i].getField()).setFullyDeclared(true);
		  }

		  for (int i = 0; i < inners.length; i++) {
		try {
		  inners[i].checkTypeBody(self);
		} catch (CBlockError e) {
		  context.reportTrouble(e);
		}
		  }

		  // First we compile constructors
		  constructorsInfo = compileConstructors(context);

		  // Now we compile methods
		  for (int i = methods.length - 1; i >= 0 ; i--) {
		try {
		  if (!(methods[i] instanceof JConstructorDeclaration)) {
			methods[i].checkBody1(self);
		  }
		} catch (CBlockError e) {
		  context.reportTrouble(e);
		}
		  }

		  // Now we check members
		  for (int i = methods.length - 1; i >= 0 ; i--) {
		if (!((CSourceMethod)methods[i].getMethod()).isUsed() &&
			!methods[i].getMethod().getIdent().equals(JAV_CONSTRUCTOR)) {
		  context.reportTrouble(new CWarning( methods[i].getTokenReference(),
							 KjcMessages.UNUSED_PRIVATE_METHOD,
							 methods[i].getMethod().getIdent()));
		}
		  }
		  for (int i = fields.length - 1; i >= 0 ; i--) {
		if (!((CSourceField)fields[i].getField()).isUsed()) {
		  context.reportTrouble(new CWarning(fields[i].getTokenReference(),
							 KjcMessages.UNUSED_PRIVATE_FIELD,
							 fields[i].getField().getIdent()));
		}
		  }

		  self.close(this, null, null, null);
		  super.checkTypeBody(context);

		} catch (UnpositionedError cue) {
		  throw cue.addPosition(getTokenReference());
		}

		self = null;
	}

	/**
	 * Constructs the class context.
	 */
	protected CClassContext constructContext(CContext context)
	{
		return new FjClassContext(
			context,
			context.getEnvironment(),
			sourceClass,
			this);
	}

  /**
   * @return An int with all modifiers allowed for classes.
   */	
  protected int getAllowedModifiers()
  {
	  return ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE | 
		  	  ACC_ABSTRACT | ACC_STATIC | ACC_FINAL | ACC_STRICT 
			| FJC_VIRTUAL
			| FJC_OVERRIDE
			| FJC_CLEAN
			| ACC_CROSSCUTTING  // Klaus
			| getInternalModifiers();
	}
	
	protected int getInternalModifiers()
	{
		return  CCI_COLLABORATION
				| CCI_BINDING
				| CCI_PROVIDING
				| CCI_WEAVELET
				//Jurgen's
				| ACC_PRIVILEGED 
				| ACC_CROSSCUTTING 
				| ACC_DEPLOYED;
	}

}
