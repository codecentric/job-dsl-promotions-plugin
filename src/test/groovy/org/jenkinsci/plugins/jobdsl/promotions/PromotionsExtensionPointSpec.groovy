package org.jenkinsci.plugins.jobdsl.promotions

import hudson.model.FreeStyleProject
import hudson.model.Job
import javaposse.jobdsl.plugin.api.DslSession
import jenkins.model.Jenkins
import org.apache.commons.collections.CollectionUtils

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

import spock.lang.Specification

class PromotionsExtensionPointSpec extends Specification {

    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    PromotionsExtensionPoint extensionPoint = new PromotionsExtensionPoint()

	def 'promotionJobExtension'() {
		when:
		String xml = extensionPoint.promotions({
            promotion {
                name('dev')
                icon('star')
            }
            promotion {
                name('dev2')
                icon('star')
            }
		})

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
        })
        FreeStyleProject item = new FreeStyleProject(Jenkins.getInstance(), 'freestyle')
        extensionPoint.notifyItemCreated(item)

        then:
        true
    }
}
