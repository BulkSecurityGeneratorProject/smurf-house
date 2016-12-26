(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('UpdateDialogController', UpdateDialogController);

    UpdateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Update', 'GroupSearch', 'HouseUpdate'];

    function UpdateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Update, GroupSearch, HouseUpdate) {
        var vm = this;

        vm.update = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.groupsearches = GroupSearch.query({filter: 'update-is-null'});
        $q.all([vm.update.$promise, vm.groupsearches.$promise]).then(function() {
            if (!vm.update.groupSearch || !vm.update.groupSearch.id) {
                return $q.reject();
            }
            return GroupSearch.get({id : vm.update.groupSearch.id}).$promise;
        }).then(function(groupSearch) {
            vm.groupsearches.push(groupSearch);
        });
        vm.houseupdates = HouseUpdate.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.update.id !== null) {
                Update.update(vm.update, onSaveSuccess, onSaveError);
            } else {
                Update.save(vm.update, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('smurfHouseApp:updateUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.updateDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
