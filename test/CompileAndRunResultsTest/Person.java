package generated;
public class Person {
	String[] items;
	public Person( String[] items ) {
		this.items = items;
	}
	public boolean hasItem( Object item ) {
		for( int i = 0; i < items.length; i++ ) {
			if( items[ i ].equals( item ) )
				return true;
		}
		return false;
	}
}