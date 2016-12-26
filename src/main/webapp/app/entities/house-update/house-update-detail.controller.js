(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('HouseUpdateDetailController', HouseUpdateDetailController);

    HouseUpdateDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'HouseUpdate', 'Update', 'House'];

    function HouseUpdateDetailController($scope, $rootScope, $stateParams, previousState, entity, HouseUpdate, Update, House) {
        var vm = this;

        vm.houseUpdate = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('smurfHouseApp:houseUpdateUpdate', function(event, result) {
            vm.houseUpdate = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
