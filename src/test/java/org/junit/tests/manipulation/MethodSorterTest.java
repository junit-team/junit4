package org.junit.tests.manipulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.SortWith;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Sorters;

@RunWith(Enclosed.class)
public class MethodSorterTest {

    private static Comparator<Description> nameDescending() {
        return new Comparator<Description>() {
            public int compare(Description o1, Description o2) {
                return o2.getDisplayName().compareTo(o1.getDisplayName());
            }
        };
    }
    
    public static class TestSortByName {

        @RunWith(JUnit4.class)
        @SortWith(Sorters.NAME_ASCENDING)
        public static class SortByName {

            public static String str = "";

            @Test
            public void testA() {
                str += "a";
            }

            @Test
            public void testB() {
                str += "b";
            }

            @Test
            public void testC() {
                str += "c";
            }

            @Test
            public void testD() {
                str += "d";
            }

            @Test
            public void testE() {
                str += "e";
            }
        }

        @Test
        public void testNameAscendingOrder() {
            Request request = Request.aClass(SortByName.class);
            new JUnitCore().run(request);
            assertEquals("abcde", SortByName.str);
        }
    }

    public static class TestSortByDefault {

        @RunWith(JUnit4.class)
        @SortWith
        public static class SortByDefault {

            public static List<String> list = new ArrayList<String>();

            @Test
            public void fun() {
                list.add("fun");
            }

            @Test
            public void ecstasy() {
                list.add("ecstasy");
            }

            @Test
            public void happy() {
                list.add("happy");
            }

            @Test
            public void meow() {
                list.add("meow");
            }

            @Test
            public void halirious() {
                list.add("halirious");
            }
        }
        
        @Test
        public void testDefaultOrder() {
            Request request = Request.aClass(SortByDefault.class);
            new JUnitCore().run(request);
            String[] strs = { "fun", "ecstasy", "happy", "meow" ,"halirious"};
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
            String[] results = new String[4];
            assertEquals(strs, SortByDefault.list.toArray(results));
        }
    }

    public static class TestSortByRandom {
        
        @RunWith(JUnit4.class)
        @SortWith(Sorters.RANDOM)
        public static class SortByRandom{
            
            public static String str="";
            
            @Test
            public void testA(){
                str += "a";
            }
            
            @Test
            public void testB(){
                str += "b";
            }
            
            @Test
            public void testC(){
                str += "c";
            }
            
            @Test
            public void testD(){
                str += "d";
            }
            
            @Test
            public void testE(){
                str += "e";
            }
            
            @Test
            public void testF(){
                str += "f";
            }
            
        }
        
        @Test
        public void testRandomOrder() {
            String lastExecutionOrder=null;
            int i=0;
            for(;i<10;i++,lastExecutionOrder=SortByRandom.str,SortByRandom.str=""){
                Request request = Request.aClass(SortByRandom.class);
                new JUnitCore().run(request);
                if(lastExecutionOrder==null){
                    lastExecutionOrder=SortByRandom.str;
                    continue;
                }
                if(!lastExecutionOrder.equals(SortByRandom.str))
                    break;
            }
            assertNotEquals(10,i);
        }
        
    }
    
    public static class TestSortByJVM{
        
        @RunWith(JUnit4.class)
        @SortWith(Sorters.JVM)
        public static class SortByJVM{
            
            public static String str="";
            
            @Test
            public void testA(){
                str+="a";
            }
            
            @Test
            public void testB(){
                str+="b";
            }
            
        }
        
        @Test
        public void testJVMOrder(){
            Request request = Request.aClass(SortByJVM.class);
            new JUnitCore().run(request);
            String test1=SortByJVM.str;
            SortByJVM.str="";
            request = Request.aClass(SortByJVM.class);
            new JUnitCore().run(request);
            String test2=SortByJVM.str;
            assertEquals(test1,test2);
        }
    }
    
    public static class TestOverrideSorting{
        
        @RunWith(JUnit4.class)
        @SortWith(Sorters.NAME_ASCENDING)
        public static class SortByNameAscending{
            
            public static String str="";
            
            @Test
            public void testA(){
                str+="a";
            }
            
            @Test
            public void testB(){
                str+="b";
            }
            
            @Test
            public void testC(){
                str+="c";
            }
            
        }
        
        @Test
        public void testOverideSorting(){
            Request ascending=Request.aClass(SortByNameAscending.class);
            new JUnitCore().run(ascending);
            assertEquals("abc",SortByNameAscending.str);
            SortByNameAscending.str="";
            Request descending = Request.aClass(SortByNameAscending.class).sortWith(nameDescending());
            new JUnitCore().run(descending);
            assertEquals("cba",SortByNameAscending.str);
        }
        
    }

}
