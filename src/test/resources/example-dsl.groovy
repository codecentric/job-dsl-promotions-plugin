freeStyleJob('test-job') {
	properties{
	  promotions {
			promotion {
			  name('Development')
			  conditions {
				  manual('tester'){
					  parameters{
						  textParam("parameterName","defaultValue","description")
					  }
				  }
			  }
	      actions {
				  shell('echo hello;')
			  }
			}
	  }
	}
}
