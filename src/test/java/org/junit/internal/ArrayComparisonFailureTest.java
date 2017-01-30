package org.junit.internal;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ArrayComparisonFailureTest {

    private static final String ARRAY_COMPARISON_FAILURE_411 = "arrayComparisonFailure_411";
    private static final String ARRAY_COMPARISON_FAILURE_412 = "arrayComparisonFailure_412";

    /*
        Test compatibility of older versions of ArrayComparisonFailure
        Setup:
        - checkout prior versions of the codebase (r4.11, r4.12 in this case)
        - catch the exception resulting from:
            assertArrayEquals(new int[]{0, 1}, new int[]{0, 5});
        - serialize the resulting exception to a file, moving into the test/resources path
            Ex., for v4.11's resulting exception {@link org/junit/internal/arrayComparisonFailure_411}
        Current unit test:
        - deserialize the above files casting it to the current version of the class
            (catches any forward incompatibility with missing fields)
        - assert the results from existing methods: getCause(), toString() -> getMessage()
            (catches incompatible usages of fields)

        This does not test if an instance of the current version of the class is able to deserialize to a previous ver.
     */

    @Test
    public void classShouldAccept411Version() throws Exception {
        assertFailureSerializableFromOthers(ARRAY_COMPARISON_FAILURE_411);
    }

    @Test
    public void classShouldAccept412Version() throws Exception {
        assertFailureSerializableFromOthers(ARRAY_COMPARISON_FAILURE_412);
    }

    private void assertFailureSerializableFromOthers(String failureFileName) throws IOException,
            ClassNotFoundException {
        try {
            assertArrayEquals(new int[]{0, 1}, new int[]{0, 5});
            fail();
        } catch (ArrayComparisonFailure e) {
            ArrayComparisonFailure arrayComparisonFailureFromFile = deserializeFailureFromFile(failureFileName);
            assertNotNull("ArrayComparisonFailure.getCause() should fallback to the deprecated fCause field"
                            + " for compatibility with older versions of junit4 that didn't use Throwable.initCause().",
                    arrayComparisonFailureFromFile.getCause());
            assertEquals(e.getCause().toString(), arrayComparisonFailureFromFile.getCause().toString());
            assertEquals(e.toString(), arrayComparisonFailureFromFile.toString());
        }
    }

    private ArrayComparisonFailure deserializeFailureFromFile(String fileName) throws IOException,
            ClassNotFoundException {
        InputStream resource = getClass().getResourceAsStream(fileName);
        ObjectInputStream objectInputStream = new ObjectInputStream(resource);
        return (ArrayComparisonFailure) objectInputStream.readObject();
    }

}