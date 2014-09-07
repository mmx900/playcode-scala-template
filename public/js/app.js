'use strict';

angular.module('playcodeApp', ['ngRoute', 'ngResource', 'playcodeApp.controllers'])
	.constant('USER_ROLES', {
		all: '*',
		admin: 'admin',
		user: 'user'
	})
	.service('Session', function () {
		this.create = function (userId, nickname, role) {
			this.userId = userId;
			this.nickname = nickname;
			this.role = role;
		};

		this.destroy = function () {
			this.userId = null;
			this.nickname = null;
			this.role = null;
		};

		return this;
	})
	.factory('AuthService', function ($http, Session, USER_ROLES) {
		return {
			logout: function () {
				return $http.get('/logout').then(function () {
					Session.destroy();
				});
			},
			isAuthenticated: function () {
				return !!Session.userId;
			},
			getAuthentication: function () {
				return $http.get('/loggedin').then(function (res) {
					var user = res.data;

					if (user !== '0') {
						Session.create(user.id, user.nickname, USER_ROLES.user);
					} else {
						Session.destroy();
					}

					return user;
				});
			}
		}
	})
	.factory('alertService', function($rootScope) {
		$rootScope.alerts = [];

		var alertService;

		return alertService = {
			add: function (msg, type) {
				$rootScope.alerts.push({type: type, msg: msg, close: function() {
					return alertService.closeAlert(this);
				}});
			},
			danger: function (msg) {
				this.add(msg, 'danger');
			},
			closeAlert: function(alert) {
				return this.closeAlertIdx($rootScope.alerts.indexOf(alert));
			},
			closeAlertIdx: function(index) {
				return $rootScope.alerts.splice(index, 1);
			},
			clear: function() {
				$rootScope.alerts = [];
			}
		};
	})
	.config([ '$routeProvider', '$httpProvider',
		function ($routeProvider, $httpProvider) {
			var checkLoggedin = function ($q, $timeout, $window, $location, AuthService) {
				var deferred = $q.defer();

				AuthService.getAuthentication().then(function (user) {
					if (user !== '0') {
						$timeout(deferred.resolve, 0);
					} else {
						$timeout(function () {
							deferred.reject();
						}, 0);
						$window.location.href = '/login?redirectPath=/%23' + $location.path();
					}
				});

				return deferred.promise;
			};

			$httpProvider.interceptors.push(function ($rootScope, $q, $window) {
				return {
					responseError: function (response) {
						if (response.status === 401)
							$window.location.href = '/login';

						return $q.reject(response);
					}
				}
			});

			$routeProvider.when('/', {
				title: '시작',
				templateUrl: '/assets/tpl/main.tpl.html',
				controller: 'MainCtrl'
			}).when('/login', {
				title: '로그인',
				templateUrl: '/assets/tpl/login.tpl.html',
				controller: 'LoginCtrl'
			}).when('/signup', {
				title: '회원가입',
				templateUrl: '/assets/tpl/signup.tpl.html',
				controller: 'SignUpCtrl'
			})
		} ])
	.run(['$location', '$rootScope', '$http', 'AuthService',
		function ($location, $rootScope, $http, AuthService) {
			AuthService.getAuthentication();

			$rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
				$rootScope.routeTitle = current.$$route.title;
				$rootScope.showJumbotron = $location.path() === "/";
			});
		}]);