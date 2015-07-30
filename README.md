# Example DSL to generate Promotions

```groovy
job{
	name('promotion-job')
	properties{
		promotions{
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
		}
	}
}
```