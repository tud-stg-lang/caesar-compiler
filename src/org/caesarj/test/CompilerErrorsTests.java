package org.caesarj.test;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.KjcMessages;

public class CompilerErrorsTests extends FjTestCase {

	public CompilerErrorsTests(String name) {
		super(name);
	}
		
	/* cyclic dependencies */
	public void testCaesarTestCase_200() throws Throwable {
        compileAndCheckErrorMessage("test200", KjcMessages.CLASS_CIRCULARITY);
    }
	
	/* class in cclass */
	public void testCaesarTestCase_201() throws Throwable {
	    compileAndCheckErrorMessage("test201", CaesarMessages.SYNTAX_ERROR);
    }
	
	/* cclass in class */
	public void testCaesarTestCase_202() throws Throwable {
	    compileAndCheckErrorMessage("test202", CaesarMessages.SYNTAX_ERROR);
    }
	
	/* cclass inherit class */
	public void testCaesarTestCase_203() throws Throwable {
	    compileAndCheckErrorMessage("test203", CaesarMessages.CCLASS_SUPER_NOT_FOUND);
    }
	
	/* class extends cclass */
	public void testCaesarTestCase_204() throws Throwable {
        compileAndCheckErrorMessage("test204", null);
    }
	
	/* class implements cclass */
	public void testCaesarTestCase_205() throws Throwable {
        compileAndCheckErrorMessage("test205", null);
    }
	
	/* inner extends outer */
	public void testCaesarTestCase_206() throws Throwable {
        compileAndCheckErrorMessage("test206", null);
    }
	
	/* outer extends inner */
	public void testCaesarTestCase_207() throws Throwable {
        compileAndCheckErrorMessage("test207", null);
    }
	
	/* inner extends external class */
	public void testCaesarTestCase_208() throws Throwable {
        compileAndCheckErrorMessage("test208", null);
    }
	
	/* outer cclass extends external inner cclass */
	public void testCaesarTestCase_209() throws Throwable {
        compileAndCheckErrorMessage("test209", null);
    }
	
	/* inner inheritance cycle through different branches of mixin combination */
	public void testCaesarTestCase_210() throws Throwable {
        compileAndCheckErrorMessage("test210", null);
    }
	
	/* interface in cclass */
	public void testCaesarTestCase_211() throws Throwable {
        compileAndCheckErrorMessage("test211", null);
    }
	
	/* mixing plain classes */
	public void testCaesarTestCase_212() throws Throwable {
        compileAndCheckErrorMessage("test212", null);
    }
	
	/* repeated mixing */
	public void testCaesarTestCase_213() throws Throwable {
        compileAndCheckErrorMessage("test213", null);
    }
	
	/* mixing outer with inner */
	public void testCaesarTestCase_214() throws Throwable {
        compileAndCheckErrorMessage("test214", null);
    }
	
	/* mixing interfaces */
	public void testCaesarTestCase_215() throws Throwable {
        compileAndCheckErrorMessage("test215", null);
    }
	
	/* class extends interface */
	public void testCaesarTestCase_216() throws Throwable {
        compileAndCheckErrorMessage("test216", null);
    }
	
	/* cclass in implements */
	public void testCaesarTestCase_217() throws Throwable {
        compileAndCheckErrorMessage("test217", null);
    }
	
	/* abstract cclass */
	public void testCaesarTestCase_218() throws Throwable {
        compileAndCheckErrorMessage("test218", null);
    }
	
	/* extending overrriden classes */
	public void testCaesarTestCase_219() throws Throwable {
        compileAndCheckErrorMessage("test219", null);
    }
	
	/* changing mixing order */
	public void testCaesarTestCase_220() throws Throwable {
        compileAndCheckErrorMessage("test220", null);
    }
	
	/* new operator with parameter */
	public void testCaesarTestCase_221() throws Throwable {
        compileAndCheckErrorMessage("test221", null);
    }
	
	/* new array on cclass */
	public void testCaesarTestCase_222() throws Throwable {
        compileAndCheckErrorMessage("test222", null);
    }
	
