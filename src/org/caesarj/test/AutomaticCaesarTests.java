package org.caesarj.test;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.caesarj.compiler.tools.antlr.runtime.ParserException;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JClassImport;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPackageImport;
import org.caesarj.kjc.JPackageName;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcClassReader;
import org.caesarj.kjc.KjcEnvironment;
import org.caesarj.kjc.KjcOptions;
import org.caesarj.kjc.KjcSignatureParser;
import org.caesarj.kjc.KjcTypeFactory;
import org.caesarj.kjc.SignatureParser;

import org.caesarj.compiler.CaesarParser;
import org.caesarj.compiler.Compiler;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.util.ClassTransformationFjVisitor;
import org.caesarj.compiler.util.FjVisitor;

public class AutomaticCaesarTests extends FjTestCase {

	public String[] errorfiles = new String[] {
	};
	int i;
	public String[][] errormessages = new String[][] {
	};

	protected Vector allUnits;
	protected ClassModulatingFjVisitorMock modulator;
	protected ClassReaderMock classReader;
	protected Compiler compiler;
	protected static boolean doSetUp = true;

	public AutomaticCaesarTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		System.out.println("AutomaticCaesartests Setup starts");
		super.setUp();

		if (doSetUp) {
			try {

				removeClassFiles();
				allUnits = new Vector();

				// these files should not raise errors
				String[] args =
					new String[] {
						"CaesarTestCase_0.java",
						"CaesarTestCase_1.java",
						"CaesarTestCase_2.java",
						"CaesarTestCase_3.java",
						"CaesarTestCase_4.java",
						"CaesarTestCase_5.java",
						"CaesarTestCase_6.java",
						"CaesarTestCase_7.java",
						"CaesarTestCase_8.java",
						"CaesarTestCase_9.java",						
						"CaesarTestCase_10.java",	
						"CaesarTestCase_11.java",
						"CaesarTestCase_12.java",																													
						"Barrier.java" 
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
		System.out.println("AutomaticCaesartests testCompilation starts");
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

	public void testCaesarTestCase_0() throws Throwable {
		doGeneratedTest("CaesarTestCase_0");
	}

	public void testCaesarTestCase_1() throws Throwable {
		doGeneratedTest("CaesarTestCase_1");
	}

	public void testCaesarTestCase_2() throws Throwable {
		doGeneratedTest("CaesarTestCase_2");
	}

	public void testCaesarTestCase_3() throws Throwable {
		doGeneratedTest("CaesarTestCase_3");
	}

	public void testCaesarTestCase_4() throws Throwable {
		doGeneratedTest("CaesarTestCase_4");
	}

	public void testCaesarTestCase_5() throws Throwable {
		doGeneratedTest("CaesarTestCase_5");
	}

	public void testCaesarTestCase_6() throws Throwable {
		doGeneratedTest("CaesarTestCase_6");
	}

	public void testCaesarTestCase_7() throws Throwable {
		doGeneratedTest("CaesarTestCase_7");
	}

	public void testCaesarTestCase_8() throws Throwable {
		doGeneratedTest("CaesarTestCase_8");
	}
	
	public void testCaesarTestCase_9() throws Throwable {
		doGeneratedTest("CaesarTestCase_9");
	}
	
	public void testCaesarTestCase_10() throws Throwable {
		doGeneratedTest("CaesarTestCase_10");
	}
	
	public void testCaesarTestCase_11() throws Throwable {
		doGeneratedTest("CaesarTestCase_11");
	}
	public void testCaesarTestCase_12() throws Throwable {
		doGeneratedTest("CaesarTestCase_12");
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
		public void visitFjClassDeclaration(
			FjClassDeclaration self,
			int modifiers,
			String ident,
			CTypeVariable[] typeVariables,
			String superClass,
			CReferenceType[] interfaces,
			JPhylum[] body,
			JMethodDeclaration[] methods,
			JTypeDeclaration[] decls) {
			super.visitFjClassDeclaration(
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
			FjCleanClassDeclaration self,
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

		public CompilerMock(AutomaticCaesarTests test, PrintWriter p) {
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
		protected FjVisitor getClassTransformation(KjcEnvironment environment) {
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