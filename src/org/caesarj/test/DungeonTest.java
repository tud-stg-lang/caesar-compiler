package org.caesarj.test;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.caesarj.compiler.Log;
import org.caesarj.compiler.Main;

public class DungeonTest extends TestCase {	
	
	public void testDungeon() throws Throwable {			
		
		Log.setVerbose(true);
		FileOutputStream fos = new FileOutputStream("caesar-logger.log");
		
//		Main main = new Main(null, new PrintWriter(System.out));
		Main main = new Main(null, new PrintWriter(fos));
		
		String args[] = {
			"-d", "dungeon/bin", 
			"-v", "-classpath",
			"dist/caesar-runtime.jar;../CesarJ_Dungeon/lib/j3dutils.jar;../CesarJ_Dungeon/lib/log4j-1.2/log4j-1.2.11.jar", 			
			"@dungeon/filelist.txt"
		};
		
		main.run(args);
		
		fos.close();
	}
}
