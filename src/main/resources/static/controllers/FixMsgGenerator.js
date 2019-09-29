var fixApp = angular.module('fixApp', ['ngSanitize', 'ngAnimate', 'ngTouch', 'ui.select', 'ui.bootstrap', 'ui.grid', 'ui.grid.edit', 'ui.grid.selection',]);

fixApp.controller('MainCtrl', ['$scope', '$http', '$uibModal', '$interval' ,'$window', 'uiGridConstants', '$filter', function ($scope, $http, $uibModal, $interval, $window, uiGridConstants, $filter) {

	$scope.debugMode = false;

	$scope.model = { };
	$scope.internal = { };
	
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
		//$scope.model.senderCompId = $scope.defaults.senderCompId;
		//$scope.model.targetCompId = $scope.defaults.targetCompId;
		$scope.model.tradeDate = $filter('date')(new Date(), 'yyyyMMdd');
		var sttlDate = new Date();
		sttlDate.setDate(sttlDate.getDate()+2);
		$scope.model.settleDate = $filter('date')(sttlDate, 'yyyyMMdd');
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
	  
	$scope.model.allocs = [
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
	
	$scope.gridOpts = {
		enableRowSelection : true,
		showGridFooter : true,
		multiSelect : false,
		columnDefs : columnDefs1,
		data : $scope.model.allocs
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
		//$scope.model.fixVersion = $scope.internal.selectedFixVersion.key;
		$scope.model.sessionId = $scope.internal.selectedFixSession.name;
		$scope.model.buySell = $scope.internal.selectedBuySell.key;
		$scope.model.securitySource = $scope.internal.selectedSecurityIdSource.key;
		$scope.result = JSON.stringify($scope.model, null, 4);

		$scope.sendToServer();
	}
	
	$scope.gridOpts.onRegisterApi = function(gridApi){
	    $scope.gridApi = gridApi;
	    gridApi.selection.on.rowSelectionChanged($scope,function(row){
	        $scope.selectedRow = row.entity;
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
			data : $scope.model
		}).then(function(response) {
			console.log(response.data);
			$scope.resultFix = response.data.result;
		});
	}
	  
}
]);
