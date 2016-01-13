package org.jenkinsci.plugins.jobdsl.promotions;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import groovy.util.Node;
import hudson.model.Items;
import hudson.plugins.promoted_builds.conditions.SelfPromotionCondition;
import hudson.plugins.promoted_builds.dsl.PromotionProcess;
import hudson.plugins.promoted_builds.dsl.PromotionProcessConverter;
import hudson.plugins.promoted_builds.dsl.ReleasePromotionCondition;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class PromotionProcessConverterTest {
	
	@Test
	public void shouldGenerateValidXml() throws Exception {
		//Given
		PromotionProcess pp = new PromotionProcess(); 
		pp.setName("testname");
		Node node = new Node(null, "hudson.tasks.Shell");
		Node subNode = new Node(node, "command");
		subNode.setValue("echo hello;");		
		pp.getConditions().add(new ReleasePromotionCondition());
		pp.getConditions().add(new SelfPromotionCondition(true));		
		pp.getBuildSteps().add(node);
		//When
		Items.XSTREAM2.registerConverter(new PromotionProcessConverter());
		String xml =  Items.XSTREAM2.toXML(pp);
		//Then
		assertNotNull(xml);
		assertTrue(StringUtils.contains(xml, "hudson.plugins.promoted__builds.PromotionProcess"));
	}
}
