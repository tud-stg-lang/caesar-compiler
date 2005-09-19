/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
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
 */
package org.caesarj.compiler.aspectj;

import java.util.ArrayList;

import org.aspectj.weaver.Member;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.patterns.ArgsPointcut;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.HandlerPointcut;
import org.aspectj.weaver.patterns.IToken;
import org.aspectj.weaver.patterns.ITokenSource;
import org.aspectj.weaver.patterns.ModifiersPattern;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.patterns.ParserException;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.SignaturePattern;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.ThrowsPattern;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;
import org.aspectj.weaver.patterns.WildTypePattern;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.patterns.WithincodePointcut;

/**
 * Extends the PatternParser to generate Wrappers for Pointcut objects
 * instead of the objects themselves. It also performs the registration
 * of parsed pointcuts to the CaesarPointcutScope class, so that more 
 * information about the pointcut can be used when resolving them.
 *
 * @author Thiago Tonelli Bartolomei <thiagobart@gmail.com>
 *
 */
public class CaesarWrapperPatternParser extends PatternParser {

	/**
	 * Store the tokenSource too
	 */
	private ITokenSource tokenSource = null;
	
	/**
	 * Constructor for PatterParserWrapper.
	 * Just calls super.
	 * 
	 * @param tokenSource the tokensource object
	 */
	public CaesarWrapperPatternParser(ITokenSource tokenSource) {
		super(tokenSource);
		this.tokenSource = tokenSource;
	}
	
    // ----------------------------------------------------------------------
    // CODE COPIED FROM ASPECTJ'S PatternParser AND MODIFIED TO GENERATE WRAPPERS
    // ----------------------------------------------------------------------
	
	public CaesarPointcutWrapper parsePointcutWrapper() {		
		int start = tokenSource.getIndex();
		IToken t = tokenSource.peek();
		Pointcut p = t.maybeGetParsedPointcut();
		if (p != null) {
			tokenSource.next();
			return new CaesarPointcutWrapper(p);
		}
		
		String kind = parseIdentifier();
		tokenSource.setIndex(start);
		if (kind.equals("execution") || kind.equals("call") || 
						kind.equals("get") || kind.equals("set")) {
			return parseKindedPointcut();
		} else if (kind.equals("args")) {
			return parseArgsPointcut();
		} else if (kind.equals("this") || kind.equals("target")) {
			return parseThisOrTargetPointcut();
		} else if (kind.equals("within")) {
			return parseWithinPointcut();
		} else if (kind.equals("withincode")) {
			return parseWithinCodePointcut();
		} else if (kind.equals("cflow")) {
			return parseCflowPointcut(false);
		} else if (kind.equals("cflowbelow")) {
			return parseCflowPointcut(true);
		} else  if (kind.equals("adviceexecution")) {
			parseIdentifier(); eat("(");
			eat(")");
			
			// Creates the wrapper 
			CaesarKindedPointcut pointcut = new CaesarKindedPointcut(Shadow.AdviceExecution,
					new SignaturePattern(Member.ADVICE, ModifiersPattern.ANY, 
							TypePattern.ANY, TypePattern.ANY, NamePattern.ANY, 
							TypePatternList.ANY, 
							ThrowsPattern.ANY));
			CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(pointcut);
			return wrapper;
			
		} else  if (kind.equals("handler")) {
			parseIdentifier(); eat("(");
			TypePattern typePat = parseTypePattern();
			eat(")");
			
			// Creates the wrapper 
			HandlerPointcut pointcut =  new HandlerPointcut(typePat);
			CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(pointcut);
			wrapper.addInfo(CaesarPointcutWrapper.INFO_EXCEPTION_TYPE, typePat);
			return wrapper;
			
		} else  if (kind.equals("initialization")) {
			parseIdentifier(); eat("(");
			SignaturePattern sig = parseConstructorSignaturePattern();
			eat(")");
			
			// Creates the wrapper 
			CaesarKindedPointcut pointcut = new CaesarKindedPointcut(Shadow.Initialization, sig);
			CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(pointcut);
			return wrapper;
			
		} else  if (kind.equals("staticinitialization")) {
			parseIdentifier(); eat("(");
			TypePattern typePat = parseTypePattern();
			eat(")");
			SignaturePattern sig = new SignaturePattern(Member.STATIC_INITIALIZATION, ModifiersPattern.ANY, 
					TypePattern.ANY, typePat, NamePattern.ANY, TypePatternList.EMPTY, 
					ThrowsPattern.ANY);
			
			// Creates the wrapper 
			CaesarKindedPointcut pointcut =  new CaesarKindedPointcut(Shadow.StaticInitialization, sig);
			CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(pointcut);
			return wrapper;
			
		} else  if (kind.equals("preinitialization")) {
			parseIdentifier(); eat("(");
			SignaturePattern sig = parseConstructorSignaturePattern();
			eat(")");
			
			// Creates the wrapper 
			CaesarKindedPointcut pointcut =  new CaesarKindedPointcut(Shadow.PreInitialization, sig);
			CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(pointcut);
			return wrapper;
			
		} else {
			return parseReferencePointcut();
		}
	}

	
	private CaesarPointcutWrapper parseKindedPointcut() {
		String kind = parseIdentifier();  
		eat("(");
		SignaturePattern sig;

		Shadow.Kind shadowKind = null;
		if (kind.equals("execution")) {
			sig = parseMethodOrConstructorSignaturePattern();
			if (sig.getKind() == Member.METHOD) {
				shadowKind = Shadow.MethodExecution;
			} else if (sig.getKind() == Member.CONSTRUCTOR) {
				shadowKind = Shadow.ConstructorExecution;
			}          
		} else if (kind.equals("call")) {
			sig = parseMethodOrConstructorSignaturePattern();
			if (sig.getKind() == Member.METHOD) {
				shadowKind = Shadow.MethodCall;
			} else if (sig.getKind() == Member.CONSTRUCTOR) {
				shadowKind = Shadow.ConstructorCall;
			}	          
		} else if (kind.equals("get")) {
			sig = parseFieldSignaturePattern();
			shadowKind = Shadow.FieldGet;
		} else if (kind.equals("set")) {
			sig = parseFieldSignaturePattern();
			shadowKind = Shadow.FieldSet;
		} else {
			throw new ParserException("bad kind: " + kind, tokenSource.peek());
		}
		eat(")");
		
		// Creates the wrapper 
		CaesarKindedPointcut p =  new CaesarKindedPointcut(shadowKind, sig);
		CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(p);
		return wrapper;
	}
	
