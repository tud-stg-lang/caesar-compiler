package generated;

import org.caesarj.runtime.Child;

public class Entities {
	
	public Door createDoor() {
		Child child = new Door_Impl();
		child._setFamily( this );
		return (Door) child;
	}
	
	public virtual class Door {
		public AxeDoor getAxeDoor() { return null; }
		public DynamiteDoor getDynamiteDoor() { return null; }
		public Door() {}
		public boolean canOpen( Person p ) {
			return true;
		}
		public void open( Person p ) {			
			if( !canOpen( p ) )
				throw new RuntimeException( "No!");
		}
	}
	
	public SecuredDoor createSecuredDoor( Door parent ) {
		Child child = new SecuredDoor_Impl( parent );
		child._setFamily( this );
		return (SecuredDoor) child;
	}
	
	public virtual class SecuredDoor extends Door {
		public Object neededItem() { return null; }
		public boolean canOpen( Person p ) {
			return p.hasItem( neededItem() )
				&& super.canOpen( p );
		}
		public DynamiteDoor getDynamiteDoor() {
			Child child = new DynamiteDoor_Impl( this );
			child._setFamily( getEntitiesThis() );
			return (DynamiteDoor) child;
		}
	}

	public SecuredDoor2 createSecuredDoor2( SecuredDoor parent ) {
		Child child = new SecuredDoor2_Impl( parent );
		child._setFamily( this );
		return (SecuredDoor2) child;
	}

	public virtual class SecuredDoor2 extends SecuredDoor {
		public AxeDoor getAxeDoor() {
			Child child = new AxeDoor_Impl( this );
			child._setFamily( getEntitiesThis() );
			return (AxeDoor) child;
		}
	}

	public virtual class DynamiteDoor extends SecuredDoor {
		public Object neededItem() {
			return "dynamite";
		}
	}
	
	public virtual class AxeDoor extends SecuredDoor {
		public Object neededItem() {
			return "axe";
		}
	}

	public MagicDoor createMagicDoor( SecuredDoor parent ) {
		Child child = new MagicDoor_Impl( parent );
		child._setFamily( this );
		return (MagicDoor) child;
	}

	public virtual class MagicDoor extends SecuredDoor {
		public Object neededItem() {
			return "spell";
		}
	}

	public LockedDoor createLockedDoor( SecuredDoor parent ) {
		Child child = new LockedDoor_Impl( parent );
		child._setFamily( this );
		return (LockedDoor) child;
	}

	public virtual class LockedDoor extends SecuredDoor {		
		public Object neededItem() {
			return "key";
		}
	}

	private Object getEntitiesThis() {
		return this;
	}
}