package org.caesarj.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.caesarj.compiler.CaesarParser;
import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcClassReader;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.KjcOptions;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.types.KjcSignatureParser;
import org.caesarj.compiler.types.KjcTypeFactory;
import org.caesarj.compiler.types.SignatureParser;
import org.caesarj.tools.antlr.runtime.ParserException;

public class FjTestCase extends TestCase 
{
	public String[][] errormessages = new String[][] { };
	
	protected CompilerBase compiler;
	
	public FjTestCase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

/**
 * @returns a String: "./test/$CLASS", meaning the subdirectory of "./test" with the name 
 * of the executing test-file.
 */
	protected String getWorkingDirectory() 
	{
		String res = "." + File.separator + "test" + File.separator 
			+ getClass().getName().substring(
			getClass().getName().lastIndexOf( "." ) + 1 );
            
        return res;
	}

	protected void warn( String message ) {
		System.out.println( "warning - " + getClass().getName() + ": " + message );
	}
	
	/*
	 * Compiles and runs given test file in a separate package
	 */
	protected void compileAndRun(String pckgName, String testCaseName) throws Throwable 
	{
		// Clean up output folder
		removeClassFiles(pckgName);
		
		// Create compiler
		compiler = new CompilerMock(this, 
			new PrintWriter(System.out) 
			{
				public void println() {
				}
				public void write(String s) {
					System.err.println(s);
				}
			}
		);
		
		// Retrieve input files
		File workingDir = new File( getWorkingDirectory() + File.separator + pckgName);
		
		List files = new LinkedList();
		findAllFiles(workingDir, files);
		
		List fileNames = new LinkedList();
		
		Iterator it = files.iterator();
		while (it.hasNext()) 
		{
			File file = (File)it.next();
			String fileName = file.getName();
			if (fileName.endsWith( ".java" )) 
			{
				fileNames.add(file.getAbsolutePath());				
			}
		}
		
		String[] args = (String[])fileNames.toArray(new String[0]);
		
		// Compile test
		compiler.run(args);
		
		// Execute test
		System.out.println("Test "+testCaseName+" starts");
				
		Object generatedTest = Class.forName( "generated." + pckgName + "."+ testCaseName ).newInstance();
		((TestCase)generatedTest).runBare();				
	}
	
	/*
	 * Compiles and runs given test file in the root directory
	 */
	protected void compileAndRun(String testCaseName) throws Throwable 
	{
		// Clean up output folder
		removeClassFiles(null);
		
		// Compile test
		compileFile(testCaseName);
		
		// Execute test
		doGeneratedTest(testCaseName);				
	}
	
	/*
	 * Runs given compiled test file in the root directory
	 */
	protected void doGeneratedTest(String testCaseName) throws Throwable 
	{
		// Execute test
		System.out.println("Test "+testCaseName+" starts");
				
		Object generatedTest = Class.forName( "generated." + testCaseName ).newInstance();
		((TestCase)generatedTest).runBare();				
	}
	
	/*
	 * Compiles given test file in the root directory
	 */
	protected void compileFile(String testCaseName) throws Throwable 
	{
		// Create compiler
		compiler = new CompilerMock(this, 
			new PrintWriter(System.out) 
			{
				public void println() {
				}
				public void write(String s) {
					System.err.println(s);
				}
			}
		);
		
		// Compile test
		String[] args = {testCaseName + ".java"};
		compiler.run(args);			
	}
	
	/*
	 * Removes all generated class files
	 */
	public void removeClassFiles(String pckgName) 
	{
		File workingDir = new File( getWorkingDirectory() + File.separator 
			+ "generated" + (pckgName == null ? "" : File.separator + pckgName));
		List files = new LinkedList();
		findAllFiles(workingDir, files);
		
		int deleteCount = 0;
		try {
			Iterator it = files.iterator();
			while (it.hasNext()) 
			{
				File file = (File)it.next();
				if (file.getName().endsWith( ".class" )) {
					if (file.delete())
						deleteCount++;
				}
			}
		} 
		catch( Throwable t ) {
		} 
		
		if( deleteCount == 0 )
			warn( "no class files were deleted" );		
	}
	
	private void findAllFiles(File dir, List lst)
	{
		File[] files = dir.listFiles();
		if (files == null)
			return;
		
		for (int i = 0; i < files.length; i++) 
		{
			if (files[i].isDirectory())
			{
				findAllFiles(files[i], lst);
			}
			else
			{
				lst.add(files[i]);
			}
		}
	}
}

/*
 *	Compiler adapter 
 */
class CompilerMock extends Main 
{
	protected Vector allUnits;
	protected ClassReaderMock classReader;
	
	public CompilerMock(FjTestCase test, PrintWriter p) 
	{
		super(test.getWorkingDirectory(), p);
		
		allUnits = new Vector();		
	}

	private KjcEnvironment cachedEnvironment;

	protected JCompilationUnit getJCompilationUnit(CaesarParser parser)
		throws ParserException 
	{
		JCompilationUnit compilationUnit =
			super.getJCompilationUnit(parser);
		allUnits.add(compilationUnit);
		return compilationUnit;
	}

	protected KjcEnvironment createEnvironment(KjcOptions options) 
	{
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

/*
 *	Class reader adapter 
 */
class ClassReaderMock extends KjcClassReader 
{
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