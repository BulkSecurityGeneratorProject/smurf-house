(function() {
    'use strict';

    angular
        .module('smurfHouseApp')
        .controller('DashboardsMarketController', DashboardsMarketController);

    DashboardsMarketController.$inject = ['$scope', '$state', 'DashboardsMarket',
                                          'House', 'HouseSearch',
                                          'ParseLinks', 'AlertService', 'paginationConstants',
                                          'GroupSearch'];

    function DashboardsMarketController ($scope, $state, DashboardsMarket,
                                         House, HouseSearch,
                                         ParseLinks, AlertService, paginationConstants,
                                         GroupSearch) {
        var vm = this;
        vm.transition = transition;
        vm.clear = clear;
        vm.search = search;
        vm.determineColor = determineColor;
        vm.openlink = openlink;

        //vm.loadPage = loadPage;
        vm.predicate = null;
        vm.reverse = null;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.searchQuery = null;
        vm.currentSearch = null;

        vm.avgMeters = null;
        vm.avgPrice = null;
        vm.avgPriceMeter = null;

        vm.groupSearch = {};
        vm.groupsearches = GroupSearch.query();

        vm.garage = false;
        vm.elevator = false;
        vm.facingOutside = true;
        vm.sliderPrice = {};
        vm.sliderPrice.value = [10000,200000];
        vm.sliderPrice.min = 10000;
        vm.sliderPrice.max = 300000;
        vm.sliderPrice.step = 10000;

        vm.sliderMeters = {};
        vm.sliderMeters.value = [10,200];
        vm.sliderMeters.min = 10;
        vm.sliderMeters.max = 200;
        vm.sliderMeters.step = 10;

        vm.sliderFloor = {};
        vm.sliderFloor.value = [1,10];
        vm.sliderFloor.min = 0;
        vm.sliderFloor.max = 10;
        vm.sliderFloor.step = 1;

        function transition () {
            console.log ("transition ");

            HouseSearch.query({
                query: vm.currentSearch,
                //page: vm.page,
                page: 0,
                //size: vm.itemsPerPage,
                size: 1000,
                sort: sort()
            }, onSuccess, onError);

            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.houses = data;
                calculateAvgCounters();
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            function calculateAvgCounters () {
                var total = 0;
                var cntMeters = 0;
                var cntPrice = 0;
                angular.forEach(vm.houses, function (house) {
                    total++;
                    cntMeters+= house.meters;
                    cntPrice+= house.price;
                });

                vm.avgMeters = cntMeters / total;
                vm.avgPrice = cntPrice / total;
                vm.avgPriceMeter = vm.avgPrice / vm.avgMeters

            }

        }

        function search () {
        /*
            if (!searchQuery){
                return vm.clear();
            }
            */
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            var criteria = "";
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

        function addMissingCriteriaBoolean(value, field, criteria) {
            if (value != null && value == true) {
                if (criteria.trim() != "") {
                    criteria += " AND ";
                }
                return criteria + "_missing_:" + field;
            }
            return criteria;
        }


        function addExistCriteriaBoolean(value, field, criteria) {
            if (value != null && value == true) {
                if (criteria.trim() != "") {
                    criteria += " AND ";
                }
                return criteria + "_exists_:" + field;
            }
            return criteria;
        }

        function addRangeNumericCriteria (valueA, valueB, field, criteria) {
            if (valueA != null && valueB != null) {

                if (criteria.trim() != "") {
                    criteria += " AND ";
                }
                return criteria + field + ":[" + valueA + " TO " + valueB + "]";
            }
            return criteria
        }

        function clear () {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }

        function determineColor (value) {
            return (value > 0) ? "red" : "green";
        }

        function openlink(url)
        {
            console.log(url);
            alert (url);
            var instance = window.open("about:blank");
            instance.document.write("<meta http-equiv=\"refresh\" content=\"0;url="+url+"\">");
            instance.document.close();
            return false;
        }
    }
})();
