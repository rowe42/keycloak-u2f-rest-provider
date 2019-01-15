'use strict';


var module = angular.module('keycloak');


module.config([ '$routeProvider', function($routeProvider) {
    $routeProvider
        //Custom Start
        .when('/realms/:realm/users/:user/u2f', {
            templateUrl : resourceUrl + '/partials/u2f.html',
            resolve : {
                realm : function(RealmLoader) {
                    return RealmLoader();
                },
                user : function(UserLoader) {
                    return UserLoader();
                },
                u2f: function(U2FLoader) {
                    return U2FLoader();
                }
            },
            controller : 'U2FCtrl'
        })
        //Custom Ende
        .otherwise({
            templateUrl : resourceUrl + '/partials/pagenotfound.html'
        });
} ]);
