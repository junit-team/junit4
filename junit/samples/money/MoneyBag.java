package junit.samples.money;

import java.util.*;

/**
 * A MoneyBag defers exchange rate conversions. For example adding 
 * 12 Swiss Francs to 14 US Dollars is represented as a bag 
 * containing the two Monies 12 CHF and 14 USD. Adding another
 * 10 Swiss francs gives a bag with 22 CHF and 14 USD. Due to 
 * the deferred exchange rate conversion we can later value a 
 * MoneyBag with different exchange rates.
 *
 * A MoneyBag is represented as a list of Monies and provides 
 * different constructors to create a MoneyBag. 
 */ 
class MoneyBag implements IMoney {
	private Vector fMonies= new Vector(5);

	private MoneyBag() {
	}
	MoneyBag(Money bag[]) {
		for (int i= 0; i < bag.length; i++) {
			if (!bag[i].isZero())
				appendMoney(bag[i]);
		}
	}
	MoneyBag(Money m1, Money m2) {
		appendMoney(m1);
		appendMoney(m2);
	}
	MoneyBag(Money m, MoneyBag bag) {
		appendMoney(m);
		appendBag(bag);
	}
	MoneyBag(MoneyBag m1, MoneyBag m2) {
		appendBag(m1);
		appendBag(m2);
	}
	public IMoney add(IMoney m) {
		return m.addMoneyBag(this);
	}
	public IMoney addMoney(Money m) {
		return (new MoneyBag(m, this)).simplify();
	}
	public IMoney addMoneyBag(MoneyBag s) {
		return (new MoneyBag(s, this)).simplify();
	}
	private void appendBag(MoneyBag aBag) {
		for (Enumeration e= aBag.fMonies.elements(); e.hasMoreElements(); )
			appendMoney((Money)e.nextElement());
	}
	private void appendMoney(Money aMoney) {
		IMoney old= findMoney(aMoney.currency());
		if (old == null) {
			fMonies.addElement(aMoney);
			return;
		}
		fMonies.removeElement(old);
		IMoney sum= old.add(aMoney);
		if (sum.isZero()) 
			return;
		fMonies.addElement(sum);
	}
	private boolean contains(Money aMoney) {
		Money m= findMoney(aMoney.currency());
		return m.amount() == aMoney.amount();
	}
	public boolean equals(Object anObject) {
		if (isZero())
			if (anObject instanceof IMoney)
				return ((IMoney)anObject).isZero();

		if (anObject instanceof MoneyBag) {
			MoneyBag aMoneyBag= (MoneyBag)anObject;
			if (aMoneyBag.fMonies.size() != fMonies.size())
				return false;

		    for (Enumeration e= fMonies.elements(); e.hasMoreElements(); ) {
		        Money m= (Money) e.nextElement();
				if (!aMoneyBag.contains(m))
					return false;
			}
			return true;
		}
		return false;
	}
	private Money findMoney(String currency) {
		for (Enumeration e= fMonies.elements(); e.hasMoreElements(); ) {
			Money m= (Money) e.nextElement();
			if (m.currency().equals(currency))
				return m;
		}
		return null;
	}
	public int hashCode() {
		int hash= 0;
	    for (Enumeration e= fMonies.elements(); e.hasMoreElements(); ) {
	        Object m= e.nextElement();
			hash^= m.hashCode();
		}
	    return hash;
	}
	public boolean isZero() {
		return fMonies.size() == 0;
	}
	public IMoney multiply(int factor) {
		MoneyBag result= new MoneyBag();
		if (factor != 0) {
			for (Enumeration e= fMonies.elements(); e.hasMoreElements(); ) {
				Money m= (Money) e.nextElement();
				result.appendMoney((Money)m.multiply(factor));
			}
		}
		return result;
	}
	public IMoney negate() {
		MoneyBag result= new MoneyBag();
	    for (Enumeration e= fMonies.elements(); e.hasMoreElements(); ) {
	        Money m= (Money) e.nextElement();
	        result.appendMoney((Money)m.negate());
		}
		return result;
	}
	private IMoney simplify() {
		if (fMonies.size() == 1)
			return (IMoney)fMonies.elements().nextElement();
		return this;
	}
	public IMoney subtract(IMoney m) {
		return add(m.negate());
	}
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		for (Enumeration e= fMonies.elements(); e.hasMoreElements(); )
		    buffer.append((Money) e.nextElement());
		buffer.append("}");
		return buffer.toString();
	}
}