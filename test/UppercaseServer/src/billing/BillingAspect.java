package billing;

import uppercaseserver.*;

/**
 * The super-class for all billing modes.
 */
public crosscutting abstract class BillingAspect {

	public abstract double getTotalPrice();

}