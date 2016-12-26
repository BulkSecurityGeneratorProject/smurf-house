(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .factory('UpdateSearch', UpdateSearch);

    UpdateSearch.$inject = ['$resource'];

    function UpdateSearch($resource) {
        var resourceUrl =  'api/_search/updates/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
