package rdapit.typeregistry;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import rdapit.pidsystem.PID;

public abstract class BasicTypeRegistryTest {

	protected abstract ITypeRegistry getITypeRegistry();

	protected abstract PID constructTypePID(String identifierName);

	@After
	public void removeRegisteredTypes() throws IOException {
		ITypeRegistry tr = getITypeRegistry();
		tr.removePropertyDefinition(constructTypePID("propertytest1"));
		tr.removePropertyDefinition(constructTypePID("propertytest2"));
		tr.removePropertyDefinition(constructTypePID("propertytest3"));
	}

	@Test
	public void testBasicProperties() throws IOException {
		ITypeRegistry tr = getITypeRegistry();
		tr.createPropertyDefinition(new PropertyDefinition(constructTypePID("propertytest1"), "Test1", new PID(PropertyDefinition.ELEMENTAL_VALUETYPE_STRING)));
		tr.createPropertyDefinition(new PropertyDefinition(constructTypePID("propertytest2"), "Test2", new PID(PropertyDefinition.ELEMENTAL_VALUETYPE_STRING)));
		tr.createPropertyDefinition(new PropertyDefinition(constructTypePID("propertytest3"), "Test1", new PID(PropertyDefinition.ELEMENTAL_VALUETYPE_STRING)));
		// query non-existing property
		assertNull(tr.queryPropertyDefinition(constructTypePID("this-property-does-not-exist")));
		assertEquals(tr.queryPropertyDefinitionByName("this-property-name-is-not-in-use").size(), 0);
		// query single unique property
		PropertyDefinition pd = tr.queryPropertyDefinition(constructTypePID("propertytest1"));
		assertEquals(pd.getIdentifier(), constructTypePID("propertytest1"));
		assertEquals(pd.getName(), "Test1");
		assertEquals(pd.getValueType(), new PID(PropertyDefinition.ELEMENTAL_VALUETYPE_STRING));
		// query multiple properties with same name
		List<PropertyDefinition> pdl = tr.queryPropertyDefinitionByName("Test1");
		assertEquals(pdl.size(), 2);
		assertThat("Test1", anyOf(is(pdl.get(0).getName()), is(pdl.get(1).getName())));
		assertThat("Test2", not(anyOf(is(pdl.get(0).getName()), is(pdl.get(1).getName()))));
	}

}
