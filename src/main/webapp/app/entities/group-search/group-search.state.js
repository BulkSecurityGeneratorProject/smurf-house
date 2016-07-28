(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('group-search', {
            parent: 'entity',
            url: '/group-search?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.groupSearch.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/group-search/group-searches.html',
                    controller: 'GroupSearchController',
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
                    $translatePartialLoader.addPart('groupSearch');
                    $translatePartialLoader.addPart('provider');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('group-search-detail', {
            parent: 'entity',
            url: '/group-search/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.groupSearch.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/group-search/group-search-detail.html',
                    controller: 'GroupSearchDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('groupSearch');
                    $translatePartialLoader.addPart('provider');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'GroupSearch', function($stateParams, GroupSearch) {
                    return GroupSearch.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'group-search',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('group-search-detail.edit', {
            parent: 'group-search-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-search/group-search-dialog.html',
                    controller: 'GroupSearchDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['GroupSearch', function(GroupSearch) {
                            return GroupSearch.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('group-search.new', {
            parent: 'group-search',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-search/group-search-dialog.html',
                    controller: 'GroupSearchDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                title: null,
                                url: null,
                                provider: null,
                                maxLimitPrice: null,
                                activated: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('group-search', null, { reload: true });
                }, function() {
                    $state.go('group-search');
                });
            }]
        })
        .state('group-search.edit', {
            parent: 'group-search',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-search/group-search-dialog.html',
                    controller: 'GroupSearchDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['GroupSearch', function(GroupSearch) {
                            return GroupSearch.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('group-search', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('group-search.delete', {
            parent: 'group-search',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/group-search/group-search-delete-dialog.html',
                    controller: 'GroupSearchDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['GroupSearch', function(GroupSearch) {
                            return GroupSearch.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('group-search', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
