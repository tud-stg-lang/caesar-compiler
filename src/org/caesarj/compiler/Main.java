/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: Main.java,v 1.96 2005-04-06 11:59:35 gasiunas Exp $
 */

package org.caesarj.compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.compiler.aspectj.CaesarMessageHandler;
import org.caesarj.compiler.aspectj.CaesarWeaver;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.cclass.CClassPreparation;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.joincollab.JoinCollaborations;
import org.caesarj.compiler.joinpoint.GenerateDeploymentSupport;
import org.caesarj.compiler.joinpoint.JoinDeploymentSupport;
import org.caesarj.compiler.joinpoint.JoinPointReflectionVisitor;
import org.caesarj.compiler.joinpoint.StaticDeploymentPreparation;
import org.caesarj.compiler.joinpoint.StaticFieldDeploymentVisitor;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.compiler.typesys.graph.CaesarTypeGraphGenerator;
import org.caesarj.compiler.typesys.java.JavaTypeGraph;
import org.caesarj.compiler.typesys.java.JavaTypeNode;
import org.caesarj.mixer.ClassGenerator;
import org.caesarj.mixer.MixerException;
import org.caesarj.tools.antlr.extra.InputBuffer;
import org.caesarj.tools.antlr.runtime.ParserException;
import org.caesarj.util.Messages;
import org.caesarj.util.PositionedError;
import org.caesarj.util.UnpositionedError;

/**
 * The entry point of the Caesar compiler.
 * 
 * @author Jürgen Hallpap
 * @author Ivica Aracic
 */
public class Main extends MainSuper implements Constants {

    private CaesarMessageHandler messageHandler;
    private Set errorMessages;

    // The used weaver. An instance ist created when it's needed in generateAndWeaveCode
    private CaesarWeaver weaver;

	/**
     * This is the structure model constructed during compilation.
     * In the end of the compilation, it represents the structure of the program and can
     * be used, for example, by the Eclipse Plug-in, in order to get information and 
     * display for the user.
     */
    //protected StructureModel model;

    protected static boolean buildAsm = false;
    protected static boolean printAsm = false;
    
    /**
     * @param workingDirectory the working directory
     * @param diagnosticOutput where to put stderr
     * 
     */
    public Main(String workingDirectory, PrintWriter diagnosticOutput) {
        super(workingDirectory, diagnosticOutput);
    }

    public static void main(String[] args) {
        try {
	    	boolean success;
	        success = compile(args);
	        
	        if(!success)
	            System.exit(1);    
		}
        catch (Exception e) {
        	e.printStackTrace(System.err);
        	System.exit(1);
		}        
    }

    public static boolean compile(String[] args) {
        return new Main(null, null).run(args);
    }

