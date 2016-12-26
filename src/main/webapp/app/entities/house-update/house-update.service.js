(function() {
    'use strict';
    angular
        .module('smurfHouseApp')
        .factory('HouseUpdate', HouseUpdate);

    HouseUpdate.$inject = ['$resource'];

    function HouseUpdate ($resource) {
        var resourceUrl =  'api/house-updates/:id';

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
