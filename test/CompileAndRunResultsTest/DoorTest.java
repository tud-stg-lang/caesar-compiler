package generated;
import java.lang.reflect.Method;

import org.caesarj.runtime.ClassBasedDelegation;
import org.caesarj.runtime.GeneratedDispatching;

import junit.framework.TestCase;

public class DoorTest extends TestCase {
	
	public DoorTest() {
		super( "test" );
		e = new Entities();
	}
	
	final Entities e;
	e.AxeDoor i1;
	e.MagicDoor i2;
	e.SecuredDoor i3;
	e.LockedDoor i4;
	e.SecuredDoor i5;
	e.SecuredDoor i6;
	e.Door i7;
	
	e.MagicDoor magicDoor;
	e.LockedDoor lockedDoor;
	e.SecuredDoor realSecuredDoor;
	e.SecuredDoor axeAndSpellDoor;
	e.SecuredDoor axeAndDynamiteDoor;
			
	public void setUp() {
		magicDoor = e.createMagicDoor( e.createSecuredDoor( e.createDoor() ) );
		lockedDoor = e.createLockedDoor( e.createSecuredDoor( e.createDoor() ) );
		realSecuredDoor =
			(i2 = e.createMagicDoor(
			(i3 = e.createSecuredDoor(
			(i4 = e.createLockedDoor(
			(i5 = e.createSecuredDoor2(
			(i6 = e.createSecuredDoor(
			(i7 = e.createDoor()) )) )) )) )) ));
	}
	
	public void test() {
		
		Entities e = new Entities();
		Entities.Door d = e.new Door();
		
		doTestClassBasedDelegation();
		doTestGeneratedDispatching();
		tryToOpenTheDoors();
	}
	
	public void doTestClassBasedDelegation() {
		ClassBasedDelegation delegation = new ClassBasedDelegation();
		assertSame( i7, delegation.M( i2, i7, seekMethod( i7.getClass(), "open" ) ) );
		assertSame( i3, delegation.M( i2, i7, seekMethod( i7.getClass(), "canOpen" ) ) );
		assertSame( i2, delegation.M( i2, i3, seekMethod( i6.getClass(), "neededItem" ) ) );
		assertSame( i6, delegation.M( i4, i6, seekMethod( i7.getClass(), "canOpen" ) ) );
		assertSame( i4, delegation.M( i2, i6, seekMethod( i6.getClass(), "neededItem" ) ) );
	}

	public void doTestGeneratedDispatching() {
		GeneratedDispatching.getInstance().get( i6, i7 );
	}

	protected Method seekMethod( Class clazz, String methodName ) {
		Method methods[] = clazz.getMethods();
		for( int i = 0; i < methods.length; i++ ) {
			if( methods[ i ].getName().equals( methodName ) )
				return methods[ i ];
		}
		throw new RuntimeException( "method " + methodName + " not found in class " + clazz.getName() );
	}
		
	public void tryToOpenTheDoors() {
		Person hasSpell = new Person( new String[]{ "spell" } );
		Person hasKey = new Person( new String[]{ "key" } );
		Person hasAxe = new Person( new String[]{ "axe" } );
		Person hasDynamite = new Person( new String[]{ "dynamite" } );
		Person hasKeyAndSpell = new Person( new String[]{ "spell", "key" } );
		Person hasAxeAndSpell = new Person( new String[]{ "spell", "axe" } );
		Person hasAxeAndDynamite = new Person( new String[]{ "dynamite", "axe" } );

		try {
			magicDoor.open( hasSpell );
			lockedDoor.open( hasKey );
			magicDoor.open( hasKeyAndSpell );
			lockedDoor.open( hasKeyAndSpell );
		} catch( Exception exc ) {
			exc.printStackTrace();
			fail( "seems theese tests were too easy!" );
		}
		try {
			realSecuredDoor.open( hasSpell );
			fail( "we need spell and key here!" );
		} catch( Exception exc ) {}
		try {
			realSecuredDoor.open( hasKey );
			fail( "we need spell and key here too!" );
		} catch( Exception exc ) {}
		try {
			realSecuredDoor.open( hasKeyAndSpell );
		} catch( Exception exc ) {
			fail( "this should do!" );
		}
		axeAndSpellDoor = realSecuredDoor.getAxeDoor();
		try {
			axeAndSpellDoor.open( hasSpell );
			fail( "we need spell and axe here!" );
		} catch( Exception exc ) {}
		try {
			axeAndSpellDoor.open( hasKey );
			fail( "we need spell and axe here!" );
		} catch( Exception exc ) {}
		try {
			axeAndSpellDoor.open( hasAxeAndSpell );
		} catch( Exception exc ) {
			exc.printStackTrace();
			fail( "this should do, too!" );
		}
		axeAndDynamiteDoor = axeAndSpellDoor.getDynamiteDoor();
		try {
			axeAndDynamiteDoor.open( hasSpell );
			fail( "we need dynamite and axe here!" );
		} catch( Exception exc ) {}
		try {
			axeAndDynamiteDoor.open( hasKey );
			fail( "we need dynamite and axe here!" );
		} catch( Exception exc ) {}
		try {
			axeAndDynamiteDoor.open( hasAxe );
			fail( "we need dynamite and axe here!" );
		} catch( Exception exc ) {}
		try {
			axeAndDynamiteDoor.open( hasDynamite );
			fail( "we need dynamite and axe here!" );
		} catch( Exception exc ) {}
		try {
			axeAndDynamiteDoor.open( hasAxeAndSpell );
			fail( "we need dynamite and axe here!" );
		} catch( Exception exc ) {}
		try {
			axeAndDynamiteDoor.open( hasKeyAndSpell );
			fail( "we need dynamite and axe here!" );
		} catch( Exception exc ) {}
		try {
			axeAndDynamiteDoor.open( hasAxeAndDynamite );
		} catch( Exception exc ) {
			exc.printStackTrace();
			fail( "this should dooooo!" );
		}
	}
}