(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('update', {
            parent: 'entity',
            url: '/update?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.update.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/update/updates.html',
                    controller: 'UpdateController',
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
                    $translatePartialLoader.addPart('update');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('update-detail', {
            parent: 'entity',
            url: '/update/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.update.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/update/update-detail.html',
                    controller: 'UpdateDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('update');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Update', function($stateParams, Update) {
                    return Update.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'update',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('update-detail.edit', {
            parent: 'update-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/update/update-dialog.html',
                    controller: 'UpdateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Update', function(Update) {
                            return Update.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('update.new', {
            parent: 'update',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/update/update-dialog.html',
                    controller: 'UpdateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                updateDate: null,
                                news: null,
                                deletes: null,
                                priceUpdates: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('update', null, { reload: true });
                }, function() {
                    $state.go('update');
                });
            }]
        })
        .state('update.edit', {
            parent: 'update',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/update/update-dialog.html',
                    controller: 'UpdateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Update', function(Update) {
                            return Update.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('update', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('update.delete', {
            parent: 'update',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/update/update-delete-dialog.html',
                    controller: 'UpdateDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Update', function(Update) {
                            return Update.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('update', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
