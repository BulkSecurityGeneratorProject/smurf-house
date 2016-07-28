(function() {
    'use strict';
    angular
        .module('smurfHouseApp')
        .factory('GroupSearch', GroupSearch);

    GroupSearch.$inject = ['$resource'];

    function GroupSearch ($resource) {
        var resourceUrl =  'api/group-searches/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
