// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   KlausTest.java

package generated;

import org.caesarj.runtime.Child;
import org.caesarj.runtime.ChildImpl;

public interface Fooo
    extends Child
{
    public static interface Goo
        extends Child
    {

        public abstract void goo();

        public abstract void _goo_selfContext(Object obj);
    }

    public static class Goo_Proxy extends ChildImpl
        implements Goo
    {

        public void goo()
        {
            _goo_selfContext(this);
        }

        public void _goo_selfContext(Object obj)
        {
            ((Goo)super._parent._getTarget())._goo_selfContext(_getDispatcher(obj));
        }

        protected Goo_Proxy(Child child)
        {
            super(child);
        }
    }


    public abstract void foo();

    public abstract void _foo_selfContext(Object obj);

    public abstract Object _createGoo();

    public abstract Object __createGoo_selfContext(Object obj);
}
