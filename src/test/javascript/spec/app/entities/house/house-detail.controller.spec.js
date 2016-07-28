'use strict';

describe('Controller Tests', function() {

    describe('House Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockHouse, MockPriceHouse, MockGroupSearch;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockHouse = jasmine.createSpy('MockHouse');
            MockPriceHouse = jasmine.createSpy('MockPriceHouse');
            MockGroupSearch = jasmine.createSpy('MockGroupSearch');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'House': MockHouse,
                'PriceHouse': MockPriceHouse,
                'GroupSearch': MockGroupSearch
            };
            createController = function() {
                $injector.get('$controller')("HouseDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smurfHouseApp:houseUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
