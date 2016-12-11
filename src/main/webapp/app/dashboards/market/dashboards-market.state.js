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
            resolve: {
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
