package org.jenkinsci.plugins.jobdsl.promotions;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import hudson.model.Items;
import hudson.plugins.promoted_builds.dsl.ManualCondition;
import hudson.plugins.promoted_builds.dsl.ManualConditionConverter;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class ManualConditionConverterTest {
	
	@Test
	public void shouldGenerateValidXml() throws Exception {
		//Given
		ManualCondition mc = new ManualCondition();
		mc.setUsers("testusers");
		//When
		Items.XSTREAM2.registerConverter(new ManualConditionConverter());
		String xml =  Items.XSTREAM2.toXML(mc);
		//Then
		assertNotNull(xml);
		System.out.println(xml);
		assertTrue(StringUtils.contains(xml, "hudson.plugins.promoted__builds.conditions.ManualCondition"));
	}
}
