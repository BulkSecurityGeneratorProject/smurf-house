(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dashboards-updates', {
            parent: 'dashboards',
            url: '/dashboards-updates?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'smurfHouseApp.dashboards.updates.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/dashboards/updates/dashboards-updates.html',
                    controller: 'DashboardsUpdatesController',
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
