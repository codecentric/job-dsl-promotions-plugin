# Example DSL to generate Promotions

## Simple example

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

## More complex example

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

