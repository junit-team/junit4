package junit.tests.runner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import junit.tests.framework.Success;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.tests.running.methods.AnnotationTest;

public class ResultTest extends TestCase {

	public void testRunFailureResultCanBeSerialised() throws Exception {
		JUnitCore runner = new JUnitCore();
		Result result = runner.run(AnnotationTest.FailureTest.class);
		assertResultSerializable(result);
	}

	public void testRunSuccessResultCanBeSerialised() throws Exception {
		JUnitCore runner = new JUnitCore();
		Result result = runner.run(Success.class);
		assertResultSerializable(result);
	}

	private void assertResultSerializable(Result result) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		new ObjectOutputStream(byteArrayOutputStream).writeObject(result);
		byte[] bytes = byteArrayOutputStream.toByteArray();
		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
		Result fromStream = (Result) objectInputStream.readObject();
		assertNotNull(fromStream);
	}
}
