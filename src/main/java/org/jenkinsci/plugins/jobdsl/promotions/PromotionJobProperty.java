package org.jenkinsci.plugins.jobdsl.promotions;

import hudson.model.Job;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hudson.plugins.promoted_builds.JobPropertyImpl")
public class PromotionJobProperty extends hudson.model.JobProperty<Job<?,?>> {
	
	List<String> activeProcessNames = new ArrayList<String>();
	
	public PromotionJobProperty(List<String> activeProcessNames) {
		this.activeProcessNames = activeProcessNames;
	}
	
}
