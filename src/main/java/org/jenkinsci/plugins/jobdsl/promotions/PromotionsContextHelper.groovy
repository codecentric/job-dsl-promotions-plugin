package org.jenkinsci.plugins.jobdsl.promotions

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction

class PromotionsContextHelper {

    List<WithXmlAction> withXmlActions = []

    List<Map<String, WithXmlAction>> subWithXmlActions = []

    PromotionsContextHelper() {
    }

    List<String> promotions(Closure closure) {
        PromotionsContext context = new PromotionsContext()
        execute(closure, context)
        return context.names
    }

    Closure generateWithXmlClosure(PromotionsContext context) {
        return { Node property ->
            def promotions = property / 'activeProcessNames'
            context.promotionNodes.each {
                promotions << it
            }
        }
    }

    Map<String, Closure> generateSubWithXmlClosures(PromotionsContext context) {
        Map<String, Closure> closureMap = [:]
        context.subPromotionNodes.keySet().each {
            def clos = { Node project ->
                def promotion = project
                context.subPromotionNodes.get(it).children().each {
                     def name = it.name()
                     appendOrReplaceNode(promotion, name, it)
                }
            }
            closureMap.put(it, clos)
        }
        return closureMap
    }

    private void appendOrReplaceNode(Node node, String name, Node replace) {
        node.children().removeAll { it instanceof Node && it.name() == name }
        node.append replace
    }

    Node getNode() {
        Node project = executeEmptyTemplate(xmlProperty)
        executeWithXmlActions(project)
        project
    }

    private executeEmptyTemplate(String xml) {
        new XmlParser().parse(new StringReader(xml))
    }

    void executeWithXmlActions(final Node root) {
        // Create builder, based on what we already have
        withXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }

    String getXml() {
        getNodeXml(node)
    }

    private getNodeXml(Node node) {
        Writer xmlOutput = new StringWriter()
        XmlNodePrinter xmlNodePrinter = new XmlNodePrinter(new PrintWriter(xmlOutput), '    ')
        xmlNodePrinter.with {
            preserveWhitespace = true
            expandEmptyElements = true
            quote = "'" // Use single quote for attributes
        }
        xmlNodePrinter.print(node)

        xmlOutput.toString()
    }

    String getSubXml(String name) {
        getNodeXml(getSubNode(name))
    }

    Node getSubNode(String name) {
        Node project = executeEmptyTemplate(xmlPromotion)
        executeSubWithXmlActions(project, name)
        project
    }

    void executeSubWithXmlActions(final Node root, String name) {
        // Create builder, based on what we already have
        subWithXmlActions.each { Map<String, WithXmlAction> withXmlClosureMap ->
            withXmlClosureMap.get(name).execute(root)
        }
    }

    def execute(Closure closure, PromotionsContext freshContext) {
        // Execute context, which we expect will just establish some state
        ContextHelper.executeInContext(closure, freshContext)

        // Queue up our action, using the concrete classes logic
        withXmlActions << generateWithXmlAction(freshContext)

        // Add promotions actions for each promotion in the context
        subWithXmlActions << generateSubWithXmlActions(freshContext)

        return freshContext
    }

    Map<String, WithXmlAction> generateSubWithXmlActions(PromotionsContext context) {
        // Closure to be run later, in this context we're given the root node with the WithXmlAction magic
        Map<String, Closure> withXmlClosure = generateSubWithXmlClosures(context)

        Map<String, WithXmlAction> map = [:]
        withXmlClosure.keySet().each {
            map.put(it, new WithXmlAction(withXmlClosure.get(it)))
        }
        return map
    }

    WithXmlAction generateWithXmlAction(PromotionsContext context) {
        // Closure to be run later, in this context we're given the root node with the WithXmlAction magic
        Closure withXmlClosure = generateWithXmlClosure(context)

        new WithXmlAction(withXmlClosure)
    }

    def xmlProperty = '''
<hudson.plugins.promoted__builds.JobPropertyImpl>
</hudson.plugins.promoted__builds.JobPropertyImpl>'''

    def xmlPromotion = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.promoted__builds.PromotionProcess plugin="promoted-builds@2.15">
    <actions/>
    <keepDependencies>false</keepDependencies>
    <properties/>
    <scm class="hudson.scm.NullSCM"/>
    <canRoam>false</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers/>
    <concurrentBuild>false</concurrentBuild>
    <conditions/>
    <icon/>
    <buildSteps/>
</hudson.plugins.promoted__builds.PromotionProcess>
'''
}

