(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('GroupSearchDeleteController',GroupSearchDeleteController);

    GroupSearchDeleteController.$inject = ['$uibModalInstance', 'entity', 'GroupSearch'];

    function GroupSearchDeleteController($uibModalInstance, entity, GroupSearch) {
        var vm = this;

        vm.groupSearch = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            GroupSearch.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
