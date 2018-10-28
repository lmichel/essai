package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import exceptions.UnconsistantResourceException;
import mapping.nodes.MappingNodeId;

public class MappingNodeIdTest {

	@Test
	public void test() throws UnconsistantResourceException {
		final MappingNodeId nodeId = new MappingNodeId("id", "role", "name");
		assertEquals("id=id role=role", nodeId.toString());
	}

}
