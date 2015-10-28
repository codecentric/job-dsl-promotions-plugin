package org.jenkinsci.plugins.jobdsl.promotions;
import groovy.lang.Closure;
import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.Item;
import hudson.model.Items;
import hudson.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javaposse.jobdsl.dsl.helpers.properties.PropertiesContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslEnvironment;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension
public class PromotionsExtensionPoint extends ContextExtensionPoint {

        private static final Logger LOGGER = Logger.getLogger(PromotionsExtensionPoint.class.getName());

        @DslExtensionMethod(context = PropertiesContext.class)
        public Object promotions(Runnable closure, DslEnvironment dslEnvironment) {
                PromotionsContextHelper contextHelper = new PromotionsContextHelper();
                @SuppressWarnings("rawtypes")
                List<String> activeProcessNames = contextHelper.promotions((Closure) closure);
                dslEnvironment.put("helper", contextHelper);
                dslEnvironment.put("names", activeProcessNames);
                PromotionJobProperty jobProperty = new PromotionJobProperty(activeProcessNames);
                return jobProperty;
        }

        @Override
        public void notifyItemCreated(Item item, DslEnvironment dslEnvironment) {
            notifyItemCreated(item, dslEnvironment, false);
        }

        public void notifyItemCreated(Item item, DslEnvironment dslEnvironment, boolean update) {
                LOGGER.log(Level.INFO, String.format("Creating promotions for %s", item.getName()));
                PromotionsContextHelper contextHelper = (PromotionsContextHelper) dslEnvironment.get("helper");
                @SuppressWarnings("unchecked")
                List<String> names = (List<String>) dslEnvironment.get("names");
                if (names != null && names.size() > 0) {
                    for (String name : names) {
                        String xml = contextHelper.getSubXml(name);
                        File dir = new File(item.getRootDir(), "promotions/" + name);
                        File configXml = Items.getConfigFile(dir).getFile();
                        configXml.getParentFile().mkdirs();
                        try {
                                InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
                                IOUtils.copy(in, configXml);
                                LOGGER.log(Level.INFO, String.format("Added promotion with name %s for %s", name, item.getName()));
                                update = true;
                        } catch (UnsupportedEncodingException e) {
                                throw new IllegalStateException("Error handling extension code", e);
                        } catch (IOException e) {
                                throw new IllegalStateException("Error handling extension code", e);
                        }
                    }
                }

                // Only update if a promotion was actually added, updated, or removed.
                if(update) {
                    try {
                        ((AbstractItem) item).doReload();
                    } catch(Exception e) {
                        throw new IllegalStateException("Unable to cast item to AbstractItem and reload config", e);
                    }
                }
        }

        @Override
        public void notifyItemUpdated(Item item, DslEnvironment dslEnvironment) {
                LOGGER.log(Level.INFO, String.format("Updating promotions for %s", item.getName()));
                @SuppressWarnings("unchecked")
                List<String> newPromotions = (List<String>) dslEnvironment.get("names");
                File dir = new File(item.getRootDir(), "promotions/");
                boolean update = false;
                //Delete removed promotions
                if (newPromotions != null && dir != null){
                    File[] files = dir.listFiles();
                    if (files != null) {
                        for (File promotion : files) {
                            if (!newPromotions.contains(promotion.getName())){
                                promotion.delete();
                                LOGGER.log(Level.INFO, String.format("Deleted promotion with name %s for %s", promotion.getName(), item.getName()));
                                update = true;
                            }
                        }
                    }
                }

                //Delegate to create-method
                this.notifyItemCreated(item, dslEnvironment, update);
        }

}
