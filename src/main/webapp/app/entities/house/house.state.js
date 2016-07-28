(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('house', {
            parent: 'entity',
            url: '/house?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.house.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/house/houses.html',
                    controller: 'HouseController',
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
                    $translatePartialLoader.addPart('house');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('house-detail', {
            parent: 'entity',
            url: '/house/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.house.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/house/house-detail.html',
                    controller: 'HouseDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('house');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'House', function($stateParams, House) {
                    return House.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'house',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('house-detail.edit', {
            parent: 'house-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/house/house-dialog.html',
                    controller: 'HouseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['House', function(House) {
                            return House.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('house.new', {
            parent: 'house',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/house/house-dialog.html',
                    controller: 'HouseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                key: null,
                                externalLink: null,
                                price: null,
                                startDate: null,
                                endDate: null,
                                meters: null,
                                numrooms: null,
                                floor: null,
                                details: null,
                                elevator: null,
                                facingOutside: null,
                                garage: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('house', null, { reload: true });
                }, function() {
                    $state.go('house');
                });
            }]
        })
        .state('house.edit', {
            parent: 'house',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/house/house-dialog.html',
                    controller: 'HouseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['House', function(House) {
                            return House.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('house', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('house.delete', {
            parent: 'house',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/house/house-delete-dialog.html',
                    controller: 'HouseDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['House', function(House) {
                            return House.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('house', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
