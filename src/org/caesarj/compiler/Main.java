package org.caesarj.compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.weaver.bcel.BcelWeaver;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.compiler.aspectj.CaesarMessageHandler;
import org.caesarj.compiler.ast.FjCompilationUnit;
import org.caesarj.compiler.ast.FjSourceClass;
import org.caesarj.compiler.tools.antlr.extra.InputBuffer;
import org.caesarj.compiler.tools.antlr.runtime.ParserException;
import org.caesarj.compiler.util.ClassTransformationFjVisitor;
import org.caesarj.compiler.util.CollaborationInterfaceTransformation;
import org.caesarj.compiler.util.CollectClassesFjVisitor;
import org.caesarj.compiler.util.DebugVisitor;
import org.caesarj.compiler.util.FamiliesInitializerFjVisitor;
import org.caesarj.compiler.util.FjVisitor;
import org.caesarj.compiler.util.InheritConstructorsFjVisitor;
import org.caesarj.compiler.util.JoinPointReflectionVisitor;
import org.caesarj.compiler.util.MethodTransformationFjVisitor;
import org.caesarj.compiler.util.ResolveSuperClassFjVisitor;
import org.caesarj.kjc.BytecodeOptimizer;
import org.caesarj.kjc.CSourceClass;
import org.caesarj.kjc.CodeSequence;
import org.caesarj.kjc.Constants;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.KjcEnvironment;
import org.caesarj.kjc.KjcMessages;
import org.caesarj.kjc.TypeFactory;
import org.caesarj.util.Message;
import org.caesarj.util.Utils;
import org.omg.CORBA.Environment;

/**
 * The entry point of the Caesar compiler.
 * 
 * 
 * @author J?rgen Hallpap
 */
public class Main extends org.caesarj.kjc.Main implements Constants {

	private IMessageHandler messageHandler;
	protected Vector compilationUnits;
	private Set errorMessages;
	private CollectClassesFjVisitor inherritConstructors;
	protected boolean joined;

	public Main(String workingDirectory, PrintWriter diagnosticOutput) {
		super(workingDirectory, new Outputter(diagnosticOutput));
		//		super(workingDirectory, diagnosticOutput  );
	}

	public static void main(String[] args) {
		boolean success;
		success = compile(args);
		System.exit(success ? 0 : 1);
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
		joined = false;
		compilationUnits = null;

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

		if (verboseMode()) {
			inform(
				CompilerMessages.COMPILATION_STARTED,
				new Integer(infiles.size()));
		}

		try {
			infiles = verifyFiles(infiles);
		} catch (UnpositionedError e) {
			reportTrouble(e);
			return false;
		}
		
		System.out.println("PARSER:");
		options.destination = checkDestination(options.destination);
		JCompilationUnit[] tree = new JCompilationUnit[infiles.size()];
		for (int count = 0; count < tree.length; count++) {
			tree[count] =
				parseFile((File) infiles.elementAt(count), environment);
			//tree[count].accept(new DebugVisitor());
		}

		infiles = null;
		if (errorFound) {
			return false;
		}

		if (!options.beautify) {
			long lastTime = System.currentTimeMillis();

				
			//Handle Join Point Reflection.
			for (int i = 0; i < tree.length; i++) {
				tree[i].accept(new JoinPointReflectionVisitor());
			}

			//Modify and generate support classes for dynamic deployment.
			for (int i = 0; i < tree.length; i++) {
				FjCompilationUnit cu = (FjCompilationUnit) tree[i];
				cu.prepareForDynamicDeployment(environment);
			}
			
			for (int i = 0; i < tree.length; i++) {
				tree[i].accept(getClassTransformation(environment));
				//tree[i].accept(new DebugVisitor());
			}
						
			System.out.println("JOIN:");
			for (int count = 0; count < tree.length; count++) {
				join(tree[count]);
				//tree[count].accept(new DebugVisitor());
			}

			if (errorFound) {
				return false;
			}
			System.out.println("CHECK_INTERFACE:");
			for (int count = 0; count < tree.length; count++) {
				checkInterface(tree[count]);
				//tree[count].accept(new DebugVisitor());
			}
			if (verboseMode()) {
				inform(
					CompilerMessages.INTERFACES_CHECKED,
					new Long(System.currentTimeMillis() - lastTime));
			}

			if (errorFound) {
				return false;
			}
			
			System.out.println("INIT FAMILY:");
			//Walter: Now the families are initializated in a new round
			for (int count = 0; count < tree.length; count++)
			{
				initFamilies(tree[count]);
				//tree[count].accept(new DebugVisitor());
			}

			if (errorFound)
				return false;
			System.out.println("CHECK INITIALIZERS:");
			for (int count = 0; count < tree.length; count++) {
				checkInitializers(tree[count]);
			}

			if (errorFound) {
				return false;
			}
			System.out.println("CHECK BODY:");
			for (int count = 0; count < tree.length; count++) {
				checkBody(tree[count]);
				//tree[count].accept(new DebugVisitor());
				
				if (!options._java
					&& !options.beautify
					&& !(environment.getAssertExtension()
						== KjcEnvironment.AS_ALL)) {
					tree[count] = null;
				}
			}

			if (errorFound) {
				return false;
			}

			if (environment.getAssertExtension() == KjcEnvironment.AS_ALL) {
				for (int count = 0; count < tree.length; count++) {
					checkCondition(tree[count]);
					if (!options._java && !options.beautify) {
						tree[count] = null;
					}
				}

				if (errorFound) {
					return false;
				}
			}
		}

		if (!options.nowrite) {
			if (options._java || options.beautify) {
				for (int count = 0; count < tree.length; count++) {
					generateJavaCode(tree[count], environment.getTypeFactory());
					tree[count] = null;
				}
			} else {
				if (noWeaveMode()) {
					//just generate the code
					genCode(environment.getTypeFactory());

				} else {
					//generate code and perform weaving
					generateAndWeaveCode(environment.getTypeFactory());

				}
			}
		}

		if (verboseMode()) {
			inform(CompilerMessages.COMPILATION_ENDED);
		}

		CodeSequence.endSession();
		return true;
	}

	
	protected void checkInterface(JCompilationUnit cunit) {
		cunit.accept(getMethodTransformation(cunit.getEnvironment()));
		super.checkInterface(cunit);
	}

