acme = {}
acme.myapp = {}

acme.myapp.ConstructionController = function($location, $scope) {
  this.location_ = $location;
  this.scope_ = $scope;
};

acme.myapp.ConstructionController.prototype.begin = function() {
  alert('You broke it!');
  this.location_.path('/email');
};



acme.myapp.EmailController = function($resource, $scope) {
  this.resource_ = $resource;
  this.scope_ = $scope;
};

acme.myapp.EmailController.prototype.send = function() {
  // TODO:validate email.

  var onSave = function(result, status, headers, config) {
    alert('sent'); 
  };

  // TODO: Add proper error handling.
  var onError = function(data, status, headers, config) {
    console.log(data);
    console.log(status);
    alert('omg');
  };

  console.log(this.email);

  this.resource_('/myapp/rest/email').save(
      null,       // Query params.
      this.email, // POST params. Email is hydrated from view.
      onSave,
      onError); 
};

acme.myapp.EmailController.prototype.spellCheck = function() {
  alert('spellCheck');
};



acme.myapp.Module = angular.module('myapp', ['ngRoute', 'ngResource']);

acme.myapp.Module.configure = function($routeProvider, $locationProvider) {
  $routeProvider
      .when('/', {
        templateUrl: '/myapp/views/construction.html',
        controller: acme.myapp.ConstructionController,
        controllerAs: 'ctrl'
      })
      .when('/email', {
        templateUrl: '/myapp/views/email.html',
        controller: acme.myapp.EmailController,
        controllerAs: 'ctrl'
      })
      .otherwise({ redirectTo: '/' });
};

acme.myapp.Module.config(acme.myapp.Module.configure);
