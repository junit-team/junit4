import static org.junit.Assert.fail;
import org.junit.rules.MethodRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


public class CategoryTest {
	public static class A {
		public void a() {
			// pass
		}
		
		@Category(SlowTests.class)
		public void b() {
			fail();
		}
	}
	
	@Category(SlowTests.class)	
	public static class B {
		public void c() {
			
		}
	}

	public static class C {
		public void d() {
			
		}
	}
	
	@RunWith(Suite.class)
	@SuiteClasses({A.class, B.class, C.class})
	public static class AbcTest {
		enum JUnitCategories {
			INTEGRATION, UNIT, GOOD, BAD;
		}
		
		enum GoogleCategories extends JUnitCategories {
			SMALL, MEDIUM, LARGE, ENORMOUS;
		}
		
		@SuiteRule public Filter filter = new CategoryFilter(Category.class, SLOW);
		@SuiteRule public Filter decimator = new RandomFilter(0.1);
		
		@SuiteRule public SuiteRule globalTimeout = GlobalTimeout.createTimeoutOnEachMethod(1000);
		
		public static MethodRule GOOGLE_DEFAULT_TIMEOUT = new Timeout(1000);
		
		SuiteRule 
		@SuiteRule public MethodRule timeout = new Timeout(1000);
		
		@SuiteRule public MethodRuleDistributor timeoutDistributer = 
			new MethodRuleDistributor(new Timeout(1000));
	}
}