	protected void join(JCompilationUnit cunit) {
		if (!joined) {
			joined = true;

			for (int i = 0; i < compilationUnits.size(); i++) {
				cunit = (JCompilationUnit) compilationUnits.elementAt(i);
				// perform a first join pass to resolve all
				// directly known (i.e. specified) superclasses
				super.join(cunit);
			}
			if (errorFound)
				return;

			// let a visitor traverse the tree
			// and resolve all overriders superclasses
			getResolveSuperClass(this, compilationUnits).transform();
			if (errorFound)
				return;

			try {
				Vector warnings =
					getInheritConstructors(compilationUnits).transform();
				for (int i = 0; i < warnings.size(); i++) {
					inform((PositionedError) warnings.elementAt(i));
				}
			} catch (PositionedError e) {
				reportTrouble(e);
			}
		}
	}

	public void reJoin(JCompilationUnit unit) {
		super.join(unit);
	}
	
	/**
	 * Initializes the families of the compilation unit passed.
	 * 
	 * @param cunit
	 */
	protected void initFamilies(JCompilationUnit cunit)
	{
		cunit.accept(getFamiliesInitializer(cunit.getEnvironment()));
	}

	protected FjVisitor getClassTransformation(KjcEnvironment environment) 
	{
		return new ClassTransformationFjVisitor(environment);
	}
	
	/**
	 * Returns the visitor instance for transforms the CIs.
	 * @param environment
	 * @return
	 */
	protected FjVisitor getCollaborationInteraceTransformation(
		KjcEnvironment environment) 
	{
		return new CollaborationInterfaceTransformation(environment, this);
	}
	
	
	/**
	 * Returns the visitor instance for initializes the families.
	 * @param environment
	 * @return
	 */
	protected FjVisitor getFamiliesInitializer(KjcEnvironment environment)
	{
		return new FamiliesInitializerFjVisitor(this, environment);
	}

	protected CollectClassesFjVisitor getInheritConstructors(Vector compilationUnits) {
		if (inherritConstructors == null)
			inherritConstructors =
				new InheritConstructorsFjVisitor(compilationUnits);
		return inherritConstructors;
	}

	protected FjVisitor getMethodTransformation(KjcEnvironment environment) {
		return new MethodTransformationFjVisitor(environment);
	}

