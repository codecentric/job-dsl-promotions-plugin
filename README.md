# Example DSL to generate Promotions

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