/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: Caesar.flex,v 1.8 2004-10-28 13:06:26 aracic Exp $
 */

package org.caesarj.compiler;


import org.caesarj.util.CWarning;
import org.caesarj.util.*;
import org.caesarj.compiler.ast.*;
import org.caesarj.tools.antlr.extra.*;




%%

%public
%class CaesarScanner
%extends org.caesarj.tools.antlr.extra.Scanner
%implements CaesarTokenTypes

%function nextTokenImpl
%type org.caesarj.tools.antlr.runtime.Token

%unicode
%pack

%{
  public CaesarScanner(CompilerBase compiler, InputBuffer buffer) {
    super(compiler, buffer);
    this.buffer = buffer;
  }

  /**
   * Creates a character literal token.
   */
  private CToken buildCharacterLiteral(char image) {
    return new CToken(CHARACTER_LITERAL, String.valueOf(image));
  }


  private final StringBuffer	string = new StringBuffer();
  
  private final StringBuffer	pattern = new StringBuffer();
  
  private boolean typePatternExpected;
  
  private int oldState;
%}

%init{
  // dummy: we provide our own constructor
  super(null, null);
%init}

%eofval{
  return TOKEN_EOF;
%eofval}

/*
 * macros
 */
// white space
W =	[ \t\f]

// line terminator
T =	\r|\n|\r\n

// decimal digit
D =	[0-9]

// hexadecimal digit
H =	[0-9a-fA-F]

// octal digit
O =	[0-7]

// exponent part
E =	[eE] [+\-]? {D}+


%state TYPEPATTERN, STRINGLITERAL, CHARLITERAL, TRADITIONALCOMMENT, ENDOFLINECOMMENT

%%