	/**
	 * Method parseArgsPointcut.
	 * @return Pointcut
	 */
	private CaesarPointcutWrapper parseArgsPointcut() {
		parseIdentifier();
		TypePatternList arguments = parseArgumentsPattern();

		// Creates the wrapper 
		ArgsPointcut p = new ArgsPointcut(arguments);
		CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(p);
		return wrapper;
		
	}
	
	/**
	 * Method parseThisOrTargetPointcut.
	 * @return Pointcut
	 */
	private CaesarPointcutWrapper parseThisOrTargetPointcut() {
		String kind = parseIdentifier();
		eat("(");
		TypePattern type = parseTypePattern();
		eat(")");
		
		// Creates the wrapper 
		ThisOrTargetPointcut p = new ThisOrTargetPointcut(kind.equals("this"), type);
		CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(p);
		return wrapper;
	}
	
	/**
	 * Method parseWithinPointcut.
	 * @return Pointcut
	 */
	private CaesarPointcutWrapper parseWithinPointcut() {
		parseIdentifier();
		eat("(");
		TypePattern type = parseTypePattern();
		eat(")");
		
		// Creates the wrapper 
		WithinPointcut p = new WithinPointcut(type);
		CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(p);
		wrapper.addInfo(CaesarPointcutWrapper.INFO_TYPE_PATTERN, type);
		return wrapper;
	}

	private CaesarPointcutWrapper parseWithinCodePointcut() {
		parseIdentifier();
		eat("(");
		SignaturePattern sig = parseMethodOrConstructorSignaturePattern();
		eat(")");
		
		// Creates the wrapper 
		WithincodePointcut p = new WithincodePointcut(sig);;
		CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(p);
		wrapper.addInfo(CaesarPointcutWrapper.INFO_DECLARING_TYPE, sig.getDeclaringType());
		return wrapper;
	}
	
	private CaesarPointcutWrapper parseCflowPointcut(boolean isBelow) {
		parseIdentifier();
		eat("(");
		Pointcut entry = parsePointcut();
		eat(")");
		
		// Creates the wrapper 
		CflowPointcut p = new CflowPointcut(entry, isBelow, null);
		CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(p);
		return wrapper;
		
	}
	
	private SignaturePattern parseConstructorSignaturePattern() {
		SignaturePattern ret = parseMethodOrConstructorSignaturePattern();
		if (ret.getKind() == Member.CONSTRUCTOR) return ret;
		
		throw new ParserException("constructor pattern required, found method pattern",
				ret);
	}
	
	private CaesarPointcutWrapper parseReferencePointcut() {
		TypePattern onType = parseTypePattern();
		NamePattern name = tryToExtractName(onType);
		if (name == null) {
    		throw new ParserException("name pattern", tokenSource.peek());
    	}
    	if (onType.toString().equals("")) {
    		onType = null;
    	}
		
		TypePatternList arguments = parseArgumentsPattern();
		
		// Creates the wrapper 
		ReferencePointcut p = new ReferencePointcut(onType, name.maybeGetSimpleName(), arguments);
		CaesarPointcutWrapper wrapper = new CaesarPointcutWrapper(p);
		wrapper.addInfo(CaesarPointcutWrapper.INFO_ON_TYPE_SYMBOLIC, p.onTypeSymbolic);
		return wrapper;
	}
	
