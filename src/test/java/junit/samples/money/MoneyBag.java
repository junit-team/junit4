package junit.samples.money;

import java.util.ArrayList;
import java.util.List;

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
public class MoneyBag implements IMoney {
    private List<Money> monies = new ArrayList<Money>(5);

    public static IMoney create(IMoney m1, IMoney m2) {
        MoneyBag result = new MoneyBag();
        m1.appendTo(result);
        m2.appendTo(result);
        return result.simplify();
    }

    public IMoney add(IMoney m) {
        return m.addMoneyBag(this);
    }

    public IMoney addMoney(Money m) {
        return MoneyBag.create(m, this);
    }

    public IMoney addMoneyBag(MoneyBag s) {
        return MoneyBag.create(s, this);
    }

    void appendBag(MoneyBag aBag) {
        for (Money each : aBag.monies) {
            appendMoney(each);
        }
    }

    void appendMoney(Money aMoney) {
        if (aMoney.isZero()) return;
        IMoney old = findMoney(aMoney.currency());
        if (old == null) {
            monies.add(aMoney);
            return;
        }
        monies.remove(old);
        Money sum = (Money) old.add(aMoney);
        if (sum.isZero()) {
            return;
        }
        monies.add(sum);
    }

    @Override
    public boolean equals(Object anObject) {
        if (isZero()) {
            if (anObject instanceof IMoney) {
                return ((IMoney) anObject).isZero();
            }
        }

        if (anObject instanceof MoneyBag) {
            MoneyBag aMoneyBag = (MoneyBag) anObject;
            if (aMoneyBag.monies.size() != monies.size()) {
                return false;
            }

            for (Money each : monies) {
                if (!aMoneyBag.contains(each)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Money findMoney(String currency) {
        for (Money each : monies) {
            if (each.currency().equals(currency)) {
                return each;
            }
        }
        return null;
    }

    private boolean contains(Money m) {
        Money found = findMoney(m.currency());
        if (found == null) return false;
        return found.amount() == m.amount();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Money each : monies) {
            hash ^= each.hashCode();
        }
        return hash;
    }

    public boolean isZero() {
        return monies.size() == 0;
    }

    public IMoney multiply(int factor) {
        MoneyBag result = new MoneyBag();
        if (factor != 0) {
            for (Money each : monies) {
                result.appendMoney((Money) each.multiply(factor));
            }
        }
        return result;
    }

    public IMoney negate() {
        MoneyBag result = new MoneyBag();
        for (Money each : monies) {
            result.appendMoney((Money) each.negate());
        }
        return result;
    }

    private IMoney simplify() {
        if (monies.size() == 1) {
            return monies.iterator().next();
        }
        return this;
    }

    public IMoney subtract(IMoney m) {
        return add(m.negate());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Money each : monies) {
            sb.append(each);
        }
        sb.append("}");
        return sb.toString();
    }

    public void appendTo(MoneyBag m) {
        m.appendBag(this);
    }
}