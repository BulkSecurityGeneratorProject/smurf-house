(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('house-update', {
            parent: 'entity',
            url: '/house-update?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.houseUpdate.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/house-update/house-updates.html',
                    controller: 'HouseUpdateController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('houseUpdate');
                    $translatePartialLoader.addPart('houseUpdateOperation');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('house-update-detail', {
            parent: 'entity',
            url: '/house-update/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.houseUpdate.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/house-update/house-update-detail.html',
                    controller: 'HouseUpdateDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('houseUpdate');
                    $translatePartialLoader.addPart('houseUpdateOperation');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'HouseUpdate', function($stateParams, HouseUpdate) {
                    return HouseUpdate.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'house-update',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('house-update-detail.edit', {
            parent: 'house-update-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/house-update/house-update-dialog.html',
                    controller: 'HouseUpdateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['HouseUpdate', function(HouseUpdate) {
                            return HouseUpdate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('house-update.new', {
            parent: 'house-update',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/house-update/house-update-dialog.html',
                    controller: 'HouseUpdateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                operation: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('house-update', null, { reload: true });
                }, function() {
                    $state.go('house-update');
                });
            }]
        })
        .state('house-update.edit', {
            parent: 'house-update',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/house-update/house-update-dialog.html',
                    controller: 'HouseUpdateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['HouseUpdate', function(HouseUpdate) {
                            return HouseUpdate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('house-update', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('house-update.delete', {
            parent: 'house-update',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/house-update/house-update-delete-dialog.html',
                    controller: 'HouseUpdateDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['HouseUpdate', function(HouseUpdate) {
                            return HouseUpdate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('house-update', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