	private NamePattern tryToExtractName(TypePattern nextType) {
		if (nextType == TypePattern.ANY) {
			return NamePattern.ANY;
		} else if (nextType instanceof WildTypePattern) {
			WildTypePattern p = (WildTypePattern)nextType;
			return p.extractName();
		} else {
		    return null;
		}
	}
	
    // ----------------------------------------------------------------------
    // CODE FOR REGISTRING THE GENERATED POINTCUTS
    // ----------------------------------------------------------------------
	
	/**
	 * Extends PatterParser's parseSinglePointcut to register the
	 * relation between the TypePattern with the Pointcut.
	 */
	public Pointcut parseSinglePointcut() {		
		
		// Parse the pointcut with PatterParser
		CaesarPointcutWrapper p = this.parsePointcutWrapper();
		
		// Store in the map
		registerPointcut(p);
		
		// Return the PatterParser result
		return p.getWrappee();
	}
	
	
	/**
	 * Register the relation between the TypePattern in 
	 * the Pointcut with the Pointcut itself. The map will 
	 * be stored statically in the CaesarPointcutScope, 
	 * where it will be used to lookup the correct type in pointcuts.
	 * 
	 * @param pointcut the pointcut to be registered
	 */
	protected void registerPointcut(CaesarPointcutWrapper pointcut) {

		if (pointcut.isKinded()) {
			CaesarKindedPointcut p = (CaesarKindedPointcut) pointcut.getWrappee();
			
			// Transform the constructor call to a method call, using $constructor 
			// and the same parameters as the constructor
			if (Shadow.ConstructorCall.equals(p.getKind())) {
				p.setKind(Shadow.MethodCall);
				p.setSignature(new SignaturePattern(Member.METHOD, p.signature.getModifiers(),
                        p.signature.getReturnType(), p.signature.getDeclaringType(),
                        new NamePattern("$constructor"), p.signature.getParameterTypes(),
                        p.signature.getThrowsPattern()));
				
			}
			// Transform the constructor execution to a method call, using $constructor 
			// and the same parameters as the constructor
			if (Shadow.ConstructorExecution.equals(p.getKind())) {
				p.setKind(Shadow.MethodExecution);
				p.setSignature(new SignaturePattern(Member.METHOD, p.signature.getModifiers(),
                        p.signature.getReturnType(), p.signature.getDeclaringType(),
                        new NamePattern("$constructor"), p.signature.getParameterTypes(),
                        p.signature.getThrowsPattern()));
				
			}
			// Transform the object initialization to the constructor with Object. This will
			// cause some different semantics than AspectJ
			// TODO - check and document the new semantics (for constructor calls too)
			if (Shadow.Initialization.equals(p.getKind())) {
				p.setSignature(new SignaturePattern(Member.CONSTRUCTOR, p.signature.getModifiers(),
                        p.signature.getReturnType(), p.signature.getDeclaringType(),
                        p.signature.getName(), createObjectTypeList(),
                        p.signature.getThrowsPattern()));
				
			}
			
			CaesarPointcutScope.register(
					p.getSignature().getDeclaringType(),
					pointcut.getWrappee());
			return;
		}

		if (pointcut.isWithin()) {
			CaesarPointcutScope.register(
					(TypePattern) pointcut.getInfo(CaesarPointcutWrapper.INFO_TYPE_PATTERN),
					pointcut.getWrappee());
			return;
		}
		
		if (pointcut.isWithincode()) {
			CaesarPointcutScope.register(
					(TypePattern) pointcut.getInfo(CaesarPointcutWrapper.INFO_DECLARING_TYPE),
					pointcut.getWrappee());
			return;
		}
		
		// TODO - check, I don't think we need it
		if (pointcut.isHandler()) {
			CaesarPointcutScope.register(
					(TypePattern) pointcut.getInfo(CaesarPointcutWrapper.INFO_EXCEPTION_TYPE),
					pointcut.getWrappee());
			return;
		}
		
		if (pointcut.isReference()) {
			CaesarPointcutScope.register(
					(TypePattern) pointcut.getInfo(CaesarPointcutWrapper.INFO_ON_TYPE_SYMBOLIC),
					pointcut.getWrappee());
			return;
		}
		
		// No need to store information for these types
		if (pointcut.isArgs()) {
			return;
		}
		if (pointcut.isThisOrTarget()) {
			return;
		}
		if (pointcut.isCflow()) {
			return;
		}
	}
	
	/**
	 * Creates a TypePatternList which contains only the WildTypePattern needed
	 * to match the java.lang.Object name. This is used to create the parameters
	 * list when matching the default contructor
	 * 
	 * @return a list with the pattern for Object
	 */
	protected TypePatternList createObjectTypeList() {

		ArrayList names = new ArrayList();
		names.add(new NamePattern("java.lang.Object"));

		return
			new TypePatternList(
				new TypePattern[] { new WildTypePattern(names, false, 0)} );
	}

}
