# Example DSL to generate Promotions

```groovy
job{
	name('promotion-job')
	properties{
		promotions{
			promotion {
			    name('dev')
			    icon('star')
			}
			promotion {
			    name('dev2')
			    icon('star')
			}
		}
	}
}
```