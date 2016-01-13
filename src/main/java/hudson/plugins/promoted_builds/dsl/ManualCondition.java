package hudson.plugins.promoted_builds.dsl;

import groovy.util.Node;
import hudson.plugins.promoted_builds.PromotionCondition;

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hudson.plugins.promoted_builds.conditions.ManualCondition")
// TODO Replace with original impl
public class ManualCondition extends PromotionCondition {

	private String users;
	
	private Collection<Node> parameterDefinitions;

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public Collection<Node> getParameterDefinitions() {
		return parameterDefinitions;
	}

	public void setParameterDefinitions(Collection<Node> parameterDefinitions) {
		this.parameterDefinitions = parameterDefinitions;
	}

}