	/* direct construction of inner class */
	public void testCaesarTestCase_223() throws Throwable {
        compileAndCheckErrorMessage("test223", null);
    }
	
	/* constructing external inner class */
	public void testCaesarTestCase_224() throws Throwable {
        compileAndCheckErrorMessage("test224", null);
    }
	
	/* statically qualified new operator */
	public void testCaesarTestCase_225() throws Throwable {
        compileAndCheckErrorMessage("test225", null);
    }
	
	/* qualified new operator inside cclass */
	public void testCaesarTestCase_226() throws Throwable {
        compileAndCheckErrorMessage("test226", null);
    }
	
	/* constructing non-existing inner class */
	public void testCaesarTestCase_227() throws Throwable {
        compileAndCheckErrorMessage("test227", null);
    }
	
	/* construction of virtual class in static context */
	public void testCaesarTestCase_228() throws Throwable {
        compileAndCheckErrorMessage("test228", null);
    }
	
	/* constructor with parameter */
	public void testCaesarTestCase_229() throws Throwable {
        compileAndCheckErrorMessage("test229", null);
    }
	
	/* constructor with wrong name */
	public void testCaesarTestCase_230() throws Throwable {
        compileAndCheckErrorMessage("test230", null);
    }
	
	/* non-public cclass */
	public void testCaesarTestCase_231() throws Throwable {
        compileAndCheckErrorMessage("test231", null);
    }
	
	/* restricting access in overriden method public -> protected */
	public void testCaesarTestCase_232() throws Throwable {
        compileAndCheckErrorMessage("test232", null);
    }
	
	/* inner inheritance leads to visibility restriction public -> protected */
	public void testCaesarTestCase_233() throws Throwable {
        compileAndCheckErrorMessage("test233", null);
    }
	
	/* restricting access in overriden method protected -> private */
	public void testCaesarTestCase_234() throws Throwable {
        compileAndCheckErrorMessage("test234", null);
    }
	
	/* public fields */
	public void testCaesarTestCase_234b() throws Throwable {
        compileAndCheckErrorMessage("test234b", null);
    }
	
	/* package visible fields */
	public void testCaesarTestCase_234c() throws Throwable {
        compileAndCheckErrorMessage("test234c", null);
    }
	
	/* package visible methods */
	public void testCaesarTestCase_234d() throws Throwable {
        compileAndCheckErrorMessage("test234d", null);
    }
	
	/* accessing private method from subclass */
	public void testCaesarTestCase_235() throws Throwable {
        compileAndCheckErrorMessage("test235", null);
    }
	
	/* accessing private method from inner class */
	public void testCaesarTestCase_236() throws Throwable {
        compileAndCheckErrorMessage("test236", null);
    }
	
	/* accessing protected method from inner class */
	public void testCaesarTestCase_237() throws Throwable {
        compileAndCheckErrorMessage("test237", null);
    }
	
	/* accessing protected of the another same class object */
	public void testCaesarTestCase_238() throws Throwable {
        compileAndCheckErrorMessage("test238", null);
    }
	
	/* accessing protected of newly created same class object */
	public void testCaesarTestCase_239() throws Throwable {
        compileAndCheckErrorMessage("test239", null);
    }
	
	/* protected cclass constructor */
	public void testCaesarTestCase_240() throws Throwable {
        compileAndCheckErrorMessage("test240", null);
    }
	
	/* access outer field */
	public void testCaesarTestCase_241() throws Throwable {
        compileAndCheckErrorMessage("test241", null);
    }
	
	/* implicit outer method call */
	public void testCaesarTestCase_242() throws Throwable {
        compileAndCheckErrorMessage("test242", null);
    }
	
	/* implicit outer inner class construction */
	public void testCaesarTestCase_243() throws Throwable {
        compileAndCheckErrorMessage("test243", null);
    }
	
	/* outer this */
	public void testCaesarTestCase_244() throws Throwable {
        compileAndCheckErrorMessage("test244", null);
    }
	
	/* method call from static context */
	public void testCaesarTestCase_245() throws Throwable {
        compileAndCheckErrorMessage("test245", null);
    }
	
