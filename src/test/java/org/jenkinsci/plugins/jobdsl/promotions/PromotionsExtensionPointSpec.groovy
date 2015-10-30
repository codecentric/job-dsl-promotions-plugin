package org.jenkinsci.plugins.jobdsl.promotions

import hudson.model.FreeStyleProject
import javaposse.jobdsl.plugin.DslEnvironment
import javaposse.jobdsl.plugin.DslEnvironmentImpl
import jenkins.model.Jenkins
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class PromotionsExtensionPointSpec extends Specification {

    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    PromotionsExtensionPoint extensionPoint = new PromotionsExtensionPoint()

	def 'promotionJobExtension'() {
		when:
        DslEnvironment dslEnvironment = new DslEnvironmentImpl();
		PromotionJobProperty jobProperty = extensionPoint.promotions({
            promotion {
                name('dev')
                icon('star')
            }
            promotion {
                name('dev2')
                icon('star')
            }
		}, dslEnvironment)

		then:
        println jobProperty
		assert jobProperty.activeProcessNames[0] == "dev"
		assert jobProperty.activeProcessNames[1] == "dev2"
	}

    def 'promotionExtraXml'() {
        when:
        DslEnvironment dslEnvironment = new DslEnvironmentImpl();
        PromotionJobProperty jobProperty = extensionPoint.promotions({
            promotion {
                name('dev')
                icon('star')
                conditions {
                    manual('name')
                }
                actions {
                    shell('echo hallo;')
                }
            }
            promotion {
                name('dev2')
                icon('star')
                conditions {
                    manual('name')
                }
                actions {
                    shell('echo adios;')
                }
            }
        }, dslEnvironment)
        FreeStyleProject item = Spy(FreeStyleProject, constructorArgs: [Jenkins.getInstance(), 'freestyle']) {
            1 * doReload() >> {}
        }
        extensionPoint.notifyItemCreated(item, dslEnvironment)

        then:
        println jobProperty
		assert jobProperty.activeProcessNames[0] == "dev"
		assert jobProperty.activeProcessNames[1] == "dev2"

        String xmlDev = FileUtils.readFileToString(new File(item.getRootDir(), '/promotions/dev/config.xml'));
        println xmlDev
        assertXMLEqual('''
<hudson.plugins.promoted__builds.PromotionProcess plugin='promoted-builds@2.15'>
    <actions></actions>
    <keepDependencies>false</keepDependencies>
    <properties></properties>
    <scm class='hudson.scm.NullSCM'></scm>
    <canRoam>false</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers></triggers>
    <concurrentBuild>false</concurrentBuild>
    <conditions>
        <hudson.plugins.promoted__builds.conditions.ManualCondition>
            <users>name</users>
            <parameterDefinitions></parameterDefinitions>
        </hudson.plugins.promoted__builds.conditions.ManualCondition>
    </conditions>
    <icon>star</icon>
    <buildSteps>
        <hudson.tasks.Shell>
            <command>echo hallo;</command>
        </hudson.tasks.Shell>
    </buildSteps>
</hudson.plugins.promoted__builds.PromotionProcess>''', xmlDev)

        String xmlDev2 = FileUtils.readFileToString(new File(item.getRootDir(), '/promotions/dev2/config.xml'));
        println xmlDev2
        assertXMLEqual('''
<hudson.plugins.promoted__builds.PromotionProcess plugin='promoted-builds@2.15'>
    <actions></actions>
    <keepDependencies>false</keepDependencies>
    <properties></properties>
    <scm class='hudson.scm.NullSCM'></scm>
    <canRoam>false</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers></triggers>
    <concurrentBuild>false</concurrentBuild>
    <conditions>
        <hudson.plugins.promoted__builds.conditions.ManualCondition>
            <users>name</users>
            <parameterDefinitions></parameterDefinitions>
        </hudson.plugins.promoted__builds.conditions.ManualCondition>
    </conditions>
    <icon>star</icon>
    <buildSteps>
        <hudson.tasks.Shell>
            <command>echo adios;</command>
        </hudson.tasks.Shell>
    </buildSteps>
</hudson.plugins.promoted__builds.PromotionProcess>''', xmlDev2)
    }
}
