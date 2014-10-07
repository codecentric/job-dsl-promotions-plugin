package org.jenkinsci.plugins.jobdsl.promotions

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.plugin.api.DslSession

class PromotionsContextHelper extends AbstractContextHelper<PromotionsContext> {

    List<WithXmlAction> subWithXmlActions = []

    PromotionsContextHelper(List<WithXmlAction> withXmlActions, JobType jobType) {
        super(withXmlActions, jobType)
    }

    String promotion(Closure closure) {
        PromotionsContext context = new PromotionsContext()
        execute(closure, context)
        return context.name
    }

    @Override
    Closure generateWithXmlClosure(PromotionsContext context) {
        return { Node property ->
            def promotions = property / 'activeProcessNames'
            promotions << context.promotionNode
        }
    }

    Closure generateSubWithXmlClosures(PromotionsContext context) {
       return { Node project ->
            def promotion = project
            context.subPromotionNode.children().each {
                def name = it.name()
                appendOrReplaceNode(promotion, name, it)
            }
        }
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

    String getSubXml() {
        getNodeXml(subNode)
    }

    Node getSubNode() {
        Node project = executeEmptyTemplate(xmlPromotion)
        executeSubWithXmlActions(project)
        project
    }

    void executeSubWithXmlActions(final Node root) {
        // Create builder, based on what we already have
        subWithXmlActions.each { WithXmlAction withXmlClosure ->
            withXmlClosure.execute(root)
        }
    }

    @Override
    def execute(Closure closure, PromotionsContext freshContext) {
        super.execute(closure, freshContext)

        // Add promotions actions for each promotion in the context
        subWithXmlActions << generateSubWithXmlActions(freshContext)

        return freshContext
    }

    WithXmlAction generateSubWithXmlActions(PromotionsContext context) {
        // Closure to be run later, in this context we're given the root node with the WithXmlAction magic
        Closure withXmlClosure = generateSubWithXmlClosures(context)

        return new WithXmlAction(withXmlClosure)
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

