package billing;

import uppercaseserver.*;

/**
 * The session-price depends on the number of client-requests.
 * That includes the exit-request.
 */
public crosscutting class PerRequestBillingAspect extends BillingAspect {

	protected int counter;

	pointcut handleRequest() : call(* handleRequest(..))
		&& this(UppercaseWorker);

	before() : handleRequest() {
		counter++;
	}

	public double getTotalPrice() {
		return counter * 0.1;
	}

}