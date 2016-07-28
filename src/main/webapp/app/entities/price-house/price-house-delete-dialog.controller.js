(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('PriceHouseDeleteController',PriceHouseDeleteController);

    PriceHouseDeleteController.$inject = ['$uibModalInstance', 'entity', 'PriceHouse'];

    function PriceHouseDeleteController($uibModalInstance, entity, PriceHouse) {
        var vm = this;

        vm.priceHouse = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            PriceHouse.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
