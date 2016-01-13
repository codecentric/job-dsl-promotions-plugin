package org.jenkinsci.plugins.jobdsl.promotions;

import groovy.lang.Closure;
import hudson.plugins.promoted_builds.dsl.PromotionProcess;

import java.util.List;
import java.util.Map;

public class PromotionsContextHelper {

	private Map<String, PromotionProcess> promotionProcesses;

	public List<String> promotions(Closure<?> closure) {
		PromotionsContext context = new PromotionsContext();
		executeInContext(closure, context);
		promotionProcesses = context.promotionProcesses;
		return context.names;
	}

	public PromotionProcess getPromotionProcess(String name) {
		if (name != null) {
			return promotionProcesses.get(name);
		}
		return null;
	}

	private static void executeInContext(Closure<?> configClosure, Object context) {
		configClosure.setDelegate(context);
		configClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
		configClosure.call();
	}

}
