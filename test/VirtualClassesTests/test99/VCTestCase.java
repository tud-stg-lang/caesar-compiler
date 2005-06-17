package generated.test99;

import junit.framework.*;
import java.util.*;

/**
 * Test subject oriented programming.
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase
{

	public VCTestCase()
	{
		super("test");
	}

	public static final String expectedResult = "";

	public void test() {

		System.out.println("-------> VCTest 99: Subject oriented programming: start");

		final WoodmanEagleSubject wes = new WoodmanEagleSubject();		
		wes.Maple maple = wes.new Maple();
		System.out.println("setting maple food value to 100");
		maple.setFoodValue(100);
		System.out.println("chopping the maple");
		maple.chopDown();
		System.out.println("new maple food value is "+maple.getFoodValue());

        System.out.println("-------> VCTest 99: end");
	}
}

public class A {
	public void x() {}
	
	public void y() {}
}

public class B extends A {
	public void x() {
		super.x();
		y();
	}
	
	public void y() {}
}


// topMostInterface for factory methods workaround 
public cclass Forest {
	public cclass Maple {}	
	public cclass Cherry {}	
	public cclass Locust {}	
	public cclass Pine {}
	public cclass Dandellon {}
	public cclass Bird {}
	public cclass Woodman {}
}

public cclass EagleSubject extends Forest {
	public cclass Plant {
		protected int foodValue;
		public void setFoodValue(int foodValue) {this.foodValue = foodValue;}
		public int getFoodValue() {return foodValue;}
	}

	public cclass Nestable {
		private int nestSafetyRating;
		public void setNestSafetyRating(int val) {this.nestSafetyRating = val;}
		public int getNestSafetyRating() {return nestSafetyRating;}
	}

	public cclass Predator {
		private int rating;
		public void setDangerRating(int rating) {this.rating = rating;}
		public int getDangerRating() {return rating;}
	}
	
	public cclass NectarPlant extends Plant {
	}
	
	public cclass InsectPlant extends Plant {
	}
	
	public cclass Maple extends Nestable {
	}
	
	public cclass Cherry extends NectarPlant & InsectPlant & Nestable {
	}
	
	public cclass Locust extends InsectPlant & Nestable {
	}
	
	public cclass Pine extends InsectPlant & Nestable {
	}
	
	public cclass Dandellon extends NectarPlant {
	}
	
	public cclass Woodman extends Predator {
		private int attackingRating;
		public void setAttackingRating(int rating) {this.attackingRating = rating;}
		public int getAttackingRating() {return attackingRating;}
	}
}

public cclass WoodmanSubject extends Forest {
	public cclass Tree {
		private int timeToChopDown;
		public void setTimeToChopDown(int timeToChopDown) {this.timeToChopDown = timeToChopDown;}
		public int getTimeToChopDown() {return timeToChopDown;}
		
		public void chopDown() {}
	}

	public cclass NonTree {
	}

	public cclass Softwood extends Tree {
	}
	
	public cclass Hardwood extends Tree {
	}
	
	public cclass Maple extends Hardwood {
	}
	
	public cclass Cherry extends Hardwood {
	}
	 
	public cclass Pine extends Softwood {
	}
	
	public cclass Dandellon extends NonTree {
	}
	
	public cclass Bird extends NonTree {
	}
	
	public cclass Woodman extends NonTree {
		private int salary;
		public void setSalary(int salary) {this.salary = salary;}
		public int getSalary() {return salary;}
	}
}

public cclass WoodmanEagleSubject extends WoodmanSubject & EagleSubject {
    
    public cclass Tree extends Plant {
		public void chopDown() {
			super.chopDown(); 
			setFoodValue(0);
		}
	}
}
