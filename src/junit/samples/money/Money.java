package junit.samples.money;

/**
 * A simple Money.
 *
 */
public class Money implements IMoney {

	private int fAmount;
	private String fCurrency;

	/**
	 * Constructs a money from the given amount and currency.
	 */
	public Money(int amount, String currency) {
		fAmount= amount;
		fCurrency= currency;
	}
	/**
	 * Adds a money to this money. Forwards the request to the addMoney helper.
	 */
	public IMoney add(IMoney m) {
		return m.addMoney(this);
	}
	public IMoney addMoney(Money m) {
		if (m.currency().equals(currency()) )
			return new Money(amount()+m.amount(), currency());
		return MoneyBag.create(this, m);
	}
	public IMoney addMoneyBag(MoneyBag s) {
		return s.addMoney(this);
	}
	public int amount() {
		return fAmount;
	}
	public String currency() {
		return fCurrency;
	}
	@Override
	public boolean equals(Object anObject) {
		if (isZero()) 
			if (anObject instanceof IMoney)
				return ((IMoney)anObject).isZero();
		if (anObject instanceof Money) {
			Money aMoney= (Money)anObject;
			return aMoney.currency().equals(currency())
							 && amount() == aMoney.amount();
		}
		return false;
	}
	@Override
	public int hashCode() {
		if (fAmount == 0)
			return 0;
		return fCurrency.hashCode()+fAmount;
	}
	public boolean isZero() {
		return amount() == 0;
	}
	public IMoney multiply(int factor) {
		return new Money(amount()*factor, currency());
	}
	public IMoney negate() {
		return new Money(-amount(), currency());
	}
	public IMoney subtract(IMoney m) {
		return add(m.negate());
	}
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("["+amount()+" "+currency()+"]");
		return buffer.toString();
	}
	public /*this makes no sense*/ void appendTo(MoneyBag m) {
		m.appendMoney(this);
	}
}