	/* static method call from within the class */
	public void testCaesarTestCase_246() throws Throwable {
        compileAndCheckErrorMessage("test246", null);
    }
	
	/* accessing cclass data members */
	public void testCaesarTestCase_247() throws Throwable {
        compileAndCheckErrorMessage("test247", null);
    }
	
	/* accessing outer from outer class */
	public void testCaesarTestCase_248() throws Throwable {
        compileAndCheckErrorMessage("test248", null);
    }
	
	/* changing outer */
	public void testCaesarTestCase_249() throws Throwable {
        compileAndCheckErrorMessage("test249", null);
    }
	
	/* changing wrappee */
	public void testCaesarTestCase_249b() throws Throwable {
        compileAndCheckErrorMessage("test249b", null);
    }
	
	/* private access in overriden class */
	public void testCaesarTestCase_250() throws Throwable {
        compileAndCheckErrorMessage("test250", null);
    }
	
	/* duplicate inner class */
	public void testCaesarTestCase_251() throws Throwable {
        compileAndCheckErrorMessage("test251", null);
    }
	
	/* duplicate constructor */
	public void testCaesarTestCase_252() throws Throwable {
        compileAndCheckErrorMessage("test252", null);
    }
	
	/* duplicate method */
	public void testCaesarTestCase_253() throws Throwable {
        compileAndCheckErrorMessage("test253", null);
    }
	
	/* overriding inner with incompatible signature */
	public void testCaesarTestCase_254() throws Throwable {
        compileAndCheckErrorMessage("test254", null);
    }
	
	/* overriding method in subclass with incompatible signature */
	public void testCaesarTestCase_255() throws Throwable {
        compileAndCheckErrorMessage("test255", null);
    }
	
	/* overriding method with exception specification */
	public void testCaesarTestCase_256() throws Throwable {
        compileAndCheckErrorMessage("test256", null);
    }
	
	/* mixing incompatible methods */
	public void testCaesarTestCase_257() throws Throwable {
        compileAndCheckErrorMessage("test257", null);
    }
	
	/* mixing inner classes with incompatible signatures */
	public void testCaesarTestCase_258() throws Throwable {
        compileAndCheckErrorMessage("test258", null);
    }
	
	/* non-existing super call */
	public void testCaesarTestCase_259() throws Throwable {
        compileAndCheckErrorMessage("test259", null);
    }
	
	/* cclass and class with the same name */
	public void testCaesarTestCase_260() throws Throwable {
        compileAndCheckErrorMessage("test260", null);
    }
	
	/* assigning to more specific virtual class */
	public void testCaesarTestCase_261() throws Throwable {
        compileAndCheckErrorMessage("test261", null);
    }
	
	/* assigning to more specific virtual class inside context of more specific class */
	public void testCaesarTestCase_262() throws Throwable {
        compileAndCheckErrorMessage("test262", null);
    }
	
	/* assigning to more specific virtual class inside context of more general class */
	public void testCaesarTestCase_263() throws Throwable {
        compileAndCheckErrorMessage("test263", null);
    }
	
	/* outer class objects not covariant */
	public void testCaesarTestCase_264() throws Throwable {
        compileAndCheckErrorMessage("test264", null);
    }
	
	/* wraps in outer class */
	public void testCaesarTestCase_266() throws Throwable {
        compileAndCheckErrorMessage("test266", null);
    }
	
	/* wraps in simple class */
	public void testCaesarTestCase_267() throws Throwable {
        compileAndCheckErrorMessage("test267", null);
    }
	
	/* overriding wraps in overriden class */
	public void testCaesarTestCase_268() throws Throwable {
        compileAndCheckErrorMessage("test268", null);
    }
	
	/* overriding wraps in subclass */
	public void testCaesarTestCase_269() throws Throwable {
        compileAndCheckErrorMessage("test269", null);
    }
	
	/* applying wrapper function on wrong type */
	public void testCaesarTestCase_270() throws Throwable {
        compileAndCheckErrorMessage("test270", null);
    }
}