<YYINITIAL> {
   "pointcut" {typePatternExpected = true; return TOKEN_LITERAL_pointcut;}
   "before" {typePatternExpected = true; return TOKEN_LITERAL_before;}
   "after" {typePatternExpected = true; return TOKEN_LITERAL_after;}
   "around" {typePatternExpected = true; return TOKEN_LITERAL_around;} 
   
   "declare" {yybegin(TYPEPATTERN); pattern.setLength(0); return TOKEN_LITERAL_declare;}

   
  /* type pattern */
  \:				{ if(typePatternExpected) {
  						yybegin(TYPEPATTERN);
  						pattern.setLength(0);
  					  } else {
  					  	return TOKEN_COLON;
  					  }
  					}
  					
  \;				{typePatternExpected = false; return TOKEN_SEMI;}  	
				  
"abstract"		{ return TOKEN_LITERAL_abstract; }
"boolean"		{ return TOKEN_LITERAL_boolean; }
"break"		{ return TOKEN_LITERAL_break; }
"byte"		{ return TOKEN_LITERAL_byte; }
"case"		{ return TOKEN_LITERAL_case; }
"catch"		{ return TOKEN_LITERAL_catch; }
"char"		{ return TOKEN_LITERAL_char; }
"class"		{ return TOKEN_LITERAL_class; }
"const"		{ return TOKEN_LITERAL_const; }
"continue"		{ return TOKEN_LITERAL_continue; }
"default"		{ return TOKEN_LITERAL_default; }
"do"		{ return TOKEN_LITERAL_do; }
"double"		{ return TOKEN_LITERAL_double; }
"else"		{ return TOKEN_LITERAL_else; }
"extends"		{ return TOKEN_LITERAL_extends; }
"false"		{ return TOKEN_LITERAL_false; }
"final"		{ return TOKEN_LITERAL_final; }
"finally"		{ return TOKEN_LITERAL_finally; }
"float"		{ return TOKEN_LITERAL_float; }
"for"		{ return TOKEN_LITERAL_for; }
"goto"		{ return TOKEN_LITERAL_goto; }
"if"		{ return TOKEN_LITERAL_if; }
"implements"		{ return TOKEN_LITERAL_implements; }
"import"		{ return TOKEN_LITERAL_import; }
"instanceof"		{ return TOKEN_LITERAL_instanceof; }
"int"		{ return TOKEN_LITERAL_int; }
"interface"		{ return TOKEN_LITERAL_interface; }
"long"		{ return TOKEN_LITERAL_long; }
"native"		{ return TOKEN_LITERAL_native; }
"new"		{ return TOKEN_LITERAL_new; }
"null"		{ return TOKEN_LITERAL_null; }
"package"		{ return TOKEN_LITERAL_package; }
"private"		{ return TOKEN_LITERAL_private; }
"protected"		{ return TOKEN_LITERAL_protected; }
"public"		{ return TOKEN_LITERAL_public; }
"return"		{ return TOKEN_LITERAL_return; }
"short"		{ return TOKEN_LITERAL_short; }
"static"		{ return TOKEN_LITERAL_static; }
"strictfp"		{ return TOKEN_LITERAL_strictfp; }
"super"		{ return TOKEN_LITERAL_super; }
"switch"		{ return TOKEN_LITERAL_switch; }
"synchronized"		{ return TOKEN_LITERAL_synchronized; }
"this"		{ return TOKEN_LITERAL_this; }
"throw"		{ return TOKEN_LITERAL_throw; }
"throws"		{ return TOKEN_LITERAL_throws; }
"transient"		{ return TOKEN_LITERAL_transient; }
"true"		{ return TOKEN_LITERAL_true; }
"try"		{ return TOKEN_LITERAL_try; }
"void"		{ return TOKEN_LITERAL_void; }
"volatile"		{ return TOKEN_LITERAL_volatile; }
"while"		{ return TOKEN_LITERAL_while; }
"cclass"		{ return TOKEN_LITERAL_cclass; }
"wraps"		{ return TOKEN_LITERAL_wraps; }
"wrappee"		{ return TOKEN_LITERAL_wrappee; }
"after"		{ return TOKEN_LITERAL_after; }
"around"		{ return TOKEN_LITERAL_around; }
"before"		{ return TOKEN_LITERAL_before; }
"declare"		{ return TOKEN_LITERAL_declare; }
"deploy"		{ return TOKEN_LITERAL_deploy; }
"deployed"		{ return TOKEN_LITERAL_deployed; }
"pointcut"		{ return TOKEN_LITERAL_pointcut; }
"precedence"		{ return TOKEN_LITERAL_precedence; }
"privileged"		{ return TOKEN_LITERAL_privileged; }
"returning"		{ return TOKEN_LITERAL_returning; }
"throwing"		{ return TOKEN_LITERAL_throwing; }
"="		{ return TOKEN_ASSIGN; }
"&"		{ return TOKEN_BAND; }
"&="		{ return TOKEN_BAND_ASSIGN; }
"~"		{ return TOKEN_BNOT; }
"|"		{ return TOKEN_BOR; }
"|="		{ return TOKEN_BOR_ASSIGN; }
">>>"		{ return TOKEN_BSR; }
">>>="		{ return TOKEN_BSR_ASSIGN; }
"\^"		{ return TOKEN_BXOR; }
"\^="		{ return TOKEN_BXOR_ASSIGN; }
":"		{ return TOKEN_COLON; }
","		{ return TOKEN_COMMA; }
"--"		{ return TOKEN_DEC; }
"."		{ return TOKEN_DOT; }
"=="		{ return TOKEN_EQUAL; }
">="		{ return TOKEN_GE; }
">"		{ return TOKEN_GT; }
"++"		{ return TOKEN_INC; }
"&&"		{ return TOKEN_LAND; }
"["		{ return TOKEN_LBRACK; }
"{"		{ return TOKEN_LCURLY; }
"<="		{ return TOKEN_LE; }
"!"		{ return TOKEN_LNOT; }
"||"		{ return TOKEN_LOR; }
"("		{ return TOKEN_LPAREN; }
"<"		{ return TOKEN_LT; }
"-"		{ return TOKEN_MINUS; }
"-="		{ return TOKEN_MINUS_ASSIGN; }
"!="		{ return TOKEN_NOT_EQUAL; }
"%"		{ return TOKEN_PERCENT; }
"%="		{ return TOKEN_PERCENT_ASSIGN; }
"+"		{ return TOKEN_PLUS; }
"+="		{ return TOKEN_PLUS_ASSIGN; }
"?"		{ return TOKEN_QUESTION; }
"]"		{ return TOKEN_RBRACK; }
"}"		{ return TOKEN_RCURLY; }
")"		{ return TOKEN_RPAREN; }
";"		{ return TOKEN_SEMI; }
"<<"		{ return TOKEN_SL; }
"/"		{ return TOKEN_SLASH; }
"/="		{ return TOKEN_SLASH_ASSIGN; }
"<<="		{ return TOKEN_SL_ASSIGN; }
">>"		{ return TOKEN_SR; }
">>="		{ return TOKEN_SR_ASSIGN; }
"*"		{ return TOKEN_STAR; }
"*="		{ return TOKEN_STAR_ASSIGN; }

  /* string literal */
  \"    			{ yybegin(STRINGLITERAL); string.setLength(0); }

  /* character literal */
  \'    			{ yybegin(CHARLITERAL); }

  /* numeric literals */
  (0 | [1-9]{D}*) [lL]?		{ return new CToken(INTEGER_LITERAL, yytext()); }
  (0 [xX] {H}+) [lL]?		{ return new CToken(INTEGER_LITERAL, yytext()); }
  (0 {O}+) [lL]?		{ return new CToken(INTEGER_LITERAL, yytext()); }

  {D}+ \. {D}* {E}? [fFdD]?	{ return new CToken(REAL_LITERAL, yytext()); }
  \. {D}+ {E}? [fFdD]?		{ return new CToken(REAL_LITERAL, yytext()); }
  {D}+ {E} [fFdD]?		{ return new CToken(REAL_LITERAL, yytext()); }
  {D}+ [fFdD]			{ return new CToken(REAL_LITERAL, yytext()); }

  /* comments */
  "/*"				{ yybegin(TRADITIONALCOMMENT); string.setLength(0); }
  "//"				{ yybegin(ENDOFLINECOMMENT); string.setLength(0); }

  /* whitespace */
  {T}				{ incrementLine(); }
  {W}				{ /* ignore */ }

  /* identifiers */
  [:jletter:][:jletterdigit:]* { return new CToken(IDENT, yytext().intern()); }
  
}


