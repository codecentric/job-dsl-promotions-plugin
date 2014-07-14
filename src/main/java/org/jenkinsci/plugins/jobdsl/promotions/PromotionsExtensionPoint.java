package org.jenkinsci.plugins.jobdsl.promotions;

import groovy.lang.Closure;
import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.Item;
import hudson.model.Items;
import hudson.util.IOUtils;
import javaposse.jobdsl.dsl.WithXmlAction;
import javaposse.jobdsl.dsl.helpers.PropertiesContext;
import javaposse.jobdsl.plugin.api.ContextExtensionPoint;
import javaposse.jobdsl.plugin.api.DslMethod;
import javaposse.jobdsl.plugin.api.DslSession;

import java.io.*;
import java.util.ArrayList;

@Extension
public class PromotionsExtensionPoint extends ContextExtensionPoint {

	@DslMethod(context = PropertiesContext.class)
	public String promotion(Runnable closure) {
        PromotionsContextHelper contextHelper = new PromotionsContextHelper(new ArrayList<WithXmlAction>(), null);
		String name = contextHelper.promotion((Closure) closure);
        DslSession.getCurrentSession().setData("helper", contextHelper);
        DslSession.getCurrentSession().setData("name", name);
		return contextHelper.getXml();
	}

	@Override
	public void notifyItemCreated(Item item) {
        PromotionsContextHelper contextHelper = (PromotionsContextHelper)DslSession.getCurrentSession().getData("helper");
        String name = (String)DslSession.getCurrentSession().getData("name");
        String xml = contextHelper.getSubXml();
		File dir = new File(item.getRootDir(),  "promotions/" + name);
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

	@Override
	public void notifyItemUpdated(Item item) {
		System.out.println("item updated: " + item);
		// new File(configId.getType().toString() +
		// configId.getRelativePath().replace("/", "_") + jobName +
		// ext).write(config.getConfig(configId));
	}

}
