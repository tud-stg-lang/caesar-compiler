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
}