(function() {
    'use strict';
    angular
        .module('smurfHouseApp')
        .factory('Update', Update);

    Update.$inject = ['$resource', 'DateUtils'];

    function Update ($resource, DateUtils) {
        var resourceUrl =  'api/updates/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.updateDate = DateUtils.convertLocalDateFromServer(data.updateDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.updateDate = DateUtils.convertLocalDateToServer(data.updateDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.updateDate = DateUtils.convertLocalDateToServer(data.updateDate);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
