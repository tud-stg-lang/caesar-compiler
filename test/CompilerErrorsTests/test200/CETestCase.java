package generated.test200;

/**
 * Purpose: cyclic dependencies
 *
 * @author Ivica Aracic
 */
public class X {
    public X(String p1, int p2) {}
}

public cclass A extends B {}

public cclass B extends C & D {}

public cclass C extends A {}

public cclass D extends A {}