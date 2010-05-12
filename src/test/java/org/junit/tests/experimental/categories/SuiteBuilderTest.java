package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.Arrays;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.CategoryFilter;
import org.junit.experimental.runners.Listed;
import org.junit.experimental.runners.SuiteBuilder;
import org.junit.experimental.runners.SuiteBuilder.Classes;
import org.junit.experimental.runners.SuiteBuilder.RunnerFilter;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class SuiteBuilderTest {
	static class Yes {
	}

	static class No {
	}

	// TODO: test multiple filters, multiple class sources
	
	@Category(Yes.class)
	public static class Yes1 {
		@Test
		public void yes1() {
		}
	}

	@Category(Yes.class)
	public static class Yes2 {
		@Test
		public void yes2() {
		}
	}

	@Category(No.class)
	public static class No1 {
		@Test
		public void no1() {
		}
	}

	@RunWith(SuiteBuilder.class)
	public static class OnlyYesJustOne {
		@Classes
		public Listed classes= new Listed(Yes1.class, No1.class);
		
		@RunnerFilter
		public CategoryFilter filter= CategoryFilter.include(Yes.class);
	}

	@RunWith(SuiteBuilder.class)
	public static class OnlyYes {
		@Classes
		public Listed classes= new Listed(Yes1.class, Yes2.class, No1.class);

		@RunnerFilter
		public CategoryFilter filter= CategoryFilter.include(Yes.class);
	}

	@RunWith(SuiteBuilder.class)
	public static class Everything {
		@Classes
		public Listed classes= new Listed(Yes1.class, Yes2.class, No1.class);
	}

	@RunWith(SuiteBuilder.class)
	public static class Nos {
		@Classes
		public Listed classes= new Listed(Yes1.class, Yes2.class, No1.class);

		@RunnerFilter
		public CategoryFilter filter= CategoryFilter.include(No.class);
	}

	@Test
	public void gatherClasses() throws InitializationError {
		assertEquals(2, new SuiteBuilder(OnlyYesJustOne.class).gatherClasses().size());
	}
	
	@Test public void suiteBuilderDescription() throws InitializationError {
		Description description= new SuiteBuilder(Nos.class).getDescription();
		assertEquals(1, description.getChildren().size());
		assertEquals(Nos.class.getName(), description.getDisplayName());
		assertEquals(No1.class.getName(), description.getChildren().get(0).getDisplayName());
	}

	@Test
	public void onlyRunOne() {
		Result result= new JUnitCore().run(OnlyYesJustOne.class);
		assertEquals(1, result.getRunCount());
		assertThat(testResult(OnlyYesJustOne.class), isSuccessful());
	}

	@Test
	public void runTwo() {
		Result result= new JUnitCore().run(OnlyYes.class);
		assertEquals(2, result.getRunCount());
		assertThat(testResult(OnlyYes.class), isSuccessful());
	}

	@Test
	public void runAllThree() {
		Result result= new JUnitCore().run(Everything.class);
		assertEquals(3, result.getRunCount());
		assertThat(testResult(Everything.class), isSuccessful());
	}

	@Test
	public void runOneNo() {
		Result result= new JUnitCore().run(Nos.class);
		assertEquals(1, result.getRunCount());
		assertThat(testResult(Nos.class), isSuccessful());
	}

	@Test
	public void matchingRunnersOnCategories() throws InitializationError {
		Runner blockJUnit4ClassRunner= new BlockJUnit4ClassRunner(Yes1.class);
		assertEquals(1, CategoryFilter.include(Yes.class).matchingRunners(
				Arrays.asList(blockJUnit4ClassRunner)).size());
	}
}
