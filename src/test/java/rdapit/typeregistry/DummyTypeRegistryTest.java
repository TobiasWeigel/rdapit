package rdapit.typeregistry;

import org.junit.Before;

public class DummyTypeRegistryTest extends BasicTypeRegistryTest {

	private DummyTypeRegistry typeRegistry;

	@Before
	public void createTypeRegistry() {
		typeRegistry = new DummyTypeRegistry();
	}

	@Override
	protected ITypeRegistry getITypeRegistry() {
		return typeRegistry;
	}

	@Override
	protected String constructTypePID(String identifierName) {
		return "DUMMY-" + identifierName;
	}

}
