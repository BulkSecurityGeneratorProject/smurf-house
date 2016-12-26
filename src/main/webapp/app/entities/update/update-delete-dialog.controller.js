(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('UpdateDeleteController',UpdateDeleteController);

    UpdateDeleteController.$inject = ['$uibModalInstance', 'entity', 'Update'];

    function UpdateDeleteController($uibModalInstance, entity, Update) {
        var vm = this;

        vm.update = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Update.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
