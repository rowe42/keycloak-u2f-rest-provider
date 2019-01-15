'use strict';

var module = angular.module('keycloak.services');

module.factory('U2F', function($resource) {
    return $resource(authUrl + '/realms/:realm/users/:user/u2f', {
        realm : '@realm',
        user : '@user'
    });
});

module.factory('U2FRemove', function($resource) {
    return $resource(authUrl + '/realms/:realm/users/:user/u2f/:credentialid', {
        realm : '@realm',
        user : '@user',
        credentialid : '@credentialid'
    });
});