    /**
     * Overriden in order to introduce some additional passes in the
     * compiler control flow.
     */
    public boolean run(String[] args) {   
        /*
	    // Make sure we have an instance of the structure model
    	if (model == null) {
    		model = new StructureModel();
    	}
    
    	if(Main.buildAsm){
    		// starting to build CaesarAsm........
    		CaesarAsmBuilder.preBuild(model);
    		//System.out.println("after preBuid");
    	}
    	*/
        
    	errorFound = false;

        if (!parseArguments(args)) {
            return false;
        }
        
        Log.setVerbose(options.verbose);
        
        KjcEnvironment environment = createEnvironment(options);
        setSourceVersion(environment.getSourceVersion());
        initialize(environment);
        
        if (infiles.size() == 0) {
            options.usage();
            Log.error(KjcMessages.NO_INPUT_FILE.getFormat());
            return false;
        }
        
        options.destination = checkDestination(options.destination);
        
        Log.verbose("compilation started");

        try {
            infiles = verifyFiles(infiles);
        }
        catch (UnpositionedError e) {
            reportTrouble(e);
            return false;
        }


        // KOPI step - parsing
        JCompilationUnit[] tree = parseFiles(environment);    
        if(errorFound) return false;
        
        // CJ general: collects externalized classes
        new JoinCollaborations(environment, this).joinAll(tree);
        if(errorFound) return false;
               
        // CJ VC: separate interface from implementation
        prepareCaesarClasses(environment, tree);
        
        // CJ Aspects: prepare the advices, which use joinpoint reflection
        prepareJoinpointReflection(tree);
        
        // KOPI step - resolves inheritance hierarchy
        joinAll(tree);                  
        if(errorFound) return false;
        
        // CJ VC 
        generateCaesarTypeSystem(environment, tree);
        if(errorFound) return false;
        
        // need Caesar type system
        // CJ Aspects: generate aspect deployment support classes and methods
        prepareAspectDeployment(environment, tree);
        if(errorFound) return false;
        
        // CJ VC 
        createMixinCloneTypeInfo(environment, tree[0]);
        
        // CJ VC
        createImplicitCaesarTypes(tree);
        if(errorFound) return false;
        
        // CJ VC
        adjustSuperTypes(tree);
        if(errorFound) return false;
  
        // CJ VC / Binding
        // this one generates factory and wrapper recycling methods
        generateSupportMembers(environment);
        if(errorFound) return false;
        
        // CJ Aspects:
        // must be called before check interfaces, but
        // lookup in type context must already work
        prepareStaticFieldDeployment(environment, tree);
        if(errorFound) return false;
        
        // KOPI step - check the interfaces of type declarations
        // this one adds type info about fields and methods
        checkAllInterfaces(tree); 
        if(errorFound) return false;
        
        // CJ VC
        // complete mixin interfaces here with infos we have till now
        // this step is necessary since access to fields defined in the enclosing cclass
        // work via accessor method which has to be declared in the interface
        completeCClassInterfaces(tree);
        if(errorFound) return false;        
        
        // CJ VC
        // start second check on all fields and signatures containing dependent types
        // note: these are all the types not resolved in the checkAllInterface pass
        checkDependentTypesInAllInterfaces(tree); 
        if(errorFound) return false;
        
        // CJ VC
        // this step generates missing mixin chain parts
        // (these are all the classes where the statically 
        // known super class parameter has been changed due to the linearization process) 
        // the temporary info generated is used in checkVirtualClassMethodSignatures
        completeMixinCloneTypeInfo(environment, tree[0]);
        
        // CJ VC 
        // check overridding in virtual classes
        // we have to keep the signature of a overridding method having virtual classes as
        // parameters equal to the first declaration introduced in one of the super-collaborations
        checkVirtualClassMethodSignatures(tree);
        if(errorFound) return false;
        
        // CJ VC
        // repeat the process here again
        // re-export the cclass interface (some signatures may have changed since last time)
        completeCClassInterfaces(tree);
        if(errorFound) return false;

        // CJ VC
        // repeat the process 
        completeMixinCloneTypeInfo(environment, tree[0]);
        
        // KOPI step - check class initialization, constructors? 
        checkAllInitializers(tree);
        if(errorFound) return false;
        
        // KOPI step - check inside the bodies of the methods 
        checkAllBodies(tree);
        if(errorFound) return false;
                
        byteCodeMap = new ByteCodeMap(options.destination);
        // KOPI step - generate byte code 
        genCode(environment.getTypeFactory());
         
        // CJ VC
        genMixinCopies(environment);
        if(errorFound) return false;
        
        // CJ Aspect: structure model preprocessing
        preWeaveProcessing(tree);
        
        tree = null;
        
        // CJ Aspect: Weaving
        if(!noWeaveMode())
            weaveGeneratedCode(environment.getTypeFactory());               
               
        CodeSequence.endSession();
        /*
        // CJ Aspect: structure model postprocessing
        if(Main.buildAsm){
        	CaesarAsmBuilder.postBuild(model);
        	if(Main.printAsm){
        		StructureModelDump dump = new StructureModelDump(System.out);
                System.out.println("== model after weaving ===============");
                dump.print("", model.getRoot());
                System.out.println("======================================");
        	}
        }
        */
        Log.verbose("compilation ended");
        
        return true;
    }


    protected void completeCClassInterfaces(JCompilationUnit[] tree) {
        Log.verbose("completeCClassInterfaces");
        for (int count = 0; count < tree.length; count++) {    
            try {
                tree[count].completeCClassInterfaces(this);
            }
            catch (PositionedError e) {
                reportTrouble(e);
            }
        }
    }

    protected void preWeaveProcessing(JCompilationUnit[] cu) {
        // redefine in subclass
    	if(Main.buildAsm){
	        for (int i = 0; i < cu.length; i++) {
	        	// note: AsmBuilder.build() starts the Visitor-part of AsmBuilder
	        	// iterating over the compilationunits and adding appropriate Nodes to 
	        	// the StructureModel.
	        	//System.out.println("before AsmBuilder.build");
	        	//CaesarAsmBuilder.build(cu[i], model);
	        	//System.out.println("after AsmBuilder.build");
	        }
		}
    }

