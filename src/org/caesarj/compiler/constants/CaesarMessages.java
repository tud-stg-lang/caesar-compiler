package org.caesarj.compiler.constants;

import org.caesarj.util.MessageDescription;

public interface CaesarMessages extends org.caesarj.util.Messages {
  MessageDescription	COMPILATION_STARTED = new MessageDescription("[ start compilation in verbose mode ]", null, 4);
  MessageDescription	FILE_PARSED = new MessageDescription("[ parsed {0} in {1} ms ]", null, 4);
  MessageDescription	INTERFACES_CHECKED = new MessageDescription("[ checked interfaces in {0} ms ]", null, 4);
  MessageDescription	BODY_CHECKED = new MessageDescription("[ checked body of {0} in {1} ms ]", null, 4);
  MessageDescription	CONDITION_CHECKED = new MessageDescription("[ checked condition of {0} in {1} ms ]", null, 4);
  MessageDescription	CLASSFILE_GENERATED = new MessageDescription("[ optimized and generated {0} in {1} ms ]", null, 4);
  MessageDescription	JAVA_CODE_GENERATED = new MessageDescription("[ generated {0} ]", null, 4);
  MessageDescription	CLASS_LOADED = new MessageDescription("[ loaded {0} ]", null, 4);
  MessageDescription	COMPILATION_ENDED = new MessageDescription("[ compilation ended ]", null, 4);
  MessageDescription	WEAVING_STARTED = new MessageDescription("[ weaving started ]", null, 4);
  MessageDescription	WEAVING_ENDED = new MessageDescription("[ weaving ended ]", null, 4);
  MessageDescription	WROTE_CLASS_FILE = new MessageDescription("[ wrote class file: {0} ]", null, 4);
  MessageDescription	WEAVING_FAILED = new MessageDescription("weaving failed", null, 0);
  MessageDescription	PROCEED_OUTSIDE_AROUND_ADVICE = new MessageDescription("proceed() can only be used inside an around-advice", null, 0);
  MessageDescription	ADVICE_FLAGS = new MessageDescription("only strictfp modifier allowed for Advices", null, 0);
  MessageDescription	WEAVER_ERROR = new MessageDescription("{0}", null, 0);
  MessageDescription	ASPECTJ_ERROR = new MessageDescription("{0}", null, 0);
  MessageDescription	ASPECTJ_WARNING = new MessageDescription("{0}", null, 2);
  MessageDescription	POINTCUTS_OR_ADVICES_IN_NON_CROSSCUTTING_CLASS = new MessageDescription("pointcuts and advices may be declared in crosscutting or statically deployed classes only", null, 0);
  MessageDescription	DEPLOYED_CLASS_CONSTRUCTOR_NON_PRIVATE_OR_WITH_PARAMETER = new MessageDescription("the constructor of a statically deployed class must be private and must not have any parameters", null, 0);
  MessageDescription	DEPLOYED_FIELD_NOT_FINAL_AND_STATIC = new MessageDescription("statically deployed fields must be declared final and static", null, 0);
  MessageDescription	DEPLOYED_CLASS_NOT_CROSSCUTTING = new MessageDescription("only instances of crosscutting classes can be deployed", null, 0);
  MessageDescription	DESCENDANT_OF_CROSSCUTTING_CLASS_NOT_DECLARED_CROSSCUTTING = new MessageDescription("descendants of crosscutting classes must be declared crosscutting", null, 0);
  MessageDescription	VIRTUAL_ACCESSING_OUTER_PRIVATE = new MessageDescription("Virtual or override class \"{0}\" cannot access private methods or fields of the enclosing class.", null, 0);
  MessageDescription	VIRTUAL_CALLING_OUTER_OUTER = new MessageDescription("Virtual or override class \"{0}\" cannot call methods or access fields defined in the enclosing class of its outer class.", null, 0);
  
  // IVICA
  MessageDescription    NAME_CLASH = new MessageDescription("Name clash for member \"{0}\" detected.", null, 0);
  MessageDescription    FATAL_ERROR = new MessageDescription("Fatal Error.", null, 0);
}
