package generated;
/**
 * Created by IntelliJ IDEA.
 * User: dsogos
 * Date: Feb 10, 2003
 * Time: 8:42:17 PM
 * To change this template use Options | File Templates.
 */
public class TestingVerySimple {
     public static void main(String[] args){
        final VerySimpleTest f = new VerySimpleTest ();
        //next sentence work!
        f.new R();
        //type declaration doesn’t compile
        f.R r;
        r = f.new R();

    }
}
