package rdapit.typeregistry;

import org.junit.Before;

import rdapit.pidsystem.PID;

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
	protected PID constructTypePID(String identifierName) {
		return new PID("DUMMY-" + identifierName);
	}

}
