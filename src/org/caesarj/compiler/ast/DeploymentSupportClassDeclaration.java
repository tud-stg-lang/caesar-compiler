package org.caesarj.compiler.ast;

import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * This class declaration is only for the generated deployment support classes.
 * 
 * @author Jürgen Hallpap
 */
public class DeploymentSupportClassDeclaration extends CaesarClassDeclaration {

	private CaesarClassDeclaration crosscuttingClass;

	private String postfix;

	public DeploymentSupportClassDeclaration(
		TokenReference where,
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
		JavaStyleComment[] comment,
		CaesarClassDeclaration crosscuttingClass,
		String postfix) {
		this(
			where,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment,
			PointcutDeclaration.EMPTY,
			AdviceDeclaration.EMPTY,
			null,
			crosscuttingClass,
			postfix);
	}

	public DeploymentSupportClassDeclaration(
		TokenReference where,
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
		JavaStyleComment[] comment,
		PointcutDeclaration[] pointcuts,
		AdviceDeclaration[] advices,
		CaesarDeclare[] declares,
	    CaesarClassDeclaration crosscuttingClass,
		String postfix) {

		super(
			where,
			modifiers,
			ident,
			typeVariables,
			superClass,
			null,
			null,
			null,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment,
			pointcuts,
			advices,
			declares);

		this.crosscuttingClass = crosscuttingClass;
		this.postfix = postfix;
	}

	/**
	 * Sets the superclass of this deployment class if needed.
	 */
	public void checkInterface(CContext context) throws PositionedError {

		//Add the superClass only to those classes, whose crosscuttingClass
		//has a crosscutting superClass
		if (crosscuttingClass.getSuperClass() != null
			&& (CModifier.contains(
				 crosscuttingClass.getSuperClass().getCClass().getModifiers(),
				 ACC_CROSSCUTTING)
				)
			) {
					
			String superClassName = null;
			// test klaus
			if (crosscuttingClass instanceof CaesarClassDeclaration) {
				
//				String sc = 
//				  crosscuttingClass.getSuperClass().getIdent();
//				superClassName = sc.substring(0,sc.length()  - FjConstants.PROXY_POSTFIX.length());
				superClassName = FjConstants.toIfcName(
					crosscuttingClass.getSuperClass().getIdent()) + postfix;
			} else {
			  superClassName =
				crosscuttingClass.getSuperClass().getIdent() + postfix;
			}				

			try {
				
				wouldBeSuperClass=(CReferenceType) new CClassNameType(
				superClassName.intern()).checkType(self);
				if(isRegistry()&&(!CModifier.contains(crosscuttingClass.getSuperClass().getCClass().getModifiers(),ACC_ABSTRACT))){
				
//					//setPointcuts(crosscuttingClass.getPointcuts());
					crosscuttingClass.setPointcuts(new PointcutDeclaration[0]);
					this.pointcuts=new PointcutDeclaration[0];
				}							
				if(isRegistry()&&
					(!CModifier.contains(
										crosscuttingClass.getModifiers(),
										ACC_ABSTRACT)
					)&&(!CModifier.contains(
										wouldBeSuperClass.getCClass().getModifiers(),
										ACC_ABSTRACT)
					))
						{
					callSuperClassToo(methods,"$deploy",superClassName);
					callSuperClassToo(methods,"$undeploy",superClassName);
			    		
			    		
				}else{
					superClass = wouldBeSuperClass;
					
				}
			} catch (UnpositionedError e) {
	
			context.reportTrouble(e.addPosition(getTokenReference()));
		}

			if(isRegistry()&&CModifier.contains(
										wouldBeSuperClass.getCClass().getModifiers(),
										ACC_ABSTRACT))
				pointcuts=crosscuttingClass.getPointcuts();
			
		}
		    
	super.checkInterface(context);
}

	private boolean isRegistry(){
		boolean isRegistry=false;
		for (int i=0; i<interfaces.length&&!isRegistry;i++){
			if(interfaces[i].getIdent().equals("AspectRegistry"))
				isRegistry=true;
		}
		return isRegistry;
	}
	
/**
 * weaves calls to the given Method in the superclass
 * @param method the method to call in the superClass
 * @param methodName the name, to check for errors
 * @param supeClassName where to weave
 */
	private void callSuperClassToo(JMethodDeclaration methods[],String methodName, String superClassName) {
		JMethodDeclaration method = null;
		for (int i = 0; i<methods.length; i++) {
			method = methods[i];
			if (method.getIdent() == methodName) break;	
		}
		if (method == null) throw new InconsistencyException("Method " + methodName + "not found");

		JStatement[] deployStatements=method.getBlockBody().getBody();
		JStatement[] newStatements = new JStatement[deployStatements.length + 1];
		System.arraycopy(deployStatements, 0, newStatements, 0, deployStatements.length);

		JExpression dprefix = new FjNameExpression(TokenReference.NO_REF, superClassName);
		JExpression fac= new FjFieldAccessExpression(TokenReference.NO_REF,dprefix, "ajc$perSingletonInstance");
		JFormalParameter[] dparameters = method.getArgs();
		JExpression[] args = new JExpression[dparameters.length];
		String argString="";
		for(int i=0;i<args.length;i++){
			 args[i]=new FjNameExpression(TokenReference.NO_REF,dparameters[i].getIdent());
			argString+=args[i]+", ";
		}		
		JExpression methodCall =
			new FjMethodCallExpression(TokenReference.NO_REF, fac, methodName, args);
//		System.out.println("weaved in: "+dprefix+methodName+"("+argString+")");
		newStatements[deployStatements.length] = new JExpressionStatement(TokenReference.NO_REF, methodCall, null);
		method.setBlockBody(new JBlock(TokenReference.NO_REF,newStatements,null));
	}
}