    // checks that the plain method redefinition mechanism still works with VCs 
    protected void checkVirtualClassMethodSignatures(JCompilationUnit[] tree) {
        Log.verbose("checkVirtualClassMethodSignatures");
        for (int count = 0; count < tree.length; count++) {    
            try {
                tree[count].checkVirtualClassMethodSignatures(this);
            }
            catch (PositionedError e) {
                reportTrouble(e);
            }
        }
    }
    
    // generates factory methods and wrappee recycling    
    protected void generateSupportMembers(KjcEnvironment environment) {
        CClassPreparation.instance().generateSupportMethods(
            this,
            environment
        );
    }
    
    private void genMixinCopies(KjcEnvironment environment) 
    {
        JavaTypeGraph javaTypeGraph = environment.getCaesarTypeSystem().getJavaTypeGraph();
        Collection typesToGenerate = javaTypeGraph.getTypesToGenerate();
        
        ClassGenerator generator = new ClassGenerator(
        								options.destination, 
										options.destination, 
										byteCodeMap);
        
        for (Iterator it = typesToGenerate.iterator(); it.hasNext();) {
            JavaTypeNode item = (JavaTypeNode) it.next();

            try {
            	generator.generateClass(
                    item.getMixin().getQualifiedImplName(),
                    item.getQualifiedName(),
                    item.getParent().getQualifiedName(),
                    item.getOuter() != null ? item.getOuter().getQualifiedName() : null, 
                    environment.getCaesarTypeSystem()
                );
            }
            catch (MixerException e) {
                reportTrouble(
            		new UnpositionedError(
        				CaesarMessages.CLASS_GENERATOR,
						item.getQualifiedName(),
						e.getMessage()
					)
				);
            }
        }
        
    }

    /**
     * - generate export information for missing mixin chain parts 
     */
    protected void createMixinCloneTypeInfo(KjcEnvironment environment, JCompilationUnit cu) {
        CClassPreparation.instance().createMixinCloneTypeInfo(
            this, environment, cu
        );
    }
    
    /**
     * - generate export information for missing mixin chain parts 
     */
    protected void completeMixinCloneTypeInfo(KjcEnvironment environment, JCompilationUnit cu) {
        CClassPreparation.instance().completeMixinCloneTypeInfo(
            this, environment, cu
        );
    }
    
    /**
     * - create implicit types
     * - join created types
     * - ...
     */
    protected void createImplicitCaesarTypes(JCompilationUnit[] tree) {
        try {
            for (int i=0; i<tree.length; i++) {
                tree[i].createImplicitCaesarTypes(this);
            }
        }
        catch (PositionedError e) {
            reportTrouble(e);
        }
    }

    protected void adjustSuperTypes(JCompilationUnit[] tree) {
        try {
            for (int i=0; i<tree.length; i++) {
                tree[i].adjustSuperTypes(this);
            }
        }
        catch (PositionedError e) {
            reportTrouble(e);
        }
    }

    /**
     * - create advice attribute for AspectJ weaver if necessary
     * - add outer this if necessary
     * - check constructors
     * 	  - add super-constructor call if necessary
     *    - add call to initializer method
     *    - check super-constructor call
     *    - check constructor body
     * - check exception handling of checked exceptions
     * - check local variables initialization before usage
     * - check constructor call circularity
     * - check only abstract methods in abstract classes
     * - check signature of overriding methods compatible
     * - check of method bodies including type checking
     * - more checks...
     * 
     * - generation of bridge methods
     *   Notion of bridge methods not yet clear
     *   Probably related to support for Generics a la GJ
     */
    protected void checkAllBodies(JCompilationUnit[] tree) {
        Log.verbose("checkAllBodies");
        for (int count = 0; count < tree.length; count++) {
            checkBody(tree[count]);
        }
    }
    
    /**
     * generates dependency graph on source types
     */
    protected void generateCaesarTypeSystem(KjcEnvironment environment, JCompilationUnit[] tree) {
        Log.verbose("generateCaesarTypeSystem");        
        for (int i=0; i<tree.length; i++) {        	
            CaesarTypeGraphGenerator.instance().generateGraph(
        		environment.getCaesarTypeSystem().getCaesarTypeGraph(), tree[i]
            );
        }
        
        environment.getCaesarTypeSystem().generate(this);
    }
 
    /**
     * tasks: 
     * - check all final fields are initialized
     * - check for inheritance circularity
     * - check interfaces not implemented by superclasses
     * - more (TBD)
     */
    protected void checkAllInitializers(JCompilationUnit[] tree) {
        Log.verbose("checkAllInitializers");
        for (int count = 0; count < tree.length; count++) {
            checkInitializers(tree[count]);
        }
    }

