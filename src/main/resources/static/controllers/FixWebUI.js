var fixApp = angular.module('fixApp', ['ngSanitize', 'ngAnimate', 'ngTouch', 'ui.select', 'ui.bootstrap', 'ui.grid', 'ui.grid.edit', 'ui.grid.selection','ui.grid.autoResize', 'angularjsNotify']);

fixApp.controller('MainCtrl', ['$scope', '$http', '$uibModal', '$interval' ,'$window', 'uiGridConstants', '$filter', '$timeout','confirmService', 'Notify', function ($scope, $http, $uibModal, $interval, $window, uiGridConstants, $filter, $timeout, confirmService, Notify) {

	$scope.debugMode = true;
	$scope.debugClass = 'black';
	//$scope.allocMsgType = 'AllocationInstruction';
	
	$scope.toggleDebug = function() {
		$scope.debugMode = !$scope.debugMode;
		if($scope.debugMode) {
			$scope.debugClass = 'green';
		} else {
			$scope.debugClass = 'black';
		}
	}

	$scope.model = { };
	$scope.model.data = { };
	$scope.model.data.customTags = { };
	$scope.model.data.fixMsgType = 'AllocationInstruction';

	$scope.internal = { };
	
	$scope.testCases = [{'id':'FIX44', 'name':'2 Allocations - 50mm'}, {'id':'FIXT1.1', 'name':'5 Allocations - 250mm'}];
	$scope.internal.selectedTestCase = $scope.testCases[0];
	
	$scope.fixVersions = [{'key':'FIX44', 'value':'FIX 4.4 - Allocation Instruction'}, {'key':'FIXT1.1', 'value':'FIX 5.0 - Allocation Report'}];
	$scope.internal.selectedFixVersion = $scope.fixVersions[0];

	$scope.buySellOpts = [{'key':'1', 'value':'BUY'}, {'key':'2', 'value':'SELL'}];
	$scope.internal.selectedBuySell = $scope.buySellOpts[0];
	
	$scope.securitySourceList = [{'key':'1', 'value':'CUSIP'}, {'key':'2', 'value':'SEDOL'}, {'key':'4', 'value':'ISIN'}];
	$scope.internal.selectedSecurityIdSource = $scope.securitySourceList[0];
	
	$scope.loadDefaults = function () {
		$http({
			url : 'public/defaults',
			method : "GET"
		}).then(function(response) {
			$scope.defaults = response.data;
			$scope.processDefaults();
		});
	}
	
	$scope.processDefaults = function() {
		//$scope.model.data.senderCompId = $scope.defaults.senderCompId;
		//$scope.model.data.targetCompId = $scope.defaults.targetCompId;
		$scope.model.data.tradeDate = $filter('date')(new Date(), 'yyyyMMdd');
		var sttlDate = new Date();
		sttlDate.setDate(sttlDate.getDate()+2);
		$scope.model.data.settleDate = $filter('date')(sttlDate, 'yyyyMMdd');
	}
	
	$scope.loadDefaults();
	
	$scope.loadSessions = function () {
		$http({
			url : 'public/fix/sessions',
			method : "GET"
		}).then(function(response) {
			$scope.fixSessions = response.data;
			$scope.internal.selectedFixSession = $scope.fixSessions[0];
		});
	}
	
	$scope.loadSessions();
	
	$scope.model.data.securityId="912828CR7";
	$scope.model.data.onBehalfOfCompId="MY_CLIENT_ID";
	
	var columnDefs1 = [
		    { name: 'id', displayName: 'ID', enableCellEdit: true, width: '5%' },
		    { name: 'account', displayName: 'Account', enableCellEdit: true, width: '15%' },
		    { name: 'quantity', displayName: 'Quantity', enableCellEdit: true, width: '10%' },
		    { name: 'netMoney', displayName: 'NetMoney', enableCellEdit: true, width: '20%' },
		    { name: 'accruedInterest', displayName: 'AccruedInterest', enableCellEdit: true, width: '20%' },
		    { name: 'settlementCurrency', displayName: 'STL Currency', enableCellEdit: true, width: '15%' },
		    { name: 'settlementLocation', displayName: 'STL Location', enableCellEdit: true, width: '200', editableCellTemplate: 'ui-grid/dropdownEditor', editDropdownValueLabel: 'name', editDropdownOptionsArray: [
		        { id: 'CED', name: 'ClearStream' }, 
		        { id: 'DTC', name: 'DTC' }, 
		        { id: 'EUR', name: 'EuroClear' }, 
		        { id: 'FED', name: 'FED' },
		        { id: 'CREST', name: 'CREST' } 

		      ] }
	    ];
	
	var customTagColumnDef = [
	    { name: 'key', displayName: 'Key', enableCellEdit: true, width: '50%' },
	    { name: 'value', displayName: 'Value', enableCellEdit: true, width: '50%' },
	    ];
 
	$scope.model.data.allocs = [
		    {
		      "id": "1",
		      "account": "ACCT-1",
		      "quantity": "500",
		      "netMoney": "500050",
		      "accruedInterest": "50",
		      "settlementCurrency": "USD",
		      "settlementLocation" : "DTC"
		    }, {
		      "id": "2",
		      "account": "ACCT-2",
		      "quantity": "500",
		      "netMoney": "500050",
		      "accruedInterest": "50",
		      "settlementCurrency": "CAD"
		    }
	];
	
	$scope.allocTestCaseMenu = {
		    isopen: false
		  };
	
	$scope.gridOpts = {
		enableRowSelection : true,
		showGridFooter : true,
		multiSelect : false,
		columnDefs : columnDefs1,
		data : $scope.model.data.allocs
	};
	
	$scope.customTagGridOpts = {
			enableRowSelection : true,
			showGridFooter : true,
			multiSelect : false,
			columnDefs : customTagColumnDef,
			data : $scope.model.data.customTags
	};
	
	$scope.customTagGridOpts.onRegisterApi = function(gridApi){
	    $scope.customTagGridApi = gridApi;
	    gridApi.selection.on.rowSelectionChanged($scope,function(row){
	        $scope.selectedCustomRow = row.entity;
	      });
        $timeout(function () {
            gridApi.grid.handleWindowResize();
        });	    
	};

	$scope.addData = function() {
	    var n = $scope.gridOpts.data.length + 1;
	    $scope.gridOpts.data.push({
			      "id": "",
			      "account": "",
			      "quantity": "0",
			      "netMoney": "0",
			      "accruedInterest": "0",
			      "settlementCurrency": "USD"
	              });
	  };	  
		  
	$scope.removeData = function() {
		var index = $scope.gridOpts.data.indexOf($scope.selectedRow);
	    if (index > -1) {
	    	$scope.gridOpts.data.splice(index, 1);
	    }
	}
	
	$scope.viewData = function() {
		//$scope.model.data.fixVersion = $scope.internal.selectedFixVersion.key;
		$scope.model.data.sessionId = $scope.internal.selectedFixSession.name;
		$scope.model.data.buySell = $scope.internal.selectedBuySell.key;
		$scope.model.data.securitySource = $scope.internal.selectedSecurityIdSource.key;
		
		//$scope.result = JSON.stringify($scope.model.data, null, 4);

		$scope.sendToServer();
	}
	
	$scope.gridOpts.onRegisterApi = function(gridApi){
	    $scope.gridApi = gridApi;
	    gridApi.selection.on.rowSelectionChanged($scope,function(row){
	        $scope.selectedRow = row.entity;
	      });
        $timeout(function () {
            gridApi.grid.handleWindowResize();
        });	    
	};
   
	$scope.checkForSessionTimeout = function(data) {
		console.log("Checking for Session Timeout...");
		if (typeof data == 'string' || Array.isArray(data)) {
			if (data.indexOf("Login") > -1) {
				console.log("Session is Timed out, redirecting to login page...");
				window.location.href = "logout";
			}
		}
	}
	
	$scope.sendToServer = function () {
		$http({
			url : 'public/fix/allocation/send',
			method : "POST",
			data : $scope.model.data
		}).then(function(response) {
			console.log(response.data);
			$scope.resultFix = response.data.result;
		});
	}
	
	$scope.saveAsNew = function() {
		$scope.model.data.id = null;
		for(var i=0; i< $scope.model.data.allocs.length; i++) {
			$scope.model.data.allocs[i].uuid = null;
		}
		$scope.saveTestCase();
	}
	
	$scope.removeTestCase = function () {
		
		if($scope.model.data.id == null) {
			//Notification('No model loaded in memory!');
			//Notification.error({message: 'Error notification 1s', delay: 2000});
			Notify.addMessage('Hello', 'info');
			return;
		}
		
		var msg = "Are you sure you want to remove the selected test case?";
		
		var modalOptions = {
			closeButtonText : 'Cancel',
			actionButtonText : 'Remove',
			headerText : ':: Confirm ::',
			bodyText : msg
		};

		confirmService.showModal({}, modalOptions)
				.then(function(result) {
					$http({
						url : 'public/cases/allocation/' + $scope.model.data.id,
						method : "DELETE"
					}).then(function(response) {
						console.log(response.data);
					});
				});
//		var $uibModalInstance = $uibModal.open({
//			templateUrl : 'templates/RemoveTestCaseTemplate.html',
//			scope: $scope,
//			size: 'md',
//			controller : 
//				
//			function ($scope, $uibModal, $uibModalInstance) {
//				
//				$scope.close = function() {
//					$uibModalInstance.close();
//				};
//				
//				$scope.sendMessage = function() {
//				}
//				
//			},
//			backdrop : 'static'
//		});		
	}
	
	$scope.saveTestCase = function () {
		$http({
			url : 'public/cases/allocation',
			method : "POST",
			data : $scope.model.data
		}).then(function(response) {
			console.log(response.data);
			$scope.model.data = response.data;
			$scope.gridOpts.data = $scope.model.data.allocs;
			$scope.customTagGridOpts.data = $scope.model.data.customTags;
		});
	}
	
	$scope.clearNew = function() {
		$scope.model.data = {
				securityId: '912828CR7',
				onBehalfOfCompId: '',
				allocs : []
		};
		
		$scope.model.data.tradeDate = $filter('date')(new Date(), 'yyyyMMdd');
		var sttlDate = new Date();
		sttlDate.setDate(sttlDate.getDate()+2);
		$scope.model.data.settleDate = $filter('date')(sttlDate, 'yyyyMMdd');
		
		$scope.gridOpts.data = $scope.model.data.allocs;
		
		$scope.internal.selectedBuySell = $scope.buySellOpts[0];
		$scope.internal.selectedSecurityIdSource = $scope.securitySourceList[0];
		$scope.internal.selectedTestCase = null;
	}
	
	$scope.setTestCaseName = function() {
		var $uibModalInstance = $uibModal.open({
			templateUrl : 'templates/SaveTestCaseTemplate.html',
			scope: $scope,
			size: 'md',
			controller : 
				
			function ($scope, $uibModal, $uibModalInstance) {
				
				$scope.close = function() {
					$uibModalInstance.close();
				};
				
				$scope.addNameAndSave = function() {
					$scope.model.data.name = $scope._value;
					$scope.close();
				}
				
			},
			backdrop : 'static'
		});
	};
	
	$scope.loadTestCase = function() {
		var $uibModalInstance = $uibModal.open({
			templateUrl : 'templates/LoadTestCaseTemplate.html',
			scope: $scope,
			size: 'md',
			controller : 
				
			function ($scope, $uibModal, $uibModalInstance) {
				
				$scope.close = function() {
					$uibModalInstance.close();
				};
				
				$scope.loadTestCase = function() {
				}
				
			},
			backdrop : 'static'
		});
	};
	
	$scope.sendMessageToServer = function() {
		var $uibModalInstance = $uibModal.open({
			templateUrl : 'templates/SendMessageTemplate.html',
			scope: $scope,
			size: 'md',
			controller : 
				
			function ($scope, $uibModal, $uibModalInstance) {
				
				$scope.close = function() {
					$uibModalInstance.close();
				};
				
				$scope.sendMessage = function() {
				}
				
			},
			backdrop : 'static'
		});
	};
	
	$scope.addCustomFix = function() {
		console.log('addCustomFix...');
		var $uibModalInstance = $uibModal.open({
			templateUrl : 'templates/CustomFixTemplate.html',
			scope: $scope,
			size: 'md',
			controller : 
				
			function ($scope, $uibModal, $uibModalInstance) {
				
				$scope.close = function() {
					$uibModalInstance.close();
				};
				
				$scope.addNewCustomTag = function() {
					$scope.model.data.customTags.push({key: $scope._key, value: $scope._value});
					$scope.cancel();
				}
				
				$scope.removeSelectedCustomTag = function() {
					var index = $scope.customTagGridOpts.data.indexOf($scope.selectedCustomRow);
				    if (index > -1) {
				    	$scope.customTagGridOpts.data.splice(index, 1);
				    }
				}
				
			},
			backdrop : 'static'
		});
	};
	  
//	$scope.removeCustomFix = function() {
//		var index = $scope.customTagGridOpts.data.indexOf($scope.selectedCustomRow);
//	    if (index > -1) {
//	    	$scope.customTagGridOpts.data.splice(index, 1);
//	    }
//	}
	
	$scope.customTagStatus = false;
	$scope.toggleOpen = function() {
		$scope.customTagStatus = !$scope.customTagStatus;
	}
	
}
]);

fixApp.directive('jsonText', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attr, ngModel) {            
          function into(input) {
            console.log(JSON.parse(input));
            return JSON.parse(input);
          }
          function out(data) {
            return JSON.stringify(data, undefined, 2);
          }
          ngModel.$parsers.push(into);
          ngModel.$formatters.push(out);
        }
    };
});

//fixApp.config(function(NotificationProvider) {
//    NotificationProvider.setOptions({
//        delay: 10000,
//        startTop: 20,
//        startRight: 10,
//        verticalSpacing: 20,
//        horizontalSpacing: 20,
//        positionX: 'left',
//        positionY: 'bottom'
//    });
//});
