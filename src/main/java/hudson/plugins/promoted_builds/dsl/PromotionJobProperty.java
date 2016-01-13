package hudson.plugins.promoted_builds.dsl;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.AbstractProject;
import hudson.model.Job;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hudson.plugins.promoted_builds.JobPropertyImpl")
// TODO Replace with original JobPropertyImpl from Promoted Builds Plugin
public class PromotionJobProperty extends hudson.model.JobProperty<Job<?, ?>> {

	private List<String> activeProcessNames = new ArrayList<String>();

	public PromotionJobProperty(List<String> activeProcessNames) {
		this.activeProcessNames = activeProcessNames;
	}

	@Extension
	public static final class DescriptorImpl extends JobPropertyDescriptor {

		public DescriptorImpl() {
			super();
		}

		public DescriptorImpl(Class<? extends JobProperty<?>> clazz) {
			super(clazz);
		}

		public String getDisplayName() {
			return "Promote Builds When...";
		}

		@Override
		public boolean isApplicable(Class<? extends Job> jobType) {
			return AbstractProject.class.isAssignableFrom(jobType);
		}

	}

	public List<String> getActiveProcessNames() {
		return activeProcessNames;
	}
}