    /**
     * A lot happens in this phase. We should divide this
     * into small steps.
     * 
     * - register type at CaesarBCEL world
     * - prepare for static deployment if necessary (create advice-, aspectOf-method etc.)
     * - check modifiers of classes
     * - collect static initializers and create initializer method
     * - collect instance initializers and create instance initializer method
     * - create default constructor if necessary
     * - traverse AST:
     * 		- check field and method modifiers
     * 		- check field and method signature
     * 		- create CSourceMethod/CSourceField for every field/method and enter them in corresponding CClass
     * - create perSingleton clause if necessary
     * - check pointcuts if necessary 
     */
    protected void checkAllInterfaces(JCompilationUnit[] tree) {
        Log.verbose("checkAllInterfaces");
        for (int count = 0; count < tree.length; count++) {
            checkInterface(tree[count]);
            //tree[count].accept(new DebugVisitor());
        }
    }

    protected void checkDependentTypesInAllInterfaces(JCompilationUnit[] tree) {
        Log.verbose("checkDependentTypesInAllInterfaces");
        for (int count = 0; count < tree.length; count++) {
            try {
                tree[count].checkDependentTypes(this);
            }
            catch (PositionedError e) {
                reportTrouble(e);
            }
        }
    }

    protected void prepareAspectDeployment(
        KjcEnvironment environment,
        JCompilationUnit[] tree) {
    	
        Log.verbose("prepare dynamic deployment");
        // Modify and generate support classes for dynamic deployment.
        GenerateDeploymentSupport genDeplSupport = new GenerateDeploymentSupport(this, environment);
        genDeplSupport.generateSupportClasses();
        
        Log.verbose("join deployment support classes");
        //Join generated deployment support declarations 
        for (int i = 0; i < tree.length; i++) {
            JCompilationUnit cu = tree[i];
            JoinDeploymentSupport.prepareForDynamicDeployment(this, cu);
        }
        
        Log.verbose("prepare static class deployment");
        // Prepare for static class deployment
        StaticDeploymentPreparation statDeplPrep = new StaticDeploymentPreparation(this, environment);
        for (int i = 0; i < tree.length; i++) {
            JCompilationUnit cu = tree[i];
            statDeplPrep.prepareForStaticDeployment(cu);
        }
        
        
    }

    /**
     * Following things happen here:
     * - create interface for cclass with original cclass name
     * - set the superinterface of interface to original cclass name
     * - append _Impl to superclass of each cclass 
     */
    protected void prepareCaesarClasses(
        KjcEnvironment environment,
        JCompilationUnit[] tree) {
        Log.verbose("prepareCaesarClasses");
        for (int i = 0; i < tree.length; i++) {
            JCompilationUnit cu = tree[i];
            CClassPreparation.instance().prepareCaesarClass(environment, cu);
        }
    }

    protected void prepareStaticFieldDeployment(KjcEnvironment environment,
    		JCompilationUnit[] tree) {
    	Log.verbose("prepare static field deployment");
        // Prepare for static field deployment
        StaticFieldDeploymentVisitor statFieldDepl = 
            new StaticFieldDeploymentVisitor(this, environment);
        for (int i = 0; i < tree.length; i++) {            
            tree[i].accept(statFieldDepl);
        }
    }
    
    protected void prepareJoinpointReflection(JCompilationUnit[] tree) {
        Log.verbose("prepareJoinpointReflection");
        //Handle Join Point Reflection.
        JoinPointReflectionVisitor joinPointReflection = 
            new JoinPointReflectionVisitor();
        for (int i = 0; i < tree.length; i++) {            
            tree[i].accept(joinPointReflection);
        }
    }

    protected JCompilationUnit[] parseFiles(KjcEnvironment environment) {
        Log.verbose("parseFiles");
        JCompilationUnit[] tree = new JCompilationUnit[infiles.size()];
        for (int count = 0; count < tree.length; count++) {
            tree[count] =
                parseFile((File)infiles.elementAt(count), environment);
        }
        return tree;
    }

    protected void checkInterface(JCompilationUnit cunit) {
        super.checkInterface(cunit);
    }

    /**
     * In this phase the following things happen:
     * - load all imported classes via classloader
     * - create CClass object for each imported class
     * - register loaded classes in CompilationUnit.allLoadedClasses
     * - add type declarations in compilation units in "allLoadedClasses"
     * - resolve superclasses and create CClassOrInterface type for superclass
     * - check conditions on superclasses like accessibility, superclass not final, superclass not interface
     * - similarly for implemented interfaces: resolve, create CClassOrInterface type, check for "is interface", accessiblity, circularity
     */

