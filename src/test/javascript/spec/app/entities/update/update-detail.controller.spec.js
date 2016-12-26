'use strict';

describe('Controller Tests', function() {

    describe('Update Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockUpdate, MockGroupSearch, MockHouseUpdate;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockUpdate = jasmine.createSpy('MockUpdate');
            MockGroupSearch = jasmine.createSpy('MockGroupSearch');
            MockHouseUpdate = jasmine.createSpy('MockHouseUpdate');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Update': MockUpdate,
                'GroupSearch': MockGroupSearch,
                'HouseUpdate': MockHouseUpdate
            };
            createController = function() {
                $injector.get('$controller')("UpdateDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'smurfHouseApp:updateUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
