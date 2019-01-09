
//Roland Start
module.controller('U2FCtrl', function($scope, Current, Realm, U2F, realm, serverInfo, u2f, $http, $location, $window, Dialog, Notifications, Auth) {
    // $scope.createRealm = !realm.realm;
    $scope.serverInfo = serverInfo;
    $scope.realmName = realm.realm;
    // $scope.disableRename = realm.realm == masterRealm;

    $scope.u2f = u2f;

    // if (Current.realm == null || Current.realm.realm != realm.realm) {
    //     for (var i = 0; i < Current.realms.length; i++) {
    //         if (realm.realm == Current.realms[i].realm) {
    //             Current.realm = Current.realms[i];
    //             break;
    //         }
    //     }
    // }
    // for (var i = 0; i < Current.realms.length; i++) {
    //     if (Current.realms[i].realm == realm.realm) {
    //         Current.realm = Current.realms[i];
    //     }
    // }
    $scope.realm = angular.copy(realm);

    // var oldCopy = angular.copy($scope.realm);

    // $scope.changed = $scope.create;

    // $scope.$watch('realm', function() {
    //     if (!angular.equals($scope.realm, oldCopy)) {
    //         $scope.changed = true;
    //     }
    // }, true);
    // $scope.$watch('realmName', function() {
    //     if (!angular.equals($scope.realmName, oldCopy.realm)) {
    //         $scope.changed = true;
    //     }
    // }, true);

    // $scope.save = function() {
    //     var realmCopy = angular.copy($scope.realm);
    //     realmCopy.realm = $scope.realmName;
    //     $scope.changed = false;
    //     var nameChanged = !angular.equals($scope.realmName, oldCopy.realm);
    //     var oldName = oldCopy.realm;
    //     Realm.update({ id : oldCopy.realm}, realmCopy, function () {
    //         var data = Realm.query(function () {
    //             Current.realms = data;
    //             for (var i = 0; i < Current.realms.length; i++) {
    //                 if (Current.realms[i].realm == realmCopy.realm) {
    //                     Current.realm = Current.realms[i];
    //                     oldCopy = angular.copy($scope.realm);
    //                 }
    //             }
    //         });

    //         if (nameChanged) {
    //             console.debug(Auth);
    //             console.debug(Auth.authz.tokenParsed.iss);

    //             if (Auth.authz.tokenParsed.iss.endsWith(masterRealm)) {
    //                 Auth.refreshPermissions(function () {
    //                     Auth.refreshPermissions(function () {
    //                         Notifications.success("Your changes have been saved to the realm.");
    //                         $scope.$apply(function () {
    //                             $location.url("/realms/" + realmCopy.realm);
    //                         });
    //                     });
    //                 });
    //             } else {
    //                 delete Auth.authz.token;
    //                 delete Auth.authz.refreshToken;

    //                 var newLocation = $window.location.href.replace('/' + oldName + '/', '/' + realmCopy.realm + '/')
    //                     .replace('/realms/' + oldName, '/realms/' + realmCopy.realm);
    //                 window.location.replace(newLocation);
    //             }
    //         } else {
    //             $location.url("/realms/" + realmCopy.realm);
    //             Notifications.success("Your changes have been saved to the realm.");
    //         }
    //     });
    // };

    // $scope.reset = function() {
    //     $scope.realm = angular.copy(oldCopy);
    //     $scope.changed = false;
    // };

    // $scope.cancel = function() {
    //     window.history.back();
    // };

    $scope.searchQuery = function(user) {
        console.log("search called " + user);

        // U2F.query({realm: realm.realm, user: 'abc'}, function(data) {
        //     console.log("search executed " + data);            
        //     $scope.u2f = data;
        // }, function(e) { console.log('error ' + e)});

        U2F.get({realm: realm.realm, username: user}, function(data) {
            console.log("search executed " + data);            
            $scope.u2f = data;
        }, function(e) { console.log('error ' + e)});
    };
});

//Roland Ende
