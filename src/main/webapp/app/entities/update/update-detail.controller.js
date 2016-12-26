(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('UpdateDetailController', UpdateDetailController);

    UpdateDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Update', 'GroupSearch', 'HouseUpdate'];

    function UpdateDetailController($scope, $rootScope, $stateParams, previousState, entity, Update, GroupSearch, HouseUpdate) {
        var vm = this;

        vm.update = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('smurfHouseApp:updateUpdate', function(event, result) {
            vm.update = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
