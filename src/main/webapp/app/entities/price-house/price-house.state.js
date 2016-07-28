(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('price-house', {
            parent: 'entity',
            url: '/price-house?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.priceHouse.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/price-house/price-houses.html',
                    controller: 'PriceHouseController',
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
                    $translatePartialLoader.addPart('priceHouse');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('price-house-detail', {
            parent: 'entity',
            url: '/price-house/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.priceHouse.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/price-house/price-house-detail.html',
                    controller: 'PriceHouseDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('priceHouse');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'PriceHouse', function($stateParams, PriceHouse) {
                    return PriceHouse.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'price-house',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('price-house-detail.edit', {
            parent: 'price-house-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/price-house/price-house-dialog.html',
                    controller: 'PriceHouseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PriceHouse', function(PriceHouse) {
                            return PriceHouse.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('price-house.new', {
            parent: 'price-house',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/price-house/price-house-dialog.html',
                    controller: 'PriceHouseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                price: null,
                                whenChanged: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('price-house', null, { reload: true });
                }, function() {
                    $state.go('price-house');
                });
            }]
        })
        .state('price-house.edit', {
            parent: 'price-house',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/price-house/price-house-dialog.html',
                    controller: 'PriceHouseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PriceHouse', function(PriceHouse) {
                            return PriceHouse.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('price-house', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('price-house.delete', {
            parent: 'price-house',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/price-house/price-house-delete-dialog.html',
                    controller: 'PriceHouseDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PriceHouse', function(PriceHouse) {
                            return PriceHouse.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('price-house', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
