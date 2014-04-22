package org.junit.tests.manipulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.SortWith;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Sorters;
import org.junit.runners.Suite;

@RunWith(Enclosed.class)
public class ClassSorterTest {

    private static Comparator<Description> nameDescending() {
        return new Comparator<Description>() {
            public int compare(Description o1, Description o2) {
                return o2.getClassName().compareTo(o1.getClassName());
            }
        };
    }
    
    public static class SortClassInSuiteByName {

        private static String str = "";

        @RunWith(JUnit4.class)
        public static class ClassE {

            @Test
            public void testE() {
                str += "e";
            }

        }

        @RunWith(JUnit4.class)
        public static class ClassB {

            @Test
            public void testB() {
                str += "b";
            }

        }

        @RunWith(JUnit4.class)
        public static class ClassC {

            @Test
            public void testC() {
                str += "c";
            }

        }

        @RunWith(Suite.class)
        @Suite.SuiteClasses({ ClassE.class, ClassB.class, ClassC.class })
        @SortWith(Sorters.NAME_ASCENDING)
        public static class ClassSuiteSortByName {

        }

        @Test
        public void testSortClassByName() {
            Request request = Request.aClass(ClassSuiteSortByName.class);
            new JUnitCore().run(request);
            assertEquals("bce", str);
        }

    }

    public static class SortClassInSuiteByDefault {

        private static List<String> list = new ArrayList<String>();

        @RunWith(JUnit4.class)
        public static class Fantastic {

            @Test
            public void testAdd() {
                list.add(Fantastic.class.getName());
            }

        }

        @RunWith(JUnit4.class)
        public static class Fabulent {

            @Test
            public void testAdd() {
                list.add(Fabulent.class.getName());
            }

        }

        @RunWith(JUnit4.class)
        public static class Splendid {

            @Test
            public void testAdd() {
                list.add(Splendid.class.getName());
            }

        }

        @RunWith(Suite.class)
        @Suite.SuiteClasses({ Fantastic.class, Fabulent.class, Splendid.class })
        @SortWith(Sorters.DEFAULT)
        public static class ClassSuiteSortByDefault {

        }

        @Test
        public void testSortClassByDefault() {
            Request request = Request.aClass(ClassSuiteSortByDefault.class);
            new JUnitCore().run(request);
            String[] strs = { Fantastic.class.getName(),
                    Fabulent.class.getName(), Splendid.class.getName() };
            Arrays.sort(strs, new Comparator<String>() {

                public int compare(String o1, String o2) {
                    int item1 = o1.hashCode();
                    int item2 = o2.hashCode();
                    if (item1 != item2) {
                        return item1 < item2 ? -1 : 1;
                    }
                    return 0;
                }

            });
            String[] results = new String[3];
            assertEquals(strs, SortClassInSuiteByDefault.list.toArray(results));
        }

    }

    public static class SortClassInSuiteByRandom {

        public static String str = "";

        @RunWith(JUnit4.class)
        public static class ClassRA {

            @Test
            public void testAdd() {
                str += "RA";
            }

        }

        @RunWith(JUnit4.class)
        public static class ClassRB {

            @Test
            public void testAdd() {
                str += "RB";
            }

        }

        @RunWith(JUnit4.class)
        public static class ClassRC {

            @Test
            public void testAdd() {
                str += "RC";
            }

        }

        @RunWith(JUnit4.class)
        public static class ClassRD {

            @Test
            public void testAdd() {
                str += "RD";
            }

        }

        @RunWith(JUnit4.class)
        public static class ClassRE {

            @Test
            public void testAdd() {
                str += "RE";
            }

        }

        @RunWith(Suite.class)
        @Suite.SuiteClasses({ ClassRA.class, ClassRB.class, ClassRC.class,
                ClassRD.class, ClassRE.class })
        @SortWith(Sorters.RANDOM)
        public static class ClassSuiteSortByRandom {

        }

        @Test
        public void testSortClassByRandom() {
            String lastExecutionOrder = null;
            int i = 0;
            for (; i < 10; i++, lastExecutionOrder = SortClassInSuiteByRandom.str, lastExecutionOrder = SortClassInSuiteByRandom.str = "") {
                Request request = Request.aClass(ClassSuiteSortByRandom.class);
                new JUnitCore().run(request);
                if (lastExecutionOrder == null) {
                    lastExecutionOrder = SortClassInSuiteByRandom.str;
                    continue;
                }
                if (!lastExecutionOrder.equals(SortClassInSuiteByRandom.str))
                    break;
            }
            assertNotEquals(10, i);
        }
    }

    public static class SortClassInSuiteByJVM {

        public static String str = "";

        @RunWith(JUnit4.class)
        public static class ClassJVMA {

            @Test
            public void testAdd() {
                str += "A";
            }

        }

        @RunWith(JUnit4.class)
        public static class ClassJVMB {

            @Test
            public void testAdd() {
                str += "B";
            }

        }

        @RunWith(Suite.class)
        @Suite.SuiteClasses({ ClassJVMA.class, ClassJVMB.class })
        @SortWith(Sorters.JVM)
        public static class ClassSuiteSortByJVM {

        }

        @Test
        public void testSortClassByJVM() {
            Request request = Request.aClass(ClassSuiteSortByJVM.class);
            new JUnitCore().run(request);
            String test1 = SortClassInSuiteByJVM.str;
            SortClassInSuiteByJVM.str = "";
            request = Request.aClass(ClassSuiteSortByJVM.class);
            new JUnitCore().run(request);
            String test2 = SortClassInSuiteByJVM.str;
            assertEquals(test1, test2);
        }

    }

    public static class OverrideSortingMethodInSuite {

        public static String str = "";

        @RunWith(JUnit4.class)
        public static class ClassA {

            @Test
            public void testAdd() {
                str += "a";
            }

        }

        @RunWith(JUnit4.class)
        public static class ClassB {

            @Test
            public void testAdd() {
                str += "b";
            }

        }

        @RunWith(Suite.class)
        @Suite.SuiteClasses({ ClassA.class, ClassB.class })
        @SortWith(Sorters.NAME_ASCENDING)
        public static class ClassSuiteSortByNameAscending {

        }
        
        @Test
        public void testOverrideClassOrder() {
            Request request = Request.aClass(ClassSuiteSortByNameAscending.class);
            new JUnitCore().run(request);
            assertEquals("ab",OverrideSortingMethodInSuite.str);
            OverrideSortingMethodInSuite.str="";
            request = Request.aClass(ClassSuiteSortByNameAscending.class).sortWith(nameDescending());
            new JUnitCore().run(request);
            assertEquals("ba",OverrideSortingMethodInSuite.str);
        }

    }

}
