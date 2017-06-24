({
	paths : {
		jquery:     'libs/jquery',
	    underscore: 'libs/underscore',
	    backbone:   'libs/backbone',
	    backgrid :         'libs/backgrid',
	    localizedmessages : 'libs/localizedmessages',
	    'backbone-pageable': 'libs/backbone-pageable',
	    'backgrid-paginator': 'libs/backgrid-paginator',
	    'bootstrap.min': 'libs/bootstrap.min',
	    'bootstrap-datepicker': 'libs/bootstrap-datepicker',
	    'backgrid-select-all' : 'libs/backgrid-select-all',
	    'backbone.global' : 'libs/backbone.global',
	    'respondjs' : 'libs/respond.min',
	    'html5shiv' : 'libs/html5shiv.min',
	    'bootstrap-wizard' : 'libs/jquery.bootstrap.wizard',
	    templates:  'templates'
	},
	shim : {
		'bootstrap.min' : {
			deps: ["respondjs", "html5shiv"]
		},  
		underscore: {
		   exports: '_'
		},
	    backbone: {
	      deps: ["underscore", "jquery"],
	      exports: "Backbone"
	    },	    
	    'bootstrap-wizard' :{
			deps : [ "underscore", "jquery", "backbone" ],
		},
		backgrid: {
		    exports: "Backgrid",
		    deps: ['underscore', 'backbone', 'backbone-pageable'],
		    init: function(_, Backbone, PageableCollection) {
		        this.Backbone.PageableCollection = PageableCollection;
		    }
		},
		'backgrid-paginator' : {
		  	deps: ['underscore', 'backbone', 'backgrid']
		},
		'backgrid-select-all' : {
		  	deps: ['underscore', 'backbone', 'backgrid']
		}
	},

    //baseUrl : "js",
    //name: "main",
    //out: "dist/main.js",
    removeCombined: true,
    findNestedDependencies: true,
    dir: "optimized-resources",
    modules: [
              {
                  name: "main",
                  exclude: [                      
                      "localizedmessages"
                  ]
              }
          ]
})