(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('DashboardsUpdatesController', DashboardsUpdatesController);

    DashboardsUpdatesController.$inject = ['$scope', '$state', '$filter',
                                          'House', 'HouseSearch', 'Update','HouseUpdate',
                                          'ParseLinks', 'AlertService', 'paginationConstants',
                                          'GroupSearch'];

    function DashboardsUpdatesController ($scope, $state, $filter,
                                         House, HouseSearch, Update, HouseUpdate, 
                                         ParseLinks, AlertService, paginationConstants,
                                         GroupSearch) {
        var vm = this;
        vm.transition = transition;
        vm.clear = clear;
        vm.search = search;
        vm.currentSearch;
        vm.onChangeDate = onChangeDate;
        vm.setDefaultDates = setDefaultDates;
        vm.showHouseUpdates = showHouseUpdates;
        vm.openlink = openlink;


        vm.updates = [];
        vm.setDefaultDates();
        vm.onChangeDate();
        vm.houseUpdates = [];

        function showHouseUpdates(id) {
            console.log (id);
            HouseUpdate.getByUpdateId ({id: id}, function(result, headers){
                vm.houseUpdates = result;
                //vm.links = ParseLinks.parse(headers('link'));
                //vm.totalItems = headers('X-Total-Count');
                console.log (result);

            });
        }

        function onChangeDate () {
            var dateFormat = 'yyyy-MM-dd';
            var fromDate = $filter('date')(vm.fromDate, dateFormat);
            var toDate = $filter('date')(vm.toDate, dateFormat);
            console.log ("fromDate", fromDate);
            console.log ("toDate", toDate);

            Update.getUpdatesByDates({fromDate: fromDate, toDate: toDate}, function(result, headers){
                vm.updates = result;
                //vm.links = ParseLinks.parse(headers('link'));
                //vm.totalItems = headers('X-Total-Count');
                console.log ("updates", result);

            });
        }


        // Date picker configuration
        function setDefaultDates () {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            vm.toDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1);
            vm.fromDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() );
        }



        function transition () {

            HouseSearch.query({
                query: vm.currentSearch,
                //page: vm.page,
                page: 0,
                //size: vm.itemsPerPage,
                size: 1000,
                sort: sort()
            }, onSuccess, onError);

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.houses = data;
                calculateAvgCounters();
                refreshGraph ();
                vm.api.refresh();
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function search () {
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            var criteria = "";

            criteria = addGroupSearchCriteria (vm.groupSearch, "groupSearch.id", criteria);
            criteria = addExistCriteriaBoolean (vm.elevator, "elevator", criteria);
            criteria = addExistCriteriaBoolean (vm.garage, "garage", criteria);
            criteria = addExistCriteriaBoolean (vm.facingOutside, "facingOutside", criteria);
            criteria = addRangeNumericCriteria (vm.sliderPrice.value[0], vm.sliderPrice.value[1], "price" , criteria);
            criteria = addRangeNumericCriteria (vm.sliderMeters.value[0], vm.sliderMeters.value[1], "meters" , criteria);
            criteria = addRangeNumericCriteria (vm.sliderFloor.value[0], vm.sliderFloor.value[1], "floor" , criteria);
            criteria = addMissingCriteriaBoolean (true, "endDate", criteria);

            console.log ("criteria " , criteria);

            vm.currentSearch = criteria;
            vm.houses = null;
            vm.transition();
        }

        function clear () {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }

        function openlink(url) {
            console.log("open " , url);
            var instance = window.open("about:blank");
            instance.document.write("<meta http-equiv=\"refresh\" content=\"0;url="+url+"\">");
            instance.document.close();
            return false;
        }

    }
})();
