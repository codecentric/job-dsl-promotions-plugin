package org.jenkinsci.plugins.jobdsl.promotions;

import groovy.lang.Closure;
import hudson.plugins.promoted_builds.dsl.PromotionProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javaposse.jobdsl.dsl.Context;

import com.google.common.base.Preconditions;

public class PromotionsContext implements Context {

	List<String> names = new ArrayList<String>();

	Map<String, PromotionProcess> promotionProcesses = new HashMap<String, PromotionProcess>();

	/**
	 * PromotionNodes:
	 * 1. <string>dev</string>
	 * 2. <string>test</string>
	 * 
	 * AND
	 * 
	 * Sub PromotionNode for every promotion
	 * 1. <project>
	 * <name>dev</name>
	 * .
	 * .
	 * .
	 * </project>
	 * 2. <project>
	 * <name>test</name>
	 * .
	 * .
	 * .
	 * </project>
	 * 
	 * @param promotionClosure
	 * @return
	 */
	public void promotion(Closure<?> promotionClosure) {
		PromotionContext promotionContext = new PromotionContext();
		executeInContext(promotionClosure, promotionContext);
		Preconditions.checkNotNull(promotionContext.getName(), "promotion name cannot be null");
		Preconditions.checkArgument(promotionContext.getName().length() > 0);
		names.add(promotionContext.getName());
		// Create Promotion
		PromotionProcess promotionProcess = new PromotionProcess();
		promotionProcess.setName(promotionContext.getName());
		// Icon, i.e. star-green
		if (promotionContext.getIcon() != null) {
			promotionProcess.setIcon(promotionContext.getIcon());
		}
		// Restrict label
		if (promotionContext.getRestrict() != null) {
			promotionProcess.setAssignedLabel(promotionContext.getRestrict());
		}
		promotionProcess.setConditions(promotionContext.getConditions());
		promotionProcess.setBuildSteps(promotionContext.getActions());
		promotionProcesses.put(promotionContext.getName(), promotionProcess);
	}

	private static void executeInContext(Closure<?> configClosure, Object context) {
		configClosure.setDelegate(context);
		configClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
		configClosure.call();
	}

}
