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
        vm.refreshSync = refreshSync;

            console.log('1');

        function refreshSync (id) {
            console.log('refresh sync for ', id);

            GroupSearch.sync({id:id}, {}, function(result, headers){
                console.log('result: ', result);
                console.log('headers: ', headers);

                //vm.audits = result;
                //vm.links = ParseLinks.parse(headers('link'));
                //vm.totalItems = headers('X-Total-Count');
            });

        }


        function clear () {
            console.log('3');
            $uibModalInstance.dismiss('cancel');
        }

        var unsubscribe = $rootScope.$on('smurfHouseApp:groupSearchUpdate', function(event, result) {
            vm.groupSearch = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
