package org.jenkinsci.plugins.jobdsl.promotions;

import static org.junit.Assert.assertFalse;
import hudson.model.FreeStyleProject;

import java.io.File;

import javaposse.jobdsl.plugin.RemovedJobAction;
import javaposse.jobdsl.plugin.ExecuteDslScripts;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class PromotionsDslContextExtensionTest {

	@Rule
	public JenkinsRule j = new JenkinsRule();

	@Test
	@Ignore
	public void shouldGenerateTheDefindedJob() throws Exception {
		// Given
		String dsl = FileUtils.readFileToString(new File("src/test/resources/example-dsl.groovy"));
		FreeStyleProject seedJob = j.createFreeStyleProject();
		seedJob.getBuildersList().add(
				new ExecuteDslScripts(new ExecuteDslScripts.ScriptLocation(Boolean.TRUE.toString(), null, dsl), false, RemovedJobAction.DELETE));
		// When
		j.buildAndAssertSuccess(seedJob);
		// Then
		FreeStyleProject createdJob = (FreeStyleProject) j.getInstance().getItem("test-job");
		assertFalse(createdJob.getProperties().isEmpty());
	}
}
