package org.jenkinsci.plugins.jobdsl.promotions

import groovy.lang.Closure
import groovy.util.Node

import java.util.List

import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.common.DownstreamTriggerContext
import javaposse.jobdsl.dsl.helpers.step.AbstractStepContext

import com.google.common.base.Preconditions

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper

class PromotionsContext implements Context {

    Node promotionNode

    Node subPromotionNode

    String name

    /**
     * PromotionNodes:
     * 1. <string>dev</string>
     * 2. <string>test</string>
     * 
     * AND
     * 
     * Sub PromotionNode for every promotion
     * 1. <project>
     *     <name>dev</name>
     *     .
     *     .
     *     .
     * </project>
     * 2. <project>
     *     <name>test</name>
     *     .
     *     .
     *     .
     * </project>
     * 
     * @param promotionClosure
     * @return
     */
    def promotion(Closure promotionClosure = null) {
        PromotionContext promotionContext = new PromotionContext()
        AbstractContextHelper.executeInContext(promotionClosure, promotionContext)

        Preconditions.checkNotNull(promotionContext.name, 'promotion name cannot be null')
        Preconditions.checkArgument(promotionContext.name.length() > 0)

        name = promotionContext.name
        promotionNode = new Node(null, 'string', name)

        subPromotionNode = new NodeBuilder().'project' {
            // Conditions to proof before promotion
            if (promotionContext.conditions) {
                promotionContext.conditions.each {ConditionsContext condition ->
                    conditions(condition.createConditionNode().children())
                }
            }

            // Icon, i.e. star-green
            if (promotionContext.icon) {
                icon(promotionContext.icon)
            }

            // Restrict label
            if (promotionContext.restrict) {
                assignedLabel(promotionContext.restrict)
            }
        }

        // Actions for promotions ... BuildSteps
        def steps = new NodeBuilder().'buildSteps'()
        if (promotionContext.actions) {
            promotionContext.actions.each { steps.append(it) }
        }
        subPromotionNode.append(steps)
    }

}
