'use strict';

var module = angular.module('keycloak.services');


//Roland Start
module.factory('U2F', function($resource) {
    return $resource(authUrl + '/realms/:realm/u2f?username=:username', {
        realm : '@realm',
        username : '@username'
    });
});
//Roland Ende
