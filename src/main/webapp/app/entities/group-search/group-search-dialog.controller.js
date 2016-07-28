(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('GroupSearchDialogController', GroupSearchDialogController);

    GroupSearchDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'GroupSearch', 'House'];

    function GroupSearchDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, GroupSearch, House) {
        var vm = this;

        vm.groupSearch = entity;
        vm.clear = clear;
        vm.save = save;
        vm.houses = House.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.groupSearch.id !== null) {
                GroupSearch.update(vm.groupSearch, onSaveSuccess, onSaveError);
            } else {
                GroupSearch.save(vm.groupSearch, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('smurfHouseApp:groupSearchUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
