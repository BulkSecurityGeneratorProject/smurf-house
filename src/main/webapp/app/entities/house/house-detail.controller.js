(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('HouseDetailController', HouseDetailController);

    HouseDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'House', 'PriceHouse', 'GroupSearch'];

    function HouseDetailController($scope, $rootScope, $stateParams, previousState, entity, House, PriceHouse, GroupSearch) {
        var vm = this;

        vm.house = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('smurfHouseApp:houseUpdate', function(event, result) {
            vm.house = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