    protected void joinAll(JCompilationUnit[] tree) {
        Log.verbose("joinAll");
        JCompilationUnit cunit;

        for (int i = 0; i < tree.length; i++) {
            cunit = tree[i];
            // perform a first join pass to resolve all
            // directly known (i.e. specified) superclasses
            join(cunit);
        }
        if (errorFound)
            return;
    }
   

    /**
     * Create AST via parser/scanner
     * In addition, the following actions are performed:
     * - create empty CClass placeholder for every class (to be filled later)
     * - register types in classreader/allLoadedClasses 
     */
    protected JCompilationUnit parseFile(
        File file,
        KjcEnvironment environment) {
        InputBuffer buffer;

        try {
            buffer = new InputBuffer(file, options.encoding);
        }
        catch (UnsupportedEncodingException e) {
            reportTrouble(
                new UnpositionedError(
                    Messages.UNSUPPORTED_ENCODING,
                    options.encoding));
            return null;
        }
        catch (IOException e) {
            reportTrouble(
                new UnpositionedError(
                    Messages.IO_EXCEPTION,
                    file.getPath(),
                    e.getMessage()));
            return null;
        }

        //create the Caesar parser
        CaesarParser parser;
        JCompilationUnit unit;
        long lastTime = System.currentTimeMillis();

        parser = new CaesarParser(this, buffer, environment);

        try {
            unit = getJCompilationUnit(parser);

        }
        catch (ParserException e) {
            reportTrouble(parser.beautifyParseError(e));
            unit = null;
        }
        catch (Exception e) {
            e.printStackTrace();
            errorFound = true;
            unit = null;
        }

        Log.verbose("file parsed: "+file.getPath());

        try {
            buffer.close();
        }
        catch (IOException e) {
            reportTrouble(
                new UnpositionedError(
                    Messages.IO_EXCEPTION,
                    file.getPath(),
                    e.getMessage()));
        }

        return unit;
    }

    protected JCompilationUnit getJCompilationUnit(CaesarParser parser)
        throws ParserException {
        return parser.jCompilationUnit();
    }

    /**
     * IVICA Performs weaving after compilation.
     * 
     * @param factory
     */
    public void weaveGeneratedCode(TypeFactory factory) {
        String filename;

        this.classes.setSize(0);

        weaver = new CaesarWeaver(options.destination);
        
        for(Iterator it=byteCodeMap.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();

            String fileName = (String)entry.getKey();
            byte[] byteCodeBuf = (byte[])entry.getValue();

            weaver.addUnwovenClassFile(fileName, byteCodeBuf);
        }

        weaveClasses();
    }

    /**
     * Weaves the given classes.
     * The weaver is also responsible for file output.
     * 
     * @param unwovenClassFiles
     */
    protected void weaveClasses() {
    	/* Reset the world for the weaver, so that it does not see source files */
    	CaesarBcelWorld.createInstance();
        CaesarBcelWorld bcelWorld = CaesarBcelWorld.getInstance();

        //tells the weaver whether it should inline the around advices in the calling code,
        //leads to better performance
        bcelWorld.setXnoInline(true);

        Log.verbose("weaver started...");

        try {            
            //perform weaving
        	weaver.performWeaving(bcelWorld);

            /*
            for (int i = 0; i < weaver.fileCount(); i++) {
                Log.verbose("weaver wrote class file: "+weaver.getFileName(i));
            }
            */
            
            Log.verbose("...weaver finished");
            
        }
        catch (IOException e) {
            Log.verbose("...weaver failed");
            reportTrouble(new UnpositionedError(CaesarMessages.WEAVING_FAILED));
        }
        catch (UnpositionedError e) {
            Log.verbose("...weaver failed");
            reportTrouble(e);
        }
        catch (PositionedError e) {
            Log.verbose("...weaver failed");
            reportTrouble(e);
        }
    }

    public boolean noWeaveMode() {
        //XXX Should be determined by the compiler options.
        return false;
    }

    protected void initialize(KjcEnvironment environment) {
        super.initialize(environment);

        messageHandler = new CaesarMessageHandler(this);
        
        // create static instance of bcel world
        CaesarBcelWorld.createInstance(options.classpath);
        
        CaesarBcelWorld world = CaesarBcelWorld.getInstance();
        world.setMessageHandler(messageHandler);
        
        /*
        // TODO make this optional, as command line argument
        CaesarBcelWorld.getInstance().getWorld().setModel(model);
        
        model.setRoot(
            new ProgramElementNode(
                "<root>", 
                ProgramElementNode.Kind.FILE_JAVA, 
                new LinkedList())
        );
        */
    }
}
