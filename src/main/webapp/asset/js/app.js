// Constants
var JEngine_Server_URL = window.location.origin;
var JCore_REST_Interface_Version = "v1";
var JCore_REST_Interface = "JEngine/api/interface/" + JCore_REST_Interface_Version +"/en";
var JComparser_REST_Interface = "JEngine/api/jcomparser";


(function(){
	// Vars defining the URIs of the REST-APIs
    var jcomparser = angular.module('jcomparser', [
		'ngRoute',
		'cenario']);
		
	// Create Routes for the App
	jcomparser.config(['$routeProvider',
		function($routeProvider){
			$routeProvider.
				when('/scenario', {
					templateUrl: 'asset/templates/scenario.html',
					controller: 'ScenarioController',
					controllerAs: 'scenarioCtrl'
				}).
				when('/scenario/:id', {
					templateUrl: 'asset/templates/scenarioDetails.html',
					controller: 'ScenarioController',
					controllerAs: 'scenarioCtrl'
				}).
				when('/scenario/:id/instance', {
					templateUrl: 'asset/templates/scenarioInstance.html',
					controller: 'ScenarioInstanceController',
					controllerAs: 'instanceCtrl'
				}).
				when('/scenario/:id/instance/:instanceId', {
					templateUrl: 'asset/templates/scenarioInstanceDetails.html',
					controller: 'ScenarioInstanceController',
					controllerAs: 'instanceCtrl'
				}).
				otherwise({
					redirectTo: '/scenario'
				});
		}
	]);
})();