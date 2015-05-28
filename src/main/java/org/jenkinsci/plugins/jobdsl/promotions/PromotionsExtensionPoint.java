package org.jenkinsci.plugins.jobdsl.promotions;

import groovy.lang.Closure;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Items;
import hudson.util.IOUtils;
import javaposse.jobdsl.dsl.FileJobManagement;
import javaposse.jobdsl.dsl.WithXmlAction;
import javaposse.jobdsl.dsl.helpers.properties.PropertiesContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslEnvironment;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import javaposse.jobdsl.plugin.JenkinsJobManagement;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Extension
public class PromotionsExtensionPoint extends ContextExtensionPoint {

    @DslExtensionMethod(context = PropertiesContext.class)
    public Object promotions(Runnable closure, DslEnvironment dslEnvironment) {
        PromotionsContextHelper contextHelper = new PromotionsContextHelper();
        List<String> names = contextHelper.promotions((Closure) closure);
        dslEnvironment.put("helper", contextHelper);
        dslEnvironment.put("names", names);
        return contextHelper.getXml();
    }

    @Override
    public void notifyItemCreated(Item item, DslEnvironment dslEnvironment) {
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
                // System.out.println(FileUtils.readFileToString(configXml));
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
