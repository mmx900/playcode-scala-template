'use strict';

angular.module('playcodeApp.controllers', ['ngSanitize'])
	.controller('RootCtrl', ['$scope', 'Session', 'AuthService',
		function ($scope, Session, AuthService) {
			$scope.Session = Session;
			$scope.AuthService = AuthService;
		}])
	.controller('MainCtrl', ['$scope', '$location', '$http', '$window',
		function ($scope, $location, $http) {
			$http.get("/item").
				success(function (data, status, headers, config) {
					$scope.charts = data;
				});
		}])
	.controller('SignUpCtrl', ['$scope', '$location', '$http', '$window', 'alertService',
		function ($scope, $location, $http, $window, alertService) {
			$scope.submit = function () {
				alertService.clear();

				if ($scope.password != $scope.password_check) {
					alertService.danger('비밀번호가 맞지 않습니다.');
					return;
				}

				$http.post('/signup', {
					email: $scope.email,
					nickname: $scope.nickname,
					password: $scope.password
				}).success(function (data, status, headers, config) {
					$window.location.href = '/#/chart';
				}).error(function (data, status, headers, config) {
					if (status === HTTP_STATUS.CONFLICT) {
						alertService.danger('이미 존재하는 아이디입니다.');
					} else {
						alertService.danger('오류가 발생했습니다.');
					}
				});
			}
		}])
	.controller('LoginCtrl', ['$scope', '$location', '$http', '$window', 'alertService',
		function ($scope, $location, $http, $window, alertService) {
			$scope.submit = function () {
				alertService.clear();

				$http.post('/login', {
					email: $scope.email,
					password: $scope.password
				}).success(function (data, status, headers, config) {
					$window.location.href = '/#/chart';
				}).error(function (data, status, headers, config) {
					if (status === HTTP_STATUS.UNAUTHORIZED) {
						alertService.danger('아이디 혹은 비밀번호가 맞지 않습니다.');
					} else if(status === HTTP_STATUS.NOT_FOUND) {
						alertService.danger('해당하는 사용자가 없습니다.');
					} else {
						alertService.danger('오류가 발생했습니다.');
					}
				});
			}
		}])
	.controller('ItemQueryCtrl', [ '$scope', '$location',
		function ($scope, $location) {
			$scope.search = function () {
				$location.path('/item').search('keyword', $scope.keyword);
			};
		} ]);