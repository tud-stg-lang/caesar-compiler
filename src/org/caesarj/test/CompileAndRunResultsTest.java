package org.caesarj.test;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.caesarj.compiler.CaesarParser;
import org.caesarj.compiler.Compiler;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.FjCleanClassDeclaration;
import org.caesarj.compiler.tools.antlr.runtime.ParserException;
import org.caesarj.compiler.util.ClassTransformationFjVisitor;
import org.caesarj.compiler.util.FjVisitor;
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

public class CompileAndRunResultsTest extends FjTestCase {

	public String[] errorfiles = new String[] {
		"FailureInReturn.java",
		"FailureWithoutFamilyJ.java",
		"FailureInAssignment.java",
		"FailureInAssignSubFamily.java",
		"FailureInInitializer.java",
		"FailureInParameterPassing.java",
		"FailureInPassMethodReturn.java",
		"FailureCleanClassProtectedMethod.java",
		"FailureCleanClassPackageMethod.java",
		"FailureCleanClassInner.java",
		"FailureCleanClassPublicField.java",
		"FailureCleanClassProtectedField.java",
		"FailureCleanClassNonCleanInner.java",
		"FailureCleanClassInheritsNonClean.java",
		"FailureCleanClassVirtualInheritsNonClean.java",
		"FailureWrongConstructor.java",
		"FailurePassingCase1.java",
		"FailurePassingCase2.java",
		"FailurePassingNoPrefix.java",
		"FailureNonCleanExtendsClean.java",
		"FailureCleanAbstractClass.java",
		"FailureWrongType.java",
		"BugReport1.java",
		"FailureInReturn.java"
	};
	public String[][] errormessages = new String[][] {
		{ "SubTest.java", "[FJLS 2.1]" },
		{ errorfiles[0], "int generated/FailureWithoutFamilyJ$MessagerSub.message()" },
		{ errorfiles[1], "[FJLS 2.5]" },
		{ errorfiles[2], "[FJLS 2.5]" },
		{ errorfiles[3]+":10", "[FJLS 2.5]" },
		{ errorfiles[4]+":13", "[FJLS 2.5]" },
		{ errorfiles[5]+":14", "[FJLS 2.5]" },
		{ errorfiles[6]+":3", "[FJLS 1.2]" },
		{ errorfiles[7]+":3", "[FJLS 1.2]" },
		{ errorfiles[8]+":4", "[FJLS 1.3]" },
		{ errorfiles[9]+":3", "[FJLS 1.4]" },
		{ errorfiles[10]+":3", "[FJLS 1.4]" },
		{ errorfiles[11]+":3", "[FJLS 1.5]" },
		{ errorfiles[12]+":5", "[FJLS 1.7]" },
		{ errorfiles[13]+":4", "[FJLS 1.7]" },
		{ errorfiles[14]+":9", "\"V()\"" },
		{ errorfiles[15]+":12", "[FJLS 2.5]" },
		{ errorfiles[16]+":13", "[FJLS 2.5]" },
		{ errorfiles[17]+":26", "[FJLS 2.5]" },
		{ errorfiles[18]+":4", "[FJLS 1.8]" },
		{ errorfiles[19]+":3", "[FJLS 1.9]" },
		{ errorfiles[20]+":7", "family/Inner" },
		{ errorfiles[21]+":19", "[FJLS 2.3]" }
	};

	protected Vector allUnits;
	protected ClassModulatingFjVisitorMock modulator;
	protected ClassReaderMock classReader;
	protected Compiler compiler;
	protected static boolean doSetUp = true;

