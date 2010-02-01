import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Categories.class)
@IncludeCategory(String.class)
@SuiteClasses( { SomeTestB.class, ParameterizedTestA.class })
// switch order of classes for slightly different behaviour
public class ParameterTokenSuite {
}