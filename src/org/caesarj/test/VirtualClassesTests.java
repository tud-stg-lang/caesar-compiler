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

public class VirtualClassesTests extends FjTestCase {

	public String[] errorfiles = new String[] {
	};
	int i;
	public String[][] errormessages = new String[][] {
	};

	protected Vector allUnits;
	protected ClassReaderMock classReader;
	protected CompilerBase compiler;
	protected static boolean doSetUp = true;

	public VirtualClassesTests(String name) {
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
					//	"VCTestCase_0.java",
                    //  "VCTestCase_1.java",
                    //  "VCTestCase_2.java",
					//	"VCTestCase_3.java",
					//	"VCTestCase_4.java",
					//	"VCTestCase_5.java",
					//	"VCTestCase_6.java",
					//	"VCTestCase_7.java",
					//	"VCTestCase_8.java",
					//	"VCTestCase_9.java",
					//	"VCTestCase_10.java",
					//	"VCTestCase_11.java",
					//	"VCTestCase_12.java",
					//	"VCTestCase_13.java",
					//	"VCTestCase_14.java",
						"VCTestCase_15.java"
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
/*
    public void testCaesarTestCase_0() throws Throwable {
        doGeneratedTest("VCTestCase_0");
    }
*/
/*
    public void testCaesarTestCase_1() throws Throwable {
        doGeneratedTest("VCTestCase_1");
    }
*/
/*
    public void testCaesarTestCase_2() throws Throwable {
        doGeneratedTest("VCTestCase_2");
    }
*/
/*
    public void testCaesarTestCase_3() throws Throwable {
        doGeneratedTest("VCTestCase_3");
    }
*/
/*
    public void testCaesarTestCase_4() throws Throwable {
        doGeneratedTest("VCTestCase_4");
    }
*/
/*    
    public void testCaesarTestCase_5() throws Throwable {
        doGeneratedTest("VCTestCase_5");
    }
*/
/* 
   public void testCaesarTestCase_6() throws Throwable {
        doGeneratedTest("VCTestCase_6");
    }
*/
/*
    public void testCaesarTestCase_7() throws Throwable {
        doGeneratedTest("VCTestCase_7");
    }
*/
/*
	public void testCaesarTestCase_8() throws Throwable {
        doGeneratedTest("VCTestCase_8");
    }
*/
/*	
	public void testCaesarTestCase_9() throws Throwable {
        doGeneratedTest("VCTestCase_9");
    }
*/
/*	
	public void testCaesarTestCase_10() throws Throwable {
        doGeneratedTest("VCTestCase_10");
    }
*/
/*
	public void testCaesarTestCase_11() throws Throwable {
        doGeneratedTest("VCTestCase_11");
    }
*/
/*
	public void testCaesarTestCase_12() throws Throwable {
        doGeneratedTest("VCTestCase_12");
    }
*/
/*
	public void testCaesarTestCase_13() throws Throwable {
        doGeneratedTest("VCTestCase_13");
    }
*/
/*	
	public void testCaesarTestCase_14() throws Throwable {
        doGeneratedTest("VCTestCase_14");
    }
*/
	
	public void testCaesarTestCase_15() throws Throwable {
        doGeneratedTest("VCTestCase_15");
    }
	
	class CompilerMock extends Main {

		public CompilerMock(VirtualClassesTests test, PrintWriter p) {
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