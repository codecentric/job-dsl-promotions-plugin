package org.jenkinsci.plugins.jobdsl.promotions

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.step.StepContext

class PromotionContext implements Context {

    private JobManagement jobManagement

    private Job job

    private List<ConditionsContext> conditions = []

    private List<Node> actions =  []

    private String icon

    private String restrict

    private String name

    PromotionContext(JobManagement jobManagement, Job job) {
        this.jobManagement = jobManagement
        this.job = job
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
        // delegate to ConditionsContext
        StepContext actionsContext = new StepContext(jobManagement, job)
        ContextHelper.executeInContext(actionsClosure, actionsContext)
        actionsContext.stepNodes.each { actions << it }
    }

}
