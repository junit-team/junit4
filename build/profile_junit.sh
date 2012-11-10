java -classpath ../bin:../lib/hamcrest-core-1.3.jar -agentlib:hprof=cpu=samples,depth=18 org.junit.runner.JUnitCore org.junit.tests.AllTests
cat java.hprof.txt