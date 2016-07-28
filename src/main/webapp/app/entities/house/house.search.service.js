(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .factory('HouseSearch', HouseSearch);

    HouseSearch.$inject = ['$resource'];

    function HouseSearch($resource) {
        var resourceUrl =  'api/_search/houses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
