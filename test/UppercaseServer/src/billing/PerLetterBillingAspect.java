package billing;

import uppercaseserver.*;

/**
 * The session-price depends on the number of converted letters.
 */
public crosscutting class PerLetterBillingAspect extends BillingAspect {
	
	protected int counter;

	pointcut toUpperCase(String s) : call(* String.toUpperCase(..))
		&& this(UppercaseWorker)
		&& target(s);

	before(String s) : toUpperCase(s) {
		counter += s.length();
	}

	public double getTotalPrice() {
		return counter * 0.01;
	}

}