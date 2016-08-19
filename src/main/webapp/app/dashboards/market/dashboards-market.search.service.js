(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .factory('DashboardsMarketSearch', DashboardsMarketSearch);

    DashboardsMarketSearch.$inject = ['$resource'];

    function DashboardsMarketSearch($resource) {
        var resourceUrl =  'api/_search/group-searches/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
