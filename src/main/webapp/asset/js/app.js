// Constants
var JEngine_Server_URL = window.location.origin;
var JCore_REST_Interface_Version = "v1";
var JCore_REST_Interface = "JEngine/api/interface/" + JCore_REST_Interface_Version +"/en";
var JComparser_REST_Interface = "JEngine/api/jcomparser";


(function(){
	// Vars defining the URIs of the REST-APIs
    var jfrontend = angular.module('jfrontend', [
		'ngRoute',
		'Scenario']);
		
	// Create Routes for the App
	jfrontend.config(['$routeProvider',
		function($routeProvider){
			$routeProvider.
			// Routes for jCore
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
			// Routes for Admin Dashbaord
				when('/admin/jcomparser/', {
					templateUrl: 'asset/templates/jcomparser.html',
					controller: 'jcomparserMainView',
					controllerAs: 'jcomparserMV'
				}).
				when('/admin/mail/', {
					templateUrl: 'asset/templates/mailConfigDetails.html',
					controller: 'mailConfig',
					controllerAs: 'mailC'
				}).
			// default Route
				otherwise({
					redirectTo: '/scenario'
				});
		}
	]);
})();