<TYPEPATTERN> {

  [^\{\;\/]	{ pattern.append(yytext()); }
  
  [^\{\;\/] /(\;|\{)	{ typePatternExpected = false;
  					  oldState= YYINITIAL; 
  					  yybegin(YYINITIAL);
  					  pattern.append(yytext()); 
  					  return new CToken(TYPE_PATTERN, pattern.toString()); }
  					  
  {T}	{ incrementLine();}
  
  /* comments */
  "/*"				{ oldState = yystate(); yybegin(TRADITIONALCOMMENT); string.setLength(0); }
  "//"				{ oldState = yystate(); yybegin(ENDOFLINECOMMENT); string.setLength(0); }  
}

<STRINGLITERAL> {
  \"    			{ yybegin(YYINITIAL); return new CToken(STRING_LITERAL, string.toString()); }

  [^\r\n\"\\]+			{ string.append(yytext()); }

  /* escape sequences */
  "\\b" 			{ string.append('\b'); }
  "\\t" 			{ string.append('\t'); }
  "\\n" 			{ string.append('\n'); }
  "\\f" 			{ string.append('\f'); }
  "\\r" 			{ string.append('\r'); }
  "\\\""			{ string.append('\"'); }
  "\\'" 			{ string.append('\''); }
  "\\\\"			{ string.append('\\'); }
  \\[0-3]?{O}?{O}		{
				  int		val;

				  val = Integer.parseInt(yytext().substring(1), 8);
				  string.append((char)val);
				}

  /* error cases */
  \\.   			{ reportTrouble(Messages.BAD_ESCAPE_SEQUENCE, new Object[]{ yytext() }); }
  {T}				{ reportTrouble(Messages.BAD_END_OF_LINE, new Object[]{ "string literal" }); }
}

<CHARLITERAL> {
  [^\r\n\'\\]\'			{ yybegin(YYINITIAL); return buildCharacterLiteral(yytext().charAt(0)); }

  /* escape sequences */
  "\\b"\'       		{ yybegin(YYINITIAL); return buildCharacterLiteral('\b'); }
  "\\t"\'       		{ yybegin(YYINITIAL); return buildCharacterLiteral('\t'); }
  "\\n"\'       		{ yybegin(YYINITIAL); return buildCharacterLiteral('\n'); }
  "\\f"\'       		{ yybegin(YYINITIAL); return buildCharacterLiteral('\f'); }
  "\\r"\'       		{ yybegin(YYINITIAL); return buildCharacterLiteral('\r'); }
  "\\\""\'      		{ yybegin(YYINITIAL); return buildCharacterLiteral('\"'); }
  "\\'"\'       		{ yybegin(YYINITIAL); return buildCharacterLiteral('\''); }
  "\\\\"\'      		{ yybegin(YYINITIAL); return buildCharacterLiteral('\\'); }
  \\[0-3]?{O}?{O}\'		{
				  yybegin(YYINITIAL);

				  int		val;

				  val = Integer.parseInt(yytext().substring(1, yylength()-1), 8);
				  return buildCharacterLiteral((char)val);
				}

  /* error cases */
  \\.   			{ reportTrouble(Messages.BAD_ESCAPE_SEQUENCE, new Object[]{ yytext() }); }
  {T}				{ reportTrouble(Messages.BAD_END_OF_LINE, new Object[]{ "character literal" }); }
}

<TRADITIONALCOMMENT> {

  // !!! handle /* in traditional comment

  \*+ "/"			{
				  yybegin(oldState);

				  if (string.length() > 0 && string.charAt(0) == '*') {
				    //!!! graf 001222: first '*' should be removed
				    addComment(new JavadocComment(string.toString(), false, false));
				  } else {
				    addComment(new JavaStyleComment(string.toString(), false, false, false));
				  }
				}

  [^\r\n*]+			{ string.append(yytext()); }
  \*+ [^*/\r\n]			{ string.append(yytext()); }
  \*+ {T}			{
				  incrementLine();
				  string.append(yytext());
				}
  {T}				{
				  incrementLine();
				  string.append(yytext());
				}
  <<EOF>>			{
				  reportTrouble(Messages.EOF_IN_TRADITIONAL_COMMENT, null);
				  return TOKEN_EOF;
				}
}

<ENDOFLINECOMMENT> {

  [^\r\n]+			{ string.append(yytext()); }
  {T}				{
				  yybegin(oldState);
				  incrementLine();
				  addComment(new JavaStyleComment(string.toString(), true, false, false));
				}
  <<EOF>>			{
				  reportTrouble(new CWarning(getTokenReference(),
							     Messages.EOF_IN_ENDOFLINE_COMMENT));
				  return TOKEN_EOF;
				}
}

/* error fallback */
.|\n				{ reportTrouble(Messages.ILLEGAL_CHAR, new Object[]{ yytext() }); }
