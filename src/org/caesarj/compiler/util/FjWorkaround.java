package org.caesarj.compiler.util;

import java.lang.reflect.Field;

import org.caesarj.kjc.JLocalVariable;
import org.caesarj.kjc.JLocalVariableExpression;

public class FjWorkaround {
	public static JLocalVariable getVariable( JLocalVariableExpression e ) {
		return (JLocalVariable) getPrivateField( e, "variable" );
	}
	private static Object getPrivateField( Object receiver, String fieldName ) {
		try {
			Field field = receiver.getClass().getField( fieldName );
			field.setAccessible( true );
			return field.get( receiver );
		} catch( Throwable t ) {
			return null;
		}
	}
}
