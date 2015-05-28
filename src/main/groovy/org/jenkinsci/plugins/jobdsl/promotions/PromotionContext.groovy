package org.jenkinsci.plugins.jobdsl.promotions

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.FileJobManagement
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.StepContext
import org.apache.commons.io.FileUtils

class PromotionContext implements Context {

    private List<ConditionsContext> conditions = []

    private List<Node> actions =  []

    private String icon

    private String restrict

    private String name

    PromotionContext() {
    }

    def name(String name) {
       this.name = name
    }

    def icon(String icon) {
        this.icon = icon
    }

    def restrict(String restrict) {
        this.restrict = restrict
    }

    def conditions(Closure conditionClosure) {
        // delegate to ConditionsContext
        ConditionsContext conditionContext = new ConditionsContext()
        ContextHelper.executeInContext(conditionClosure, conditionContext)
        conditions << conditionContext
    }

    def actions(Closure actionsClosure) {
        // delegate to StepContext
        StepContext stepContext = new StepContext(new FileJobManagement(FileUtils.getTempDirectory()), null)
        ContextHelper.executeInContext(actionsClosure, stepContext)
        stepContext.stepNodes.each { actions << it }
    }

}
