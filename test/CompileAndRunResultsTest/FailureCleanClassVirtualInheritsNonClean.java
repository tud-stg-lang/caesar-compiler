package generated;

public class FailureCleanClassVirtualInheritsNonClean {
	virtual class Virtual extends FailureCleanClassVirtualInheritsNonClean {}
	
	public static void main( String[] args ) {
		System.out.println( this );
	}
}