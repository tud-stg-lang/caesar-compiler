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

public class CompileAndRunResultsWeaverTest extends FjTestCase 
{
	public CompileAndRunResultsWeaverTest(String name)	{
		super(name);
	}
	
	public void testNestedAspectTest() throws Throwable {
		compileAndRun("NestedAspectTest");
	}

	public void testJoinPointReflectionTest() throws Throwable {
		compileAndRun("JoinPointReflectionTest");
	}

	public void testDeploymentTest() throws Throwable {
		compileAndRun("DeploymentTest");
	}

	public void testAutomaticDeploymentTest() throws Throwable {
		compileAndRun("AutomaticDeploymentTest");
	}

	public void testPrivilegedAccessTest() throws Throwable {
		compileAndRun("PrivilegedAccessTest");
	}
}