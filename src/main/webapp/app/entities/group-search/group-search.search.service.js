(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .factory('GroupSearchSearch', GroupSearchSearch);

    GroupSearchSearch.$inject = ['$resource'];

    function GroupSearchSearch($resource) {
        var resourceUrl =  'api/_search/group-searches/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
