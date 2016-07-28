(function() {
    'use strict';
    angular
        .module('smurfHouseApp')
        .factory('PriceHouse', PriceHouse);

    PriceHouse.$inject = ['$resource', 'DateUtils'];

    function PriceHouse ($resource, DateUtils) {
        var resourceUrl =  'api/price-houses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.whenChanged = DateUtils.convertLocalDateFromServer(data.whenChanged);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.whenChanged = DateUtils.convertLocalDateToServer(data.whenChanged);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.whenChanged = DateUtils.convertLocalDateToServer(data.whenChanged);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
