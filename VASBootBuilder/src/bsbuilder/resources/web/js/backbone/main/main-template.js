// Require.js allows us to configure shortcut alias (so that we don't always type the full path)
// Their usage will become more apparent futher along in the tutorial.
require.config({
  paths: {
	jquery:     'libs/jquery',
    underscore: 'libs/underscore',
    backbone:   'libs/backbone',
    backgrid :         'libs/backgrid',
    localizedmessages : '/${projectName}/localizedmessages',
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
  shim: {
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
  }

});

require([
  // Load our app module and pass it to our definition function
  'app',

], function(App){
  // The "app" dependency is passed in as "App"
  // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
  App.initialize();
});