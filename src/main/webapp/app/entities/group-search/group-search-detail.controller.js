(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('GroupSearchDetailController', GroupSearchDetailController);

    GroupSearchDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'GroupSearch', 'House'];

    function GroupSearchDetailController($scope, $rootScope, $stateParams, previousState, entity, GroupSearch, House) {
        var vm = this;

        vm.groupSearch = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('smurfHouseApp:groupSearchUpdate', function(event, result) {
            vm.groupSearch = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
