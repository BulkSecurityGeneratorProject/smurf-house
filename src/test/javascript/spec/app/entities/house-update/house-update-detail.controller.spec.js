'use strict';

describe('Controller Tests', function() {

    describe('HouseUpdate Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockHouseUpdate, MockUpdate, MockHouse;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockHouseUpdate = jasmine.createSpy('MockHouseUpdate');
            MockUpdate = jasmine.createSpy('MockUpdate');
            MockHouse = jasmine.createSpy('MockHouse');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'HouseUpdate': MockHouseUpdate,
                'Update': MockUpdate,
                'House': MockHouse
            };
            createController = function() {
                $injector.get('$controller')("HouseUpdateDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smurfHouseApp:houseUpdateUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