	public CompileAndRunResultsTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		if( doSetUp ) { try {
			
			removeClassFiles();			
			allUnits = new Vector();		
			
			// these files should not raise errors
			String[] args = new String[] {
				"X_.java",
				"Y_.java",
				"SuperSubTest.java",
				"TestingVerySimple.java",
				"VerySimpleTest.java",
				"UnqualifiedFamilyCreation.java",
				"FamilyCast.java",
				"HelloWorldTester.java",
				"HelloWorld.java",
				"JAXP.java",
				"GraphTest.java",
				"FactoryMethodTest.java",
				"SubTest.java",
				"Test.java",
				"DoorHighLevelTest.java",
				"DoorTest.java",
				"Entities.java",
				"Person.java"
			};
			
			compiler = new CompilerMock( this, new PrintWriter( System.out ){
				public void println() {
				}
				public void write(String s) {
					if( modulator != null )
						modulator.addMessage( s );
					else
						System.err.println( s );
				}
			} );
			compiler.run( args );
			if (! errorMessageGenerated())
				// these files will raise errors
				for( int i = 0; i < errorfiles.length; i++ ) {
					compiler.run( new String[]{ errorfiles[ i ] } );
				}
			
		} catch( Throwable t ) {
			t.printStackTrace();
		} finally {
			doSetUp = false;
		} }
	}

	/**
	 * @return
	 */
	protected boolean errorMessageGenerated()
	{
		for (int i = 0; i < modulator.messages.size(); i++)
			if (((String)modulator.messages.get(i)).indexOf("error") > 0)
				return true;
		
		return false;
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
			
			assertEquals( "we exactly expect n messages", errormessages.length, modulator.getMessages().size()	);		
			for( int i = 0; i < errormessages.length; i++ ) {
				modulator.findAndRemoveMessage( errormessages[ i ][ 0 ], errormessages[ i ][ 1 ] );
			}
			
		} catch( Throwable t ) {
			for( int i = 0; i < clonedMessages.size(); i++ ) {
				System.out.println( clonedMessages.elementAt( i ) );
			}
			throw new RuntimeException( t );
		}
	
		for( int i = 0; i < modulator.getMessages().size(); i++ ) {
			System.out.println( modulator.getMessages().elementAt( i ) );
		}
	}
		
	public void testBugReport() throws Throwable {
		doGeneratedTest( "BugReport1" );
	}

	public void testSuperSubTest() throws Throwable {
		doGeneratedTest( "SuperSubTest" );
	}

	public void testDoorTest() throws Throwable {
		doGeneratedTest( "DoorTest" );
	}

	public void testGraphTest() throws Throwable {
		doGeneratedTest( "GraphTest" );
	}

	public void testDoorHighLevelTest() throws Throwable {
		doGeneratedTest( "DoorHighLevelTest" );
	}

	public void testFactoryMethodTest() throws Throwable {
		doGeneratedTest( "FactoryMethodTest" );
	}

	class ClassModulatingFjVisitorMock extends ClassTransformationFjVisitor {

		public ClassModulatingFjVisitorMock(KjcEnvironment environment) {			
			super(environment);
			messages = new Vector();
		}

		protected Vector messages;
		public void addMessage( String e ) {
			messages.add( e );
		}
		public Vector getMessages() {
			return messages;
		}
		public String findAndRemoveMessage( String pattern ) {
			return findAndRemoveMessage( pattern, null );
		}
		public String findAndRemoveMessage( String pattern, String secondPattern ) {
			if( messages == null )
				return null;
			for( int i = 0; i < messages.size(); i++ ) {
				String message = (String) messages.elementAt( i );
				if( secondPattern == null && message.indexOf( pattern ) >= 0
					|| message.indexOf( pattern ) >= 0 && message.indexOf( secondPattern ) >= 0 ) {
					messages.remove( i );
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
				
			cleanClassesVisited.add( self );
			cleanClassInterfacesCreated.add( self.getCleanInterface() );
			cleanClassIfcImplsCreated.add( self.getCleanInterfaceImplementation() );
		}
	}
	
	class CompilerMock extends Main {
		
		public CompilerMock( CompileAndRunResultsTest test, PrintWriter p ) {
			super( test.getWorkingDirectory(), p );
		}
		
		private KjcEnvironment cachedEnvironment;
				
		protected JCompilationUnit getJCompilationUnit(CaesarParser parser)
			throws ParserException {				
			JCompilationUnit compilationUnit = 
				super.getJCompilationUnit(parser);
			allUnits.add( compilationUnit );
			return compilationUnit;
		}
		protected FjVisitor getClassTransformation(KjcEnvironment environment) {
			if( modulator == null )
				modulator = new ClassModulatingFjVisitorMock( environment );
			return modulator;
		}
		protected KjcEnvironment createEnvironment(KjcOptions options) {
			if( cachedEnvironment == null ) {
			    classReader = new ClassReaderMock(
			    	options.classpath,
			    	options.extdirs,
			    	new KjcSignatureParser() );
			    cachedEnvironment = new KjcEnvironment(
			    	classReader, 
					new KjcTypeFactory( classReader, options.generic ),
					options );
			}
		    return cachedEnvironment;
		}

		protected void inform(String message) {
			ClassModulatingFjVisitorMock modulator =
				(ClassModulatingFjVisitorMock) getClassTransformation( cachedEnvironment );
			modulator.addMessage( message );
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
			allLoadedClasses.put( cl.getQualifiedName(), cl );
			return super.addSourceClass( cl );
		}
	}
}