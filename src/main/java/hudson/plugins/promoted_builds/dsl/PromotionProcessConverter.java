package hudson.plugins.promoted_builds.dsl;

import groovy.util.Node;
import groovy.util.NodeList;
import hudson.PluginManager;
import hudson.PluginWrapper;

import java.util.Collection;

import jenkins.model.Jenkins;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PromotionProcessConverter implements Converter {

	private String classOwnership;

	private PluginManager pm;

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return type.equals(PromotionProcess.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		PromotionProcess pp = (PromotionProcess) source;
		// attributes
		String plugin = obtainClassOwnership();
		if(plugin != null){			
			writer.addAttribute("plugin", plugin);
		}
		// nodes
		if (pp.getName() != null) {
			writer.startNode("name");
			writer.setValue(pp.getName());
			writer.endNode();
		}
		if (pp.getIcon() != null) {
			writer.startNode("icon");
			writer.setValue(pp.getIcon());
			writer.endNode();
		}
		if (pp.getAssignedLabel() != null) {
			writer.startNode("assignedLabel");
			writer.setValue(pp.getAssignedLabel());
			writer.endNode();
		}
		writer.startNode("conditions");
		context.convertAnother(pp.getConditions());
		writer.endNode();
		writer.startNode("buildSteps");
		for (Node node : pp.getBuildSteps()) {
			writer.startNode(node.name().toString());
			if (node.value() instanceof Collection) {
				for (Object subNode : (Collection) node.value()) {
					convertNode((Node) subNode, writer);
				}
			} else {
				writer.setValue(node.value().toString());
			}
			writer.endNode();
		}
		writer.endNode();
	}

	private void convertNode(Node node, HierarchicalStreamWriter writer) {
		writer.startNode(node.name().toString());
		if (node.value() instanceof NodeList) {
			for (Object subNode : (NodeList) node.value()) {
				convertNode((Node) subNode, writer);
			}
		} else {
			writer.setValue(node.value().toString());
		}
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		throw new UnsupportedOperationException("The PromotionProcessConverter supports only marshalling!");
	}

	private String obtainClassOwnership() {
		if (this.classOwnership != null) {
			return this.classOwnership;
		}
		if (pm == null) {
			Jenkins j = Jenkins.getInstance();
			if (j != null) {
				pm = j.getPluginManager();
			}
		}
		if (pm == null) {
			return null;
		}
		// TODO: possibly recursively scan super class to discover dependencies
		PluginWrapper p = pm.whichPlugin(hudson.plugins.promoted_builds.PromotionProcess.class);
		this.classOwnership = p != null ? p.getShortName() + '@' + trimVersion(p.getVersion()) : null;
		return this.classOwnership;
	}

	static String trimVersion(String version) {
		// TODO seems like there should be some trick with VersionNumber to do this
		return version.replaceFirst(" .+$", "");
	}
}
