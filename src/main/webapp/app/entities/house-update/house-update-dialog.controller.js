(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('HouseUpdateDialogController', HouseUpdateDialogController);

    HouseUpdateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'HouseUpdate', 'Update', 'House'];

    function HouseUpdateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, HouseUpdate, Update, House) {
        var vm = this;

        vm.houseUpdate = entity;
        vm.clear = clear;
        vm.save = save;
        vm.updates = Update.query();
        vm.houses = House.query({filter: 'houseupdate-is-null'});
        $q.all([vm.houseUpdate.$promise, vm.houses.$promise]).then(function() {
            if (!vm.houseUpdate.house || !vm.houseUpdate.house.id) {
                return $q.reject();
            }
            return House.get({id : vm.houseUpdate.house.id}).$promise;
        }).then(function(house) {
            vm.houses.push(house);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.houseUpdate.id !== null) {
                HouseUpdate.update(vm.houseUpdate, onSaveSuccess, onSaveError);
            } else {
                HouseUpdate.save(vm.houseUpdate, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('smurfHouseApp:houseUpdateUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
