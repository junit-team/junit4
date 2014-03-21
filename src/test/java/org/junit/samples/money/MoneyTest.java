package org.junit.samples.money;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.JUnit4TestAdapter;
import junit.samples.money.IMoney;
import junit.samples.money.Money;
import junit.samples.money.MoneyBag;
import org.junit.Before;
import org.junit.Test;

public class MoneyTest {
    private Money money12CHF;
    private Money money14CHF;
    private Money money7USD;
    private Money money21USD;

    private IMoney moneyBag1;
    private IMoney moneyBag2;

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(MoneyTest.class);
    }

    @Before
    public void setUp() {
        money12CHF = new Money(12, "CHF");
        money14CHF = new Money(14, "CHF");
        money7USD = new Money(7, "USD");
        money21USD = new Money(21, "USD");

        moneyBag1 = MoneyBag.create(money12CHF, money7USD);
        moneyBag2 = MoneyBag.create(money14CHF, money21USD);
    }

    @Test
    public void testBagMultiply() {
        // {[12 CHF][7 USD]} *2 == {[24 CHF][14 USD]}
        IMoney expected = MoneyBag.create(new Money(24, "CHF"), new Money(14, "USD"));
        assertEquals(expected, moneyBag1.multiply(2));
        assertEquals(moneyBag1, moneyBag1.multiply(1));
        assertTrue(moneyBag1.multiply(0).isZero());
    }

    @Test
    public void testBagNegate() {
        // {[12 CHF][7 USD]} negate == {[-12 CHF][-7 USD]}
        IMoney expected = MoneyBag.create(new Money(-12, "CHF"), new Money(-7, "USD"));
        assertEquals(expected, moneyBag1.negate());
    }

    @Test
    public void testBagSimpleAdd() {
        // {[12 CHF][7 USD]} + [14 CHF] == {[26 CHF][7 USD]}
        IMoney expected = MoneyBag.create(new Money(26, "CHF"), new Money(7, "USD"));
        assertEquals(expected, moneyBag1.add(money14CHF));
    }

    @Test
    public void testBagSubtract() {
        // {[12 CHF][7 USD]} - {[14 CHF][21 USD] == {[-2 CHF][-14 USD]}
        IMoney expected = MoneyBag.create(new Money(-2, "CHF"), new Money(-14, "USD"));
        assertEquals(expected, moneyBag1.subtract(moneyBag2));
    }

    @Test
    public void testBagSumAdd() {
        // {[12 CHF][7 USD]} + {[14 CHF][21 USD]} == {[26 CHF][28 USD]}
        IMoney expected = MoneyBag.create(new Money(26, "CHF"), new Money(28, "USD"));
        assertEquals(expected, moneyBag1.add(moneyBag2));
    }

    @Test
    public void testIsZero() {
        assertTrue(moneyBag1.subtract(moneyBag1).isZero());
        assertTrue(MoneyBag.create(new Money(0, "CHF"), new Money(0, "USD")).isZero());
    }

    @Test
    public void testMixedSimpleAdd() {
        // [12 CHF] + [7 USD] == {[12 CHF][7 USD]}
        IMoney expected = MoneyBag.create(money12CHF, money7USD);
        assertEquals(expected, money12CHF.add(money7USD));
    }

    @Test
    public void testBagNotEquals() {
        IMoney bag = MoneyBag.create(money12CHF, money7USD);
        assertFalse(bag.equals(new Money(12, "DEM").add(money7USD)));
    }

    @Test
    public void testMoneyBagEquals() {
        assertTrue(!moneyBag1.equals(null));

        assertEquals(moneyBag1, moneyBag1);
        IMoney equal = MoneyBag.create(new Money(12, "CHF"), new Money(7, "USD"));
        assertTrue(moneyBag1.equals(equal));
        assertTrue(!moneyBag1.equals(money12CHF));
        assertTrue(!money12CHF.equals(moneyBag1));
        assertTrue(!moneyBag1.equals(moneyBag2));
    }

    @Test
    public void testMoneyBagHash() {
        IMoney equal = MoneyBag.create(new Money(12, "CHF"), new Money(7, "USD"));
        assertEquals(moneyBag1.hashCode(), equal.hashCode());
    }

    @Test
    public void testMoneyEquals() {
        assertTrue(!money12CHF.equals(null));
        Money equalMoney = new Money(12, "CHF");
        assertEquals(money12CHF, money12CHF);
        assertEquals(money12CHF, equalMoney);
        assertEquals(money12CHF.hashCode(), equalMoney.hashCode());
        assertTrue(!money12CHF.equals(money14CHF));
    }

    @Test
    public void zeroMoniesAreEqualRegardlessOfCurrency() {
        Money zeroDollars = new Money(0, "USD");
        Money zeroFrancs = new Money(0, "CHF");

        assertEquals(zeroDollars, zeroFrancs);
        assertEquals(zeroDollars.hashCode(), zeroFrancs.hashCode());
    }

    @Test
    public void testMoneyHash() {
        assertTrue(!money12CHF.equals(null));
        Money equal = new Money(12, "CHF");
        assertEquals(money12CHF.hashCode(), equal.hashCode());
    }

    @Test
    public void testSimplify() {
        IMoney money = MoneyBag.create(new Money(26, "CHF"), new Money(28, "CHF"));
        assertEquals(new Money(54, "CHF"), money);
    }

    @Test
    public void testNormalize2() {
        // {[12 CHF][7 USD]} - [12 CHF] == [7 USD]
        Money expected = new Money(7, "USD");
        assertEquals(expected, moneyBag1.subtract(money12CHF));
    }

    @Test
    public void testNormalize3() {
        // {[12 CHF][7 USD]} - {[12 CHF][3 USD]} == [4 USD]
        IMoney ms1 = MoneyBag.create(new Money(12, "CHF"), new Money(3, "USD"));
        Money expected = new Money(4, "USD");
        assertEquals(expected, moneyBag1.subtract(ms1));
    }

    @Test
    public void testNormalize4() { // [12 CHF] - {[12 CHF][3 USD]} == [-3 USD]
        IMoney ms1 = MoneyBag.create(new Money(12, "CHF"), new Money(3, "USD"));
        Money expected = new Money(-3, "USD");
        assertEquals(expected, money12CHF.subtract(ms1));
    }

    @Test
    public void testPrint() {
        assertEquals("[12 CHF]", money12CHF.toString());
    }

    @Test
    public void testSimpleAdd() {
        // [12 CHF] + [14 CHF] == [26 CHF]
        Money expected = new Money(26, "CHF");
        assertEquals(expected, money12CHF.add(money14CHF));
    }

    @Test
    public void testSimpleBagAdd() {
        // [14 CHF] + {[12 CHF][7 USD]} == {[26 CHF][7 USD]}
        IMoney expected = MoneyBag.create(new Money(26, "CHF"), new Money(7, "USD"));
        assertEquals(expected, money14CHF.add(moneyBag1));
    }

    @Test
    public void testSimpleMultiply() {
        // [14 CHF] *2 == [28 CHF]
        Money expected = new Money(28, "CHF");
        assertEquals(expected, money14CHF.multiply(2));
    }

    @Test
    public void testSimpleNegate() {
        // [14 CHF] negate == [-14 CHF]
        Money expected = new Money(-14, "CHF");
        assertEquals(expected, money14CHF.negate());
    }

    @Test
    public void testSimpleSubtract() {
        // [14 CHF] - [12 CHF] == [2 CHF]
        Money expected = new Money(2, "CHF");
        assertEquals(expected, money14CHF.subtract(money12CHF));
    }
}