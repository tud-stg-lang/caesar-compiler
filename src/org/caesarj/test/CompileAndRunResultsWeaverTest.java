package org.caesarj.test;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import org.caesarj.compiler.*;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.export.CSourceClass;
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
						"StaticAspect.java",
						"AnotherAspect.java"
								};

				compiler = new CompilerMock(this, new PrintWriter(System.out) {
					public void println() {
					}
					public void write(String s) {
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