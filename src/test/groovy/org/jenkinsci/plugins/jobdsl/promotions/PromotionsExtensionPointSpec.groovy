package org.jenkinsci.plugins.jobdsl.promotions

import hudson.model.FreeStyleProject
import javaposse.jobdsl.plugin.DslEnvironment
import javaposse.jobdsl.plugin.DslEnvironmentImpl
import jenkins.model.Jenkins
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
		String xml = extensionPoint.promotions({
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
        println xml
		assertXMLEqual('''
<hudson.plugins.promoted__builds.JobPropertyImpl>
    <activeProcessNames>
        <string>dev</string>
        <string>dev2</string>
    </activeProcessNames>
</hudson.plugins.promoted__builds.JobPropertyImpl>''', xml)
	}

    def 'promotionExtraXml'() {
        when:
        DslEnvironment dslEnvironment = new DslEnvironmentImpl();
        extensionPoint.promotions({
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
        FreeStyleProject item = new FreeStyleProject(Jenkins.getInstance(), 'freestyle')
        extensionPoint.notifyItemCreated(item, dslEnvironment)

        then:
        true
    }
}
