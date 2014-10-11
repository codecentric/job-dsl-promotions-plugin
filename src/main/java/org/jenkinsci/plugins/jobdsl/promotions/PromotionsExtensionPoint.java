package org.jenkinsci.plugins.jobdsl.promotions;

import groovy.lang.Closure;
import groovy.util.Node;
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
import java.util.List;

@Extension
public class PromotionsExtensionPoint extends ContextExtensionPoint {

    @DslMethod(context = PropertiesContext.class)
    public Object promotions(Runnable closure) {
        //System.out.println("promotion: " + groovy.json.JsonOutput.toJson(closure));
        PromotionsContextHelper contextHelper = new PromotionsContextHelper(new ArrayList<WithXmlAction>(), null);
        List<String> names = contextHelper.promotions((Closure) closure);
        DslSession.getCurrentSession().setData("helper", contextHelper);
        DslSession.getCurrentSession().setData("names", names);
        return contextHelper.getXml();
    }

    @Override
    public void notifyItemCreated(Item item) {
    	System.out.println("item created: " + item);
        PromotionsContextHelper contextHelper = (PromotionsContextHelper) DslSession.getCurrentSession().getData("helper");
        List<String> names = (List<String>) DslSession.getCurrentSession().getData("names");
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
    public void notifyItemUpdated(Item item) {
        System.out.println("item updated: " + item);
        // new File(configId.getType().toString() +
        // configId.getRelativePath().replace("/", "_") + jobName +
        // ext).write(config.getConfig(configId));
    }

}
