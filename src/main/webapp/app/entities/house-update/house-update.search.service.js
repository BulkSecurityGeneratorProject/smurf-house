(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .factory('HouseUpdateSearch', HouseUpdateSearch);

    HouseUpdateSearch.$inject = ['$resource'];

    function HouseUpdateSearch($resource) {
        var resourceUrl =  'api/_search/house-updates/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
