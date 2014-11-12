package org.jenkinsci.plugins.jobdsl.promotions

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.helpers.step.StepContext

class PromotionContext implements Context {

    private JobManagement jobManagement

    private List<ConditionsContext> conditions = []

    private List<Node> actions =  []

    private String icon

    private String restrict

    private String name

    PromotionContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
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
        AbstractContextHelper.executeInContext(conditionClosure, conditionContext)
        conditions << conditionContext
    }

    def actions(Closure actionsClosure) {
        // delegate to ConditionsContext
        StepContext actionsContext = new StepContext(jobManagement)
        AbstractContextHelper.executeInContext(actionsClosure, actionsContext)
        actionsContext.stepNodes.each { actions << it }
    }

}
