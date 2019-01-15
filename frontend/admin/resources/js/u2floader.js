'use strict';

var module = angular.module('keycloak.loaders');

//Roland Start
module.factory('U2FLoader', function(Loader, U2F, Realm, $route, $q) {
    return Loader.query(U2F, function() {
		return {
			realm : $route.current.params.realm,
			user : $route.current.params.user
		}
	});
});

module.factory('U2FRemoveLoader', function(Loader, U2FRemove, Realm, $route, $q) {
    return Loader.query(U2FRemove, function() {
		return {
			realm : $route.current.params.realm,
			user : $route.current.params.user,
			credentialid : $scope.credentialid
		}
	});
});

//Roland Ende


