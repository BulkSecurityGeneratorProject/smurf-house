(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('HouseUpdateDeleteController',HouseUpdateDeleteController);

    HouseUpdateDeleteController.$inject = ['$uibModalInstance', 'entity', 'HouseUpdate'];

    function HouseUpdateDeleteController($uibModalInstance, entity, HouseUpdate) {
        var vm = this;

        vm.houseUpdate = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            HouseUpdate.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
