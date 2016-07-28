(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('PriceHouseDialogController', PriceHouseDialogController);

    PriceHouseDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PriceHouse', 'House'];

    function PriceHouseDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PriceHouse, House) {
        var vm = this;

        vm.priceHouse = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
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
            if (vm.priceHouse.id !== null) {
                PriceHouse.update(vm.priceHouse, onSaveSuccess, onSaveError);
            } else {
                PriceHouse.save(vm.priceHouse, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('smurfHouseApp:priceHouseUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.whenChanged = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
