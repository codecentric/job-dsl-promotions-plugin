package hudson.plugins.promoted__builds;

import hudson.Extension;
import hudson.PluginWrapper;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Job;

import java.util.ArrayList;
import java.util.List;

import jenkins.model.Jenkins;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hudson.plugins.promoted_builds.JobPropertyImpl")
public class JobPropertyImpl extends JobProperty {
	
	List<String> activeProcessNames = new ArrayList<String>();
	
	public JobPropertyImpl(List<String> activeProcessNames) {
		this.activeProcessNames = activeProcessNames;
	}
	
	@Override
	public JobPropertyDescriptor getDescriptor() {
		return super.getDescriptor();
	}

	@Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
        
		public String getDisplayName() { return "test"; }
        
        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
        	return true;
        }
               
        @Override
        protected PluginWrapper getPlugin() {
        	return Jenkins.getInstance().getPluginManager().whichPlugin(clazz);
        }
    }


}