	protected ResolveSuperClassFjVisitor getResolveSuperClass(
		Compiler compiler,
		Vector compilationUnits) {
		return new ResolveSuperClassFjVisitor(compiler, compilationUnits);
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

	protected JCompilationUnit parseFile(
		File file,
		KjcEnvironment environment) {
		InputBuffer buffer;

		if (compilationUnits == null) {
			// we are introducing some more state here
			// since the regular compiler would call the
			// join method once for each compilation unit
			// but we need kind of a different flow
			compilationUnits = new Vector();
		}

		try {
			buffer = new InputBuffer(file, options.encoding);
		} catch (UnsupportedEncodingException e) {
			reportTrouble(
				new UnpositionedError(
					CompilerMessages.UNSUPPORTED_ENCODING,
					options.encoding));
			return null;
		} catch (IOException e) {
			reportTrouble(
				new UnpositionedError(
					CompilerMessages.IO_EXCEPTION,
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
			compilationUnits.add(unit);
			unit.accept(getCollaborationInteraceTransformation(environment));

		} catch (ParserException e) {
			reportTrouble(parser.beautifyParseError(e));
			unit = null;
		} catch (Exception e) {
			e.printStackTrace();
			errorFound = true;
			unit = null;
		}

		if (verboseMode()) {
			inform(
				CompilerMessages.FILE_PARSED,
				file.getPath(),
				new Long(System.currentTimeMillis() - lastTime));
		}

		try {
			buffer.close();
		} catch (IOException e) {
			reportTrouble(
				new UnpositionedError(
					CompilerMessages.IO_EXCEPTION,
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
	 * Performs weaving after compilation.
	 * 
	 * @param factory
	 */
	public void generateAndWeaveCode(TypeFactory factory) {
		CSourceClass[] classes = getClasses();
		BytecodeOptimizer optimizer = new BytecodeOptimizer(options.optimize);
		List unwovenClassFiles = new ArrayList();
		byte[] classBuffer;
		String filename;

		this.classes.setSize(0);

		try {
			for (int count = 0; count < classes.length; count++) {
				long lastTime = System.currentTimeMillis();

				classBuffer =
					((FjSourceClass) classes[count]).genCodeToBuffer(
						optimizer,
						options.destination,
						factory);

				unwovenClassFiles.add(
					new UnwovenClassFile(
						getFileName(classes[count]),
						classBuffer));
				if (verboseMode() && !classes[count].isNested()) {
					inform(
						CompilerMessages.CLASSFILE_GENERATED,
						classes[count].getQualifiedName().replace('/', '.'),
						new Long(System.currentTimeMillis() - lastTime));
				}

				classes[count] = null;
			}

			weaveClasses(
				(UnwovenClassFile[]) unwovenClassFiles.toArray(
					new UnwovenClassFile[0]));

		} catch (PositionedError e) {
			reportTrouble(e);
		} catch (ClassFileFormatException e) {
			e.printStackTrace();
			reportTrouble(
				new UnpositionedError(
					CompilerMessages.FORMATTED_ERROR,
					e.getMessage()));
		} catch (IOException e) {
			reportTrouble(new UnpositionedError(
				CompilerMessages.IO_EXCEPTION,
				"classfile",
			//!!!FIXME !!!
			e.getMessage()));
		}
	}

	/**
	 * Weaves the given classes.
	 * The weaver is also responsible for file output.
	 * 
	 * @param unwovenClassFiles
	 */
	protected void weaveClasses(UnwovenClassFile[] unwovenClassFiles) {
		BcelWorld bcelWorld = CaesarBcelWorld.getInstance();

		//tells the weaver whether it should inline the around advices in the calling code,
		//leads to better performance
		bcelWorld.setXnoInline(true);
		BcelWeaver weaver = new BcelWeaver(bcelWorld);

		for (int i = 0; i < unwovenClassFiles.length; i++) {
			weaver.addClassFile(unwovenClassFiles[i]);
		}

		if (verboseMode()) {
			inform(CaesarMessages.WEAVING_STARTED);
		}

		try {

			//perform weaving
			weaver.weave();

			if (verboseMode()) {
				for (int i = 0; i < unwovenClassFiles.length; i++) {
					inform(
						CaesarMessages.WROTE_CLASS_FILE,
						unwovenClassFiles[i].getFilename());
				}
				inform(CaesarMessages.WEAVING_ENDED);
			}

		} catch (IOException e) {
			reportTrouble(new UnpositionedError(CaesarMessages.WEAVING_FAILED));
		} catch (AbortException e) {
			reportTrouble(
				new PositionedError(
					new TokenReference(
						e
							.getIMessage()
							.getISourceLocation()
							.getSourceFile()
							.getName(),
						e.getIMessage().getISourceLocation().getLine()),
					new Message(CaesarMessages.WEAVER_ERROR, e.getMessage())));
		}

	}

	protected String getFileName(CSourceClass sourceClass) {
		String destination = options.destination;
		String[] classPath =
			Utils.splitQualifiedName(sourceClass.getQualifiedName());
		if (destination == null || destination.equals("")) {
			destination = System.getProperty("user.dir");
		}

		if (classPath[0] != null && !classPath[0].equals("")) {
			// the class is part of a package
			destination += File.separator
				+ classPath[0].replace('/', File.separatorChar);
		}

		String filename =
			destination
				+ File.separatorChar
				+ classPath[classPath.length
				- 1]
				+ ".class";
		return filename;
	}

	public boolean noWeaveMode() {
		//XXX Should be determined by the compiler options.
		return false;
	}

	protected void initialize(KjcEnvironment environment) {
		super.initialize(environment);

		messageHandler = new CaesarMessageHandler(this);
		CaesarBcelWorld world = CaesarBcelWorld.getInstance();
		world.setMessageHandler(messageHandler);
	}

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
			} else
				super.println();
		}
		public void write(String s) {
			s = CciConstants.removeCaesarInternalNames(s);
			if (otherPrinter != null) {
				otherPrinter.write(s);
				//super.write( s );
			} else
				super.write(s);
		}
		/**
		 * This was inserted because it was not working well
		 * when the otherPrinter was present.
		 * @author Walter Augusto Werner
		 */
		public void flush()
		{
			if (otherPrinter != null)
				otherPrinter.flush();
			else
				super.flush();
		}		
		
	}

}
