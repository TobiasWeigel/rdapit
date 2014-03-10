package rdapit;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PropertyTest {

	@Test
	public void simpleProperties() throws Exception {
		PropertyFactory factory = new PropertyFactory();

		ArrayList<Property<?>> properties = new ArrayList<>();

		Property<?> p1 = factory.generateProperty("one", "I'm a string!");
		Property<?> p2 = factory.generateProperty("two", 23);

		properties.add(p1);
		properties.add(p2);

		for (Property<?> p : properties) {
			if (p.getValueType().equals(Property.TYPE_INTEGER)) {
				assertTrue(p.getValue() instanceof Integer);
				assertEquals(p.getValue(), 23);
			} else if (p.getValueType().equals(Property.TYPE_STRING)) {
				assertTrue(p.getValue() instanceof String);
				assertEquals(p.getValue(), "I'm a string!");
			} else
				fail("Value type not covered!");
		}
	}

}
