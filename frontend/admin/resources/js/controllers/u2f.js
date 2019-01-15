
module.controller('U2FCtrl', function($scope, Current, Realm, U2F, U2FRemove, realm, user, u2f, $http, $location, $window, Dialog, Notifications, Auth) {
    $scope.realmName = realm.realm;
    $scope.user = user;

    $scope.u2f = u2f;

    $scope.realm = angular.copy(realm);


    $scope.remove = function(user) {
        console.log("remove called " + user);

        U2FRemove.delete({realm: realm.realm, user: $scope.user.id, credentialid : $scope.u2f[0].credentialId}, function(data) {
            console.log("remove executed " + data);            
            Notifications.success("The U2F has been removed.");
            U2F.query({realm: realm.realm, user: $scope.user.id}, function(updated) {
                $scope.u2f = updated;
            }, function(e) { console.log('error ' + e)})
        }, function(e) { console.log('error ' + e)});
    };

});

