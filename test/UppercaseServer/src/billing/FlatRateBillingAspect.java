package billing;

/**
 * The session-price is alway constant.
 */
public crosscutting class FlatRateBillingAspect extends BillingAspect {

	public double getTotalPrice() {
		return 10;
	}

}