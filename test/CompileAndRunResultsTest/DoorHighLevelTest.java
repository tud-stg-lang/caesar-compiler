package generated;

public class DoorHighLevelTest extends junit.framework.TestCase {
	public DoorHighLevelTest() {
		super( "test" );
	}
	
	public void test() throws Throwable {
		Person p1 = new Person( new String[]{ "spell" } );
		Person p2 = new Person( new String[]{ "key" } );
		new MagicDoor().open( p1 );
		try {
			new MagicDoor().open( p2 );
			fail( "exception expected!" );
		} catch( RuntimeException e ) {}
	}
}

clean class Door {
	public Door() {}
	public boolean canOpen( Person p ) {
		return true;
	}
	public void open( Person p ) {			
		if( !canOpen( p ) )
			throw new RuntimeException( "No!");
	}
}
	
clean class SecuredDoor extends Door {
	public SecuredDoor( String item ) {
		this.item = item;
	}
	public SecuredDoor() {
		this( (String) null );
	}
	private Object item;
	public Object neededItem() { return item; }
	public boolean canOpen( Person p ) {
		return p.hasItem( neededItem() )
			&& super.canOpen( p );
	}	
}
	
clean class MagicDoor extends SecuredDoor {
	public MagicDoor() {
		super( "spell" );
	}
}