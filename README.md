# Promototed Builds Plugin Extension for the Jenkins Job DSL Plugin

This plugin is an extension for the existing [Job DSL Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin). With this extension it is possible to generate promotions with the Job DSL. See the [Promoted Builds Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Promoted+Builds+Plugin) for further informations about the meaning of Promotions for a Jenkins Job.

## Build

mvn clean install

## Run

mvn hpi:run

## Usage

### Simple example

```groovy
job{
	name('promotion-job')
	properties{
		promotions{
			promotion {
                name('dev')
                icon('star')
                conditions {
                    manual('developer')
                }
                actions {
                    shell('echo hallo;')
                }
            }
		}
	}
}
```

### More complex example

```groovy
job{
	name('complex-promotion-job')
	properties{
		promotions{
			promotion {
			    name('prod')
				icon('star-green')
				conditions {
					manual('changemanager')
				}
				actions {
					downstreamParameterized {
						trigger("deploy-job","SUCCESS",false,["buildStepFailure": "FAILURE","failure":"FAILURE","unstable":"UNSTABLE"]) {
							predefinedProp("JOB_NAME", "\${PROMOTED_JOB_FULL_NAME}")
							predefinedProp("BUILD_ID","\${PROMOTED_NUMBER}")
						}
					}
					maven {
						mavenInstallation("Maven 3.0.4")
						goals("build-helper:parse-version versions:set versions:commit scm:checkin")
						property("newVersion", "\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT")
						property("connectionUrl", "scm:svn:http:/svn.codecentric.de}/test-project")
						property("message", "Automatic increment version after release")
					}
				}
			}
		}
	}
}
```

