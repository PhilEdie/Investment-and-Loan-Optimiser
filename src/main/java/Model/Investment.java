package Model;

/**
 * Model.Investment objects are accounts where the balance is always positive.
 * @author Phil Edie
 *
 */
public class Investment extends Account {


	public Investment(String accountName, double interestRate, double balance) {
		super(accountName, interestRate, balance);
		assert balance >= 0;
	}
	
	public Investment(Investment toCopy) {
		super(toCopy.getAccountName(), toCopy.getInterestRate(), toCopy.getBalance());
	}

	@Override
	public String toString() {
		return getAccountName() + "[interestRate=" + getInterestRate() + ", balance=$" + String.format("%.2f", getBalance()) + "]";
	}
	
	@Override
	public void setBalance(double balance) {
		assert balance >= 0;
		super.setBalance(balance);
	}
	
	@Override
	public double makePayment(double payment) {
		setPaymentForPeriod(payment);
		setBalance(getBalance() + payment);
		return 0.0;
	}
	
}