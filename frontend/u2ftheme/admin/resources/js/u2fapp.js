'use strict';


var module = angular.module('keycloak');


module.config([ '$routeProvider', function($routeProvider) {
    $routeProvider
        //Roland Start
        .when('/realms/:realm/u2f', {
            templateUrl : resourceUrl + '/partials/u2f.html',
            resolve : {
                realm : function(RealmLoader) {
                    return RealmLoader();
                },
                serverInfo : function(ServerInfoLoader) {
                    return ServerInfoLoader();
                },
                u2f: function(U2FLoader) {
                    return U2FLoader();
                }
            },
            controller : 'U2FCtrl'
        })
        //Roland Ende
        .otherwise({
            templateUrl : resourceUrl + '/partials/pagenotfound.html'
        });
} ]);
