package org.jenkinsci.plugins.jobdsl.promotions;

import groovy.lang.Closure;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Items;
import hudson.model.Descriptor.FormException;
import hudson.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javaposse.jobdsl.dsl.helpers.properties.PropertiesContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslEnvironment;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension
public class PromotionsExtensionPoint extends ContextExtensionPoint {

	@DslExtensionMethod(context = PropertiesContext.class)
	public Object promotions(Runnable closure, DslEnvironment dslEnvironment) throws FormException, IOException {
		PromotionsContextHelper contextHelper = new PromotionsContextHelper();
		List<String> activeProcessNames = contextHelper.promotions((Closure) closure);
		dslEnvironment.put("helper", contextHelper);
		dslEnvironment.put("names", activeProcessNames);
		PromotionJobProperty jobProperty = new PromotionJobProperty(activeProcessNames);
		return jobProperty;
	}

	@Override
	public void notifyItemCreated(Item item, DslEnvironment dslEnvironment) {
		System.out.println("item created: " + item);
		PromotionsContextHelper contextHelper = (PromotionsContextHelper) dslEnvironment.get("helper");
		List<String> names = (List<String>) dslEnvironment.get("names");
		for (String name : names) {
			String xml = contextHelper.getSubXml(name);
			File dir = new File(item.getRootDir(), "promotions/" + name);
			File configXml = Items.getConfigFile(dir).getFile();
			configXml.getParentFile().mkdirs();
			try {
				InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
				IOUtils.copy(in, configXml);
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException("Error handling extension code", e);
			} catch (IOException e) {
				throw new IllegalStateException("Error handling extension code", e);
			}
		}
	}

	@Override
	public void notifyItemUpdated(Item item, DslEnvironment dslEnvironment) {
		System.out.println("item updated: " + item);
		// new File(configId.getType().toString() +
		// configId.getRelativePath().replace("/", "_") + jobName +
		// ext).write(config.getConfig(configId));
	}

}
