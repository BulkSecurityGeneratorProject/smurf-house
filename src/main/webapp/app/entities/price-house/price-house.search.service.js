(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .factory('PriceHouseSearch', PriceHouseSearch);

    PriceHouseSearch.$inject = ['$resource'];

    function PriceHouseSearch($resource) {
        var resourceUrl =  'api/_search/price-houses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
