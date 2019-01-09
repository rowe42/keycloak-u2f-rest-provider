'use strict';

var module = angular.module('keycloak.loaders');

//Roland Start
module.factory('U2FLoader', function(Loader, U2F, Realm, $route, $q) {
    return Loader.get(U2F, function() {
		return {
			realm : $route.current.params.realm
		}
	});
	/* return Loader.get(U2F, function() {
		return {
			userId : $route.current.params.user
		}
	}); */
});
//Roland Ende
