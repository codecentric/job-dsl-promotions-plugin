package org.jenkinsci.plugins.jobdsl.promotions;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hudson.plugins.promoted_builds.JobPropertyImpl")
public class JobProperty  {
	
	List<String> activeProcessNames = new ArrayList<String>();
	
	public JobProperty(List<String> activeProcessNames) {
		this.activeProcessNames = activeProcessNames;
	}
	
}
