package org.caesarj.compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.aspectj.asm.StructureModelManager;
import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.compiler.aspectj.CaesarMessageHandler;
import org.caesarj.compiler.aspectj.CaesarWeaver;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.cclass.CClassPreparation;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.joinpoint.DeploymentPreparation;
import org.caesarj.compiler.joinpoint.JoinPointReflectionVisitor;
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
     * @param workingDirectory the working directory
     * @param diagnosticOutput where to put stderr
     * 
     */
    public Main(String workingDirectory, PrintWriter diagnosticOutput) {
        super(workingDirectory, new Outputter(diagnosticOutput));
        //		super(workingDirectory, diagnosticOutput  );
    }

    public static void main(String[] args) {
        try {
	    	boolean success;
	        success = compile(args);
		}
        catch (Exception e) {
        	e.printStackTrace();
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
        errorFound = false;

        if (!parseArguments(args)) {
            return false;
        }
        
        KjcEnvironment environment = createEnvironment(options);
        setSourceVersion(environment.getSourceVersion());
        initialize(environment);
        
        if (infiles.size() == 0) {
            options.usage();
            inform(KjcMessages.NO_INPUT_FILE);
            return false;
        }
        
        options.destination = checkDestination(options.destination);
        
        if (verboseMode()) {
            inform(CaesarMessages.COMPILATION_STARTED, new Integer(infiles.size()));
        }

        try {
            infiles = verifyFiles(infiles);
        }
        catch (UnpositionedError e) {
            reportTrouble(e);
            return false;
        }


        JCompilationUnit[] tree = parseFiles(environment);    
        if(errorFound) return false;
        
        prepareCaesarClasses(environment, tree);
        
        prepareJoinpointReflection(tree);
        
        prepareDynamicDeployment(environment, tree);
                
        joinAll(tree);                  
        if(errorFound) return false;
        
        generateCaesarTypeSystem(environment, tree);        

        createMixinCloneTypeInfo(environment, tree[0]);
        
        createImplicitCaesarTypes(tree);
        if(errorFound) return false;
        
        adjustSuperTypes(tree);
        if(errorFound) return false;
        
        generateSupportMembers(environment);
        if(errorFound) return false;
        
        checkAllInterfaces(tree); 
        if(errorFound) return false;
        
        completeMixinCloneTypeInfo(environment, tree[0]);
                
        checkAllInitializers(tree);
        if(errorFound) return false;
                
        checkAllBodies(tree);
        if(errorFound) return false;

        byteCodeMap = new ByteCodeMap(options.destination);
        genCode(environment.getTypeFactory());
         
        genMixinCopies(environment);
        if(errorFound) return false;
               
        
        /*
        AsmBuilder builder = new AsmBuilder(CaesarBcelWorld.getInstance().getWorld().getModel());
        for (int i = 0; i < tree.length; i++) {
            tree[i].accept(builder);
        }
                
        {
        StructureModelDump dump = new StructureModelDump(System.out);
        System.out.println("== model before weaving ==============");
        dump.print("", CaesarBcelWorld.getInstance().getWorld().getModel().getRoot());
        System.out.println("======================================");
    	}
    	*/
        
        preWeaveProcessing(tree);
        
        tree = null;
        
        if(!noWeaveMode())
            weaveGeneratedCode(environment.getTypeFactory());
        
        if(verboseMode())
            inform(CaesarMessages.COMPILATION_ENDED);
               
        CodeSequence.endSession();
        
        /*
        {
        StructureModelDump dump = new StructureModelDump(System.out);
        System.out.println("== model after weaving ===============");
        dump.print("", CaesarBcelWorld.getInstance().getWorld().getModel().getRoot());
        System.out.println("======================================");
        }
        */

        
        return true;
    }


    protected void preWeaveProcessing(JCompilationUnit[] cu) {
        // redefine in subclass
    }

    // generates factory methods and wrappee recycling    
    private void generateSupportMembers(KjcEnvironment environment) {
        try {
            CClassPreparation.instance().generateSupportMethods(
                this,
                environment
            );
        }
        catch (UnpositionedError e) {
            reportTrouble(e);
        }
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
        System.out.println("checkAllBodies");
        for (int count = 0; count < tree.length; count++) {
            checkBody(tree[count]);
        }
    }
    
    /**
     * generates dependency graph on source types
     */
    protected void generateCaesarTypeSystem(KjcEnvironment environment, JCompilationUnit[] tree) {
        System.out.println("generateCaesarTypeSystem");        
        for (int i=0; i<tree.length; i++) {        	
            CaesarTypeGraphGenerator.instance().generateGraph(
        		environment.getCaesarTypeSystem().getCaesarTypeGraph(), tree[i]
            );
        }
        
        environment.getCaesarTypeSystem().generate();
    }
 
    /**
     * tasks: 
     * - check all final fields are initialized
     * - check for inheritance circularity
     * - check interfaces not implemented by superclasses
     * - more (TBD)
     */
    protected void checkAllInitializers(JCompilationUnit[] tree) {
        System.out.println("checkAllInitializers");
        for (int count = 0; count < tree.length; count++) {
            checkInitializers(tree[count]);
        }
    }

    /**
     * A lot happens in this phase. We should divide this
     * into small steps.
     * 
     * - create self-enabled methods in clean classes
     * - create factory methods for virtual classes
     * - create wrapper creater/destructor methods
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
        System.out.println("checkAllInterfaces");
        for (int count = 0; count < tree.length; count++) {
            checkInterface(tree[count]);
            //tree[count].accept(new DebugVisitor());
        }
    }

    protected void prepareDynamicDeployment(
        KjcEnvironment environment,
        JCompilationUnit[] tree) {
        System.out.println("prepareDynamicDeployment");
        //Modify and generate support classes for dynamic deployment.
        for (int i = 0; i < tree.length; i++) {
            JCompilationUnit cu = tree[i];
            DeploymentPreparation.prepareForDynamicDeployment(environment, cu);
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
        System.out.println("prepareCaesarClasses");
        for (int i = 0; i < tree.length; i++) {
            JCompilationUnit cu = tree[i];
            CClassPreparation.instance().prepareCaesarClass(environment, cu);
        }
    }

    protected void prepareJoinpointReflection(JCompilationUnit[] tree) {
        System.out.println("prepareJoinpointReflection");
        //Handle Join Point Reflection.
        JoinPointReflectionVisitor joinPointReflection = 
            new JoinPointReflectionVisitor();
        for (int i = 0; i < tree.length; i++) {            
            tree[i].accept(joinPointReflection);
        }
    }

    protected JCompilationUnit[] parseFiles(KjcEnvironment environment) {
        System.out.println("parseFiles");
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
        System.out.println("joinAll");
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

    public void inform(PositionedError error) {
        if (errorMessages == null)
            errorMessages = new HashSet();

        // report every messages once only
        if (errorMessages.contains(error.getMessage()))
            return;
        else
            errorMessages.add(error.getMessage());

        super.inform(error);
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

        if (verboseMode()) {
            inform(
                CaesarMessages.FILE_PARSED,
                file.getPath(),
                new Long(System.currentTimeMillis() - lastTime));
        }

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

        weaver = new CaesarWeaver();
        
        for(Iterator it=byteCodeMap.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();

            String fileName = (String)entry.getKey();
            byte[] byteCodeBuf = (byte[])entry.getValue();

            weaver.addUnwovenClassFile(fileName, byteCodeBuf);
        }

        weaveClasses();
    }

    /**
     * Performs weaving after compilation.
     * 
     * @param factory
     */
    /*
    public void generateAndWeaveCode(TypeFactory factory) {
        CSourceClass[] classes = getClasses();
        BytecodeOptimizer optimizer = new BytecodeOptimizer(options.optimize);
        byte[] classBuffer;
        String filename;

        this.classes.setSize(0);

        weaver = new CaesarWeaver();
        try {
            for (int count = 0; count < classes.length; count++) {
                long lastTime = System.currentTimeMillis();

                classBuffer =
                    classes[count].genCodeToBuffer(
                        optimizer,
                        options.destination,
                        factory);

                weaver.addUnwovenClassFile(
                    getFileName(classes[count]),
                    classBuffer);

                if (verboseMode() && !classes[count].isNested()) {
                    inform(
                        CaesarMessages.CLASSFILE_GENERATED,
                        classes[count].getQualifiedName().replace('/', '.'),
                        new Long(System.currentTimeMillis() - lastTime));
                }

                classes[count] = null;
            }

            weaveClasses();

        }
        catch (PositionedError e) {
            reportTrouble(e);
        }
        catch (ClassFileFormatException e) {
            e.printStackTrace();
            reportTrouble(
                new UnpositionedError(
                    Messages.FORMATTED_ERROR,
                    e.getMessage()));
        }
        catch (IOException e) {
            reportTrouble(new UnpositionedError(
                Messages.IO_EXCEPTION,
                "classfile",
            //!!!FIXME !!!
            e.getMessage()));
        }
    }
    */

    /**
     * Weaves the given classes.
     * The weaver is also responsible for file output.
     * 
     * @param unwovenClassFiles
     */
    protected void weaveClasses() {
        CaesarBcelWorld bcelWorld = CaesarBcelWorld.getInstance();

        //tells the weaver whether it should inline the around advices in the calling code,
        //leads to better performance
        bcelWorld.setXnoInline(true);

        if (verboseMode()) {
            inform(CaesarMessages.WEAVING_STARTED);
        }

        try {

            //perform weaving
            weaver.performWeaving(bcelWorld);

            if (verboseMode()) {
                for (int i = 0; i < weaver.fileCount(); i++) {
                    inform(
                        CaesarMessages.WROTE_CLASS_FILE,
                        weaver.getFileName(i));
                }
                inform(CaesarMessages.WEAVING_ENDED);
            }

        }
        catch (IOException e) {
            reportTrouble(new UnpositionedError(CaesarMessages.WEAVING_FAILED));
        }
        catch (CaesarWeaver.WeavingException we) {
            reportTrouble(we.getError());
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
        
        CaesarBcelWorld.getInstance().getWorld().setModel(
            StructureModelManager.INSTANCE.getStructureModel());
    }

    /**
     * used to redirect System.out for tests.
     *
     */
    protected static class Outputter extends PrintWriter {
        protected PrintWriter otherPrinter;
        public Outputter(PrintWriter otherPrinter) {
            super(System.out);
            this.otherPrinter = otherPrinter;
        }
        public void println() {
            if (otherPrinter != null) {
                otherPrinter.println();
                //super.println();
            }
            else
                super.println();
        }

        /**
         * if otherPrinter is set, write s to otherPrinter, else to System.out
         * 
         * @param s the String to be written
         */
        public void write(String s) {
            if (otherPrinter != null) {
                otherPrinter.write(s);
                //super.write( s );
            }
            else
                super.write(s);
        }
        /**
         * This was inserted because it was not working well
         * when the otherPrinter was present.
         * @author Walter Augusto Werner
         */
        public void flush() {
            if (otherPrinter != null)
                otherPrinter.flush();
            else
                super.flush();
        }

    }

}
