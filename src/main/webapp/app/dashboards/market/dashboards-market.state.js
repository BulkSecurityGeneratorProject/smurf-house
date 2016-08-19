(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dashboards-market', {
            parent: 'dashboards',
            url: '/dashboards-market?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.dashboards.market.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboards/market/dashboards-market.html',
                    controller: 'DashboardsMarketController',
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
                    $translatePartialLoader.addPart('dashboards');
                    $translatePartialLoader.addPart('provider');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        });
    }

})();
