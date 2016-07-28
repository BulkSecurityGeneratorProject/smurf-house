(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('HouseDeleteController',HouseDeleteController);

    HouseDeleteController.$inject = ['$uibModalInstance', 'entity', 'House'];

    function HouseDeleteController($uibModalInstance, entity, House) {
        var vm = this;

        vm.house = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            House.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
