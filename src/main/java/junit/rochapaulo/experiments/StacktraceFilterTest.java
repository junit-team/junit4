package junit.rochapaulo.experiments;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class StacktraceFilterTest {
    
    @Test
    public void shouldFail() {
        Assert.assertEquals(Long.valueOf(10), Long.valueOf(500));
    }
    
}
