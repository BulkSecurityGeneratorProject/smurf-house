(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('PriceHouseDetailController', PriceHouseDetailController);

    PriceHouseDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'PriceHouse', 'House'];

    function PriceHouseDetailController($scope, $rootScope, $stateParams, previousState, entity, PriceHouse, House) {
        var vm = this;

        vm.priceHouse = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('smurfHouseApp:priceHouseUpdate', function(event, result) {
            vm.priceHouse = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
