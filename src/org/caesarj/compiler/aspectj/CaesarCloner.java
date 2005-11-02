package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.SignaturePattern;
import org.aspectj.weaver.patterns.ThrowsPattern;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;

public class CaesarCloner {

	protected static CaesarCloner instance = null;
	
	public static CaesarCloner instance() {
		if (instance == null) {
			instance = new CaesarCloner();
		}
		return instance;
	}
	
	private CaesarCloner() {
	}
	
	public Pointcut clone(Pointcut p) {
		return p;
	}
	
	
	public SignaturePattern clone(SignaturePattern pattern) {
		
		return new SignaturePattern(
				pattern.getKind(), pattern.getModifiers(),
				clone(pattern.getReturnType()), clone(pattern.getDeclaringType()),
				clone(pattern.getName()), clone(pattern.getParameterTypes()),
                clone(pattern.getThrowsPattern()));
	}
	
	/**
	 * Clones a type pattern
	 * 
	 * TODO - not implemented
	 * 
	 * @param pattern
	 * @return
	 */
	public TypePattern clone(TypePattern pattern) {
		return pattern;
	}
	
	/**
	 * Clones a name pattern
	 * 
	 * @param pattern
	 * @return
	 */
	public NamePattern clone(NamePattern pattern) {
		return new NamePattern(pattern.toString());
	}
	
	/**
	 * Clones a type pattern list
	 * 
	 * @param pattern
	 * @return
	 */
	public TypePatternList clone(TypePatternList pattern) {
		
		TypePattern[] patterns = pattern.getTypePatterns();
		TypePattern[] result = new TypePattern[patterns.length];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = clone(patterns[i]);
		}
		
		return new TypePatternList(result);
	}
	
	/**
	 * Clones a throws pattern
	 * 
	 * TODO 
	 * 
	 * @param pattern
	 * @return
	 */
	public ThrowsPattern clone(ThrowsPattern pattern) {
		return pattern;
	}
	
}
