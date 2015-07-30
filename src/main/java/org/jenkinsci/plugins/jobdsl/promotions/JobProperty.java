package org.jenkinsci.plugins.jobdsl.promotions;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hudson.plugins.promoted_builds.JobPropertyImpl")
public class JobProperty  {
	
	List<String> activeProcessNames = new ArrayList<String>();
	
	//FIXME This is not possible :-( @XStreamAsAttribute
	//private String plugin = "promoted-builds@2.15";
	
	public JobProperty(List<String> activeProcessNames) {
		this.activeProcessNames = activeProcessNames;
	}
	
}
