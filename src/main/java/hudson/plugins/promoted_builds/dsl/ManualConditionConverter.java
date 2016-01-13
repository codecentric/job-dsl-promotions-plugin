package hudson.plugins.promoted_builds.dsl;

import groovy.util.Node;

import java.util.Collection;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ManualConditionConverter implements Converter {

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		return type.equals(ManualCondition.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		ManualCondition mc = (ManualCondition) source;
		if (mc.getUsers() != null) {
			writer.startNode("users");
			writer.setValue(mc.getUsers());
			writer.endNode();
		}
		writer.startNode("parameterDefinitions");
		if(mc.getParameterDefinitions() != null){			
			for (Node node : mc.getParameterDefinitions()) {
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
		}
		writer.endNode();
	}

	private void convertNode(Node node, HierarchicalStreamWriter writer) {
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

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		throw new UnsupportedOperationException("The PromotionProcessConverter supports only marshalling!");
	}

}
