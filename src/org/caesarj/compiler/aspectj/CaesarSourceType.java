package org.caesarj.compiler.aspectj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.ResolvedTypeX.ConcreteName;
import org.aspectj.weaver.ResolvedTypeX.Name;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.PerClause;
import org.caesarj.compiler.ast.FjSourceClass;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.Constants;

/**
 * @author Jürgen Hallpap
 *
 */
public class CaesarSourceType extends ConcreteName implements Constants {

	private ResolvedTypeX[] declaredInterfaces;

	private ResolvedTypeX superClass;

	private ResolvedMember[] declaredPointcuts;

	private ResolvedMember[] declaredMethods;

	private Collection declares;

	private CClass cclass;

	private CaesarBcelWorld world = CaesarBcelWorld.getInstance();

	/**
	 * Constructor for CaesarSourceType.
	 * @param resolvedTypeX
	 * @param exposedToWeaver
	 */
	public CaesarSourceType(
		Name resolvedTypeX,
		boolean exposedToWeaver,
		CClass cclass) {
		super(resolvedTypeX, exposedToWeaver);

		this.cclass = cclass;
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#isAspect()
	 */
	public boolean isAspect() {
		return true;
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#isInterface()
	 */
	public boolean isInterface() {
		return cclass.isInterface();
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getDeclaredFields()
	 */
	public ResolvedMember[] getDeclaredFields() {
		throw new RuntimeException("unimplemented");
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getDeclaredInterfaces()
	 */
	public ResolvedTypeX[] getDeclaredInterfaces() {
		if (declaredInterfaces == null) {
			fillDeclaredMembers();
		}

		return declaredInterfaces;
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getDeclaredMethods()
	 */
	public ResolvedMember[] getDeclaredMethods() {
		if (declaredMethods == null) {
			fillDeclaredMembers();
		}

		return declaredMethods;
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getDeclaredPointcuts()
	 */
	public ResolvedMember[] getDeclaredPointcuts() {
		if (declaredPointcuts == null) {
			fillDeclaredMembers();
		}
		return declaredPointcuts;
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getPerClause()
	 */
	public PerClause getPerClause() {
		throw new RuntimeException("unimplemented");
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getDeclares()
	 */
	protected Collection getDeclares() {
		if (declares == null) {
			fillDeclaredMembers();
		}

		return declares;
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getTypeMungers()
	 */
	protected Collection getTypeMungers() {
		throw new RuntimeException("unimplemented");
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getPrivilegedAccesses()
	 */
	protected Collection getPrivilegedAccesses() {
		throw new RuntimeException("unimplemented");
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getModifiers()
	 */
	public int getModifiers() {
		return cclass.getModifiers();
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getSuperclass()
	 */
	public ResolvedTypeX getSuperclass() {
		if (superClass == null) {
			fillDeclaredMembers();
		}

		return superClass;
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#getSourceLocation()
	 */
	public ISourceLocation getSourceLocation() {
		throw new RuntimeException("unimplemented");
	}

	/**
	 * @see org.aspectj.weaver.ResolvedTypeX.ConcreteName#isWovenBy(ResolvedTypeX)
	 */
	public boolean isWovenBy(ResolvedTypeX aspectType) {
		return false;
	}

	protected void fillDeclaredMembers() {
		CReferenceType[] ifcs = cclass.getInterfaces();
		declaredInterfaces = new ResolvedTypeX[ifcs.length];
		for (int i = 0; i < ifcs.length; i++) {
			declaredInterfaces[i] = world.resolve(ifcs[i].getCClass());
		}

		if (cclass.getSuperClass() != null) {
			superClass = world.resolve(cclass.getSuperClass());
		}

		List pointcuts = new ArrayList();
		if (cclass instanceof FjSourceClass) {
			FjSourceClass caesarClass = (FjSourceClass) cclass;
			pointcuts.addAll(caesarClass.getResolvedPointcuts());
		}
		declaredPointcuts =
			(ResolvedMember[]) pointcuts.toArray(
				new ResolvedMember[pointcuts.size()]);

		CMethod[] methods = cclass.getMethods();
		if (methods != null) {
			declaredMethods = new ResolvedMember[methods.length];

			for (int i = 0; i < methods.length; i++) {
				//XXX resolved???
				ResolvedMember member =
					new ResolvedMember(
						Member.METHOD,
						TypeX.forName(methods[i].getOwner().getQualifiedName()),
						methods[i].getModifiers(),
						methods[i].getIdent(),
						methods[i].getSignature());

				declaredMethods[i] = member;
			}

		} else {
			declaredMethods = new ResolvedMember[0];
		}

		declares = new ArrayList();
		if (cclass instanceof FjSourceClass) {
			FjSourceClass caesarClass = (FjSourceClass) cclass;
			Declare[] decs = CaesarDeclare.wrappees(caesarClass.getDeclares());
			for (int i = 0; i < decs.length; i++) {
				declares.add(decs[i]);
			}

		}

	}

}
