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


        vm.options = {
            chart: {
                type: 'scatterChart',
                scatter: {
	            		dispatch: {
                            elementClick: function (e) {
                                vm.openlink(e.point.data.externalLink);
                            },
                            elementMouseover: function(e){
                                var row = $("#" + e.point.data.id);
                                row.css("background","yellow");
                            },
                            elementMouseout: function(e){
                                var row = $("#" + e.point.data.id);
                                row.css("background","");

                            }
	                    }
	            },
                height: 250,
                showLegend: false,
                showDistX: true,
                showDistY: true,
                /*
                tooltip: {
                    contentGenerator: function(d) {
                        console.log(d);
                        var title = d.point.data.title;
                        var price = d.point.data.price;
                        var description = d.point.data.details;
                        return '<p><h6>' + title + '</h6> - ' + price + '<br/><small>' + description + "</small></p>";
                    }
                },
                */
                duration: 350,
                xAxis: {
                    axisLabel: '€/m2',
                    tickFormat: function(d){
                        return d3.format('.0')(d);
                    }
                },
                yAxis: {
                    axisLabel: 'm2',
                    tickFormat: function(d){
                        return d3.format('.0')(d);
                    },
                    axisLabelDistance: -5
                },
            }
        };
        vm.data = [];
        /*
        vm.data = [ {
            key: 'xxxx',
            values: []
         }];
         */

        function transition () {

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
                refreshGraph ();
                vm.api.refresh();
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

            function refreshGraph(){

                vm.data = [];
                var cnt = 0;

                angular.forEach(vm.houses, function (house) {
                    vm.data.push({
                        key: house.title,
                        values: []
                     });
                    var sizePoint = (0.5 * house.price ) / vm.avgPrice;
                    vm.data[cnt].values.push( {
                                            x: Math.round(house.price / house.meters) ,
                                            y: house.meters,
                                            size: sizePoint,
                                            shape: 'circle',
                                            data: house
                    } )

                    cnt++;
                });
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

        function addAndToCriteria (criteria) {
            if (criteria == null || criteria.trim() == "") {
                return "";
            } else {
                return criteria += " AND ";
            }
        }

        function addGroupSearchCriteria (zones, field, criteria) {
            if (zones != null && zones.length != null && zones.length != 0 ) {
                criteria = addAndToCriteria(criteria);

                var s = "";
                angular.forEach(zones, function (zone) {
                    s+= (s=="") ? "" :  " OR " ;
                    s+= zone.id;
                });



                return criteria + field + ":(" + s + ")";
            }
            return criteria;
        }

        function addMissingCriteriaBoolean(value, field, criteria) {
            if (value != null && value == true) {
                criteria = addAndToCriteria(criteria);
                return criteria + "_missing_:" + field;
            }
            return criteria;
        }


        function addExistCriteriaBoolean(value, field, criteria) {
            if (value != null && value == true) {
                criteria = addAndToCriteria(criteria);
                return criteria + "_exists_:" + field;
            }
            return criteria;
        }

        function addRangeNumericCriteria (valueA, valueB, field, criteria) {
            if (valueA != null && valueB != null) {
                criteria = addAndToCriteria(criteria);
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
            console.log("open " , url);
            var instance = window.open("about:blank");
            instance.document.write("<meta http-equiv=\"refresh\" content=\"0;url="+url+"\">");
            instance.document.close();
            return false;
        }
    }
})();
