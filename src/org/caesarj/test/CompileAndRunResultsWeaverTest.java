package org.caesarj.test;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.caesarj.compiler.CaesarParser;
import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcClassReader;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.KjcOptions;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.ast.JClassDeclaration;
import org.caesarj.compiler.ast.CaesarClassDeclaration;
import org.caesarj.compiler.ast.DeclarationVisitor;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JCompilationUnit;
import org.caesarj.compiler.ast.JMethodDeclaration;
import org.caesarj.compiler.ast.JPackageImport;
import org.caesarj.compiler.ast.JPackageName;
import org.caesarj.compiler.ast.JPhylum;
import org.caesarj.compiler.ast.JTypeDeclaration;
import org.caesarj.compiler.delegation.ClassTransformationFjVisitor;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.KjcSignatureParser;
import org.caesarj.compiler.types.KjcTypeFactory;
import org.caesarj.compiler.types.SignatureParser;
import org.caesarj.tools.antlr.runtime.ParserException;

public class CompileAndRunResultsWeaverTest extends FjTestCase {

	public String[] errorfiles = new String[] {
	};
	int i;
	public String[][] errormessages = new String[][] {
	};

	protected Vector allUnits;
	protected ClassModulatingFjVisitorMock modulator;
	protected ClassReaderMock classReader;
	protected CompilerBase compiler;
	protected static boolean doSetUp = true;

	public CompileAndRunResultsWeaverTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		if (doSetUp) {
			try {

				removeClassFiles();
				allUnits = new Vector();

				// these files should not raise errors
				String[] args =
					new String[] {
						"NestedAspectTest.java",
						"JoinPointReflectionTest.java",
						"DeploymentTest.java",
						"SimpleAspect.java",
						"SubSimpleAspect.java",
						"SuperAspect.java",
						"AutomaticDeploymentTest.java",
						"PrivilegedAccessTest.java",
						"KlausTest.java",
						"StaticAspect.java",
						"AnotherAspect.java"
								};

				compiler = new CompilerMock(this, new PrintWriter(System.out) {
					public void println() {
					}
					public void write(String s) {
						if (modulator != null)
							modulator.addMessage(s);
						else
							System.err.println(s);
					}
				});

				compiler.run(args);

				// these files will raise errors
				for (int i = 0; i < errorfiles.length; i++) {
					compiler.run(new String[] { errorfiles[i] });
				}

			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				doSetUp = false;
			}
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCompilation() {
		Iterator ifcIt = modulator.cleanClassInterfacesCreated.iterator();
		Iterator classIt = modulator.cleanClassesVisited.iterator();

		//////////////////////////////////////////
		// assert all required messages are there:
		//////////////////////////////////////////

		Vector clonedMessages = (Vector) modulator.getMessages().clone();
		try {

			assertEquals(
				"we exactly expect n messages",
				errormessages.length,
				modulator.getMessages().size());
			for (int i = 0; i < errormessages.length; i++) {
				modulator.findAndRemoveMessage(
					errormessages[i][0],
					errormessages[i][1]);
			}

		} catch (Throwable t) {
			for (int i = 0; i < clonedMessages.size(); i++) {
				System.out.println(clonedMessages.elementAt(i));
			}
			throw new RuntimeException(t);
		}

		for (int i = 0; i < modulator.getMessages().size(); i++) {
			System.out.println(modulator.getMessages().elementAt(i));
		}
	}

	public void testNestedAspectTest() throws Throwable {
		doGeneratedTest("NestedAspectTest");
	}

	public void testJoinPointReflectionTest() throws Throwable {
		doGeneratedTest("JoinPointReflectionTest");
	}

	public void testDeploymentTest() throws Throwable {
		doGeneratedTest("DeploymentTest");
	}

	public void testAutomaticDeploymentTest() throws Throwable {
		doGeneratedTest("AutomaticDeploymentTest");
	}

	public void testPrivilegedAccessTest() throws Throwable {
		doGeneratedTest("PrivilegedAccessTest");
	}

	public void testKlausTest() throws Throwable {
		doGeneratedTest("KlausTest");
	}

	class ClassModulatingFjVisitorMock extends ClassTransformationFjVisitor {

		public ClassModulatingFjVisitorMock(KjcEnvironment environment) {
			super(environment);
			messages = new Vector();
		}

		protected Vector messages;
		public void addMessage(String e) {
			messages.add(e);
		}
		public Vector getMessages() {
			return messages;
		}
		public String findAndRemoveMessage(String pattern) {
			return findAndRemoveMessage(pattern, null);
		}
		public String findAndRemoveMessage(
			String pattern,
			String secondPattern) {
			if (messages == null)
				return null;
			for (int i = 0; i < messages.size(); i++) {
				String message = (String) messages.elementAt(i);
				if (secondPattern == null
					&& message.indexOf(pattern) >= 0
					|| message.indexOf(pattern) >= 0
					&& message.indexOf(secondPattern) >= 0) {
					messages.remove(i);
					i--;
					return message;
				}
			}
			return null;
		}

		int visitedCompilationsUnits = 0;
		public void visitCompilationUnit(
			JCompilationUnit self,
			JPackageName packageName,
			JPackageImport[] importedPackages,
			JClassImport[] importedClasses,
			JTypeDeclaration[] typeDeclarations) {
			super.visitCompilationUnit(
				self,
				packageName,
				importedPackages,
				importedClasses,
				typeDeclarations);
			visitedCompilationsUnits++;
		}

		int visitedClassDeclarations = 0;
		public void visitClassDeclaration(
			JClassDeclaration self,
			int modifiers,
			String ident,
			CTypeVariable[] typeVariables,
			String superClass,
			CReferenceType[] interfaces,
			JPhylum[] body,
			JMethodDeclaration[] methods,
			JTypeDeclaration[] decls) {
			super.visitClassDeclaration(
				self,
				modifiers,
				ident,
				typeVariables,
				superClass,
				interfaces,
				body,
				methods,
				decls);
			visitedClassDeclarations++;
		}

		Vector cleanClassIfcImplsCreated = new Vector();
		Vector cleanClassInterfacesCreated = new Vector();
		Vector cleanClassesVisited = new Vector();
		public void visitFjCleanClassDeclaration(
			CaesarClassDeclaration self,
			int modifiers,
			String ident,
			CTypeVariable[] typeVariables,
			String superClass,
			CReferenceType[] interfaces,
			JPhylum[] body,
			JMethodDeclaration[] methods,
			JTypeDeclaration[] decls) {

			super.visitFjCleanClassDeclaration(
				self,
				modifiers,
				ident,
				typeVariables,
				superClass,
				interfaces,
				body,
				methods,
				decls);

			cleanClassesVisited.add(self);
			cleanClassInterfacesCreated.add(self.getCleanInterface());
			cleanClassIfcImplsCreated.add(
				self.getCleanInterfaceImplementation());
		}
	}

	class CompilerMock extends Main {

		public CompilerMock(
			CompileAndRunResultsWeaverTest test,
			PrintWriter p) {
			super(test.getWorkingDirectory(), p);
		}

		private KjcEnvironment cachedEnvironment;

		protected JCompilationUnit getJCompilationUnit(CaesarParser parser)
			throws ParserException {
			JCompilationUnit compilationUnit =
				super.getJCompilationUnit(parser);
			allUnits.add(compilationUnit);
			return compilationUnit;
		}
		protected DeclarationVisitor getClassTransformation(KjcEnvironment environment) {
			if (modulator == null)
				modulator = new ClassModulatingFjVisitorMock(environment);
			return modulator;
		}
		protected KjcEnvironment createEnvironment(KjcOptions options) {
			if (cachedEnvironment == null) {
				classReader =
					new ClassReaderMock(
						options.classpath,
						options.extdirs,
						new KjcSignatureParser());
				cachedEnvironment =
					new KjcEnvironment(
						classReader,
						new KjcTypeFactory(classReader, options.generic),
						options);
			}
			return cachedEnvironment;
		}

		protected void inform(String message) {
			ClassModulatingFjVisitorMock modulator =
				(ClassModulatingFjVisitorMock) getClassTransformation(cachedEnvironment);
			modulator.addMessage(message);
		}

	};

	class ClassReaderMock extends KjcClassReader {

		public ClassReaderMock(
			String classp,
			String extdirs,
			SignatureParser signatureParser) {
			super(classp, extdirs, signatureParser);
			allLoadedClasses = new Hashtable();
		}

		Hashtable allLoadedClasses;
		Hashtable getAllLoadedClasses() {
			return allLoadedClasses;
		}

		public boolean addSourceClass(CSourceClass cl) {
			allLoadedClasses.put(cl.getQualifiedName(), cl);
			return super.addSourceClass(cl);
		}
	}
}