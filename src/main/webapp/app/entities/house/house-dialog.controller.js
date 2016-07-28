(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('HouseDialogController', HouseDialogController);

    HouseDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'House', 'PriceHouse', 'GroupSearch'];

    function HouseDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, House, PriceHouse, GroupSearch) {
        var vm = this;

        vm.house = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.pricehouses = PriceHouse.query();
        vm.groupsearches = GroupSearch.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.house.id !== null) {
                House.update(vm.house, onSaveSuccess, onSaveError);
            } else {
                House.save(vm.house, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('smurfHouseApp:houseUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
