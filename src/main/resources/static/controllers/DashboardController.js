unikornApp.controller('DashboardController', [ '$scope', '$http', '$log', 'uiGridConstants', '$uibModal' ,'$window' ,'$interval' ,'confirmService', function($scope, $http, $log, uiGridConstants, $uibModal, $window, $interval ,confirmService) {
	$scope.reporte = {
			fechaDesde : null,
			fechaHasta : null
	};
	
	$scope.fechaDesde = {
		    opened: false
	};

	$scope.fechaHasta = {
	    opened: false
	};
	
	$scope.openFechaDesde = function() {
	    $scope.fechaDesde.opened = true;
	};
	
	$scope.openFechaHasta = function() {
	    $scope.fechaHasta.opened = true;
	};

	$scope.series = ['Facturación (ARS$) ','Honorarios (ARS$) '];
	
	$scope.buscarFacturaMetricas = function() {   	  	
		$http({
			url : 'services/Factura/Metrica',
			method : "GET"
		}).then(function(response) {
			$scope.facturaMetricas = response.data;
			
			$scope.labels = [];
			$scope.data = [];
			$scope.data.push([]);
			$scope.data.push([]);
			
			for(var index in $scope.facturaMetricas) {
				$scope.labels.push($scope.facturaMetricas[index].anio);
				$scope.data[0].push($scope.facturaMetricas[index].facturacion);
				$scope.data[1].push($scope.facturaMetricas[index].honorarios);
			}
			
		});
	}
	
	$scope.buscarFacturaMetricas();
	
	$scope.foliadoOpciones = function() {
		//var title = "foliado";
		//console.log("Selected: " + row.entity);
		var $uibModalInstance = $uibModal.open({
			templateUrl : 'templates/foliadoTemplate.html',
			scope: $scope,
			controller : 
				
			function ($scope, $uibModal, $uibModalInstance) {
				
				$scope.cancel = function() {
					$uibModalInstance.close();
				};
				
				$scope.foliado = function() {   
					if($scope.foliadoDesde != null && $scope.foliadoHasta !=null) {
						$scope.msgVisible = false;
						$window.location.href = 'services/Foliado/' + $scope.foliadoDesde + '/' + $scope.foliadoHasta;
					} else {
						$scope.msgVisible = true;
						$scope.msgType = "bg-danger";
						$scope.msg = "Debe ingresar Nro. Desde y Nro. Hasta!";
					}
				}
				
			},
			backdrop : 'static'
		});
	};
	
	$scope.reporteContadorOpciones = function() {
		var $uibModalInstance = $uibModal.open({
			templateUrl : 'templates/reporteContadorTemplate.html',
			scope: $scope,
			controller : 
				
			function ($scope, $uibModal, $uibModalInstance) {
				
				$scope.cancel = function() {
					$uibModalInstance.close();
				};
				
				$scope.reporteContador = function() {
					$scope.mes = $scope.meses.indexOf($scope.model.mes) + 1;
			        if ($scope.mes.toString().length == 1) {
			        	$scope.mes = "0" + $scope.mes;
			        }
					
					if($scope.model.anio != null && $scope.mes !=null) {
						$scope.msgVisible = false;
						//$window.location.href = 'services/Facturacion/Contador/' + $scope.reporte.fechaDesde + '/' + $scope.reporte.fechaHasta;
						$scope.url = 'services/Facturacion/Contador/' + $scope.model.anio + '/' + $scope.mes;
						
						$window.location.href = $scope.url;

					} else {
						$scope.msgVisible = true;
						$scope.msgType = "bg-danger";
						//$scope.msg = "Debe seleccionar Fecha Desde y Fecha Hasta.";
						$scope.msg = "Debe seleccionar Año y Mes.";
					}
				}
				
			},
			backdrop : 'static'
		});
	};
	
	$scope.buscarCategoriasProforma = function() {   	  	
		$http({
			url : 'services/ProformaCategoria',
			method : "GET",
			params : {
				searchKey : ""
			}
		}).then(function(response) {
			$scope.categoriasProforma = response.data;
			$scope.categoriaProformaSeleccionada = $scope.categoriasProforma[0];
		});
	}
	
	$scope.buscarCategoriasProforma();
	
	$scope.reporteProformaOpciones = function() {
		var $uibModalInstance = $uibModal.open({
			templateUrl : 'templates/reporteProformaTemplate.html',
			scope: $scope,
			controller : 
				
			function ($scope, $uibModal, $uibModalInstance) {
				
				$scope.cancel = function() {
					$uibModalInstance.close();
				};
				
				$scope.reporteProforma = function() {
					$window.location.href = 'services/Facturacion/Proforma/' + $scope.categoriaProformaSeleccionada.id;
				}
				
			},
			backdrop : 'static'
		});
	};
	
	$scope.reporteProformaEmail = function() {
		var msg;
		msg = "Se enviará el Reporte de Proformas via Correo Electrónico. ¿Desea continuar?";
		
		var modalOptions = {
			closeButtonText : 'Cancelar',
			actionButtonText : 'Sí',
			headerText : ':: Confirmación ::',
			bodyText : msg
		};

		confirmService.showModal({}, modalOptions)
				.then(function(result) {
					
					 $http.get('services/Facturacion/Proforma/Email').then(
							    function(response) {
							    	$scope.displayMsg("Se ha enviado el correo electrónico.", "info");
							    }
							  // Catch gives an error in eclipse editor.... but it works!
							  ).catch(
							    function(response) {
							    	$scope.displayMsg("Se ha producido un error al enviar el correo electrónico.", "danger");
							    }
							  );
				});
	};
	
	/*
	var date = Date.now();
	date.toLocaleDateString('es-AR');
	$scope.clock = date.now;
	$interval(function () { $scope.clock = Date.now().toLocaleDateString('es-AR'); }, 1000);
	*/
	
	/* Archivos AFIP */
	$scope.meses = ["Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octuber","Noviembre","Diciembre"];
	$scope.anios = [];
	
	$scope.model = {
		anio : null,
		mes : 'Enero'
	};
	
	$scope.buscarAnios = function() {   	  	
		$http({
			url : 'services/IndiceAnios',
			method : "GET"
		}).then(function(response) {
			$scope.anios.push.apply($scope.anios, response.data);
			$scope.model.anio = $scope.anios[0];
		});
	}
	
	$scope.buscarAnios();
	
	$scope.mes = 0;
	$scope.url = '';
	
	$scope.afipContadorOpciones = function() {
		var $uibModalInstance = $uibModal.open({
			templateUrl : 'templates/afipContadorTemplate.html',
			scope: $scope,
			controller : 
				
			function ($scope, $uibModal, $uibModalInstance) {
				
				$scope.cancel = function() {
					$uibModalInstance.close();
				};
				

				$scope.generarComprobantes = function() {
					$scope.mes = $scope.meses.indexOf($scope.model.mes) + 1;
			        if ($scope.mes.toString().length == 1) {
			        	$scope.mes = "0" + $scope.mes;
			        }
			        
					$scope.url = 'services/Facturacion/CompraVenta/Comprobantes/' + $scope.model.anio + '/' + $scope.mes;
					
					$window.location.href = $scope.url;
				}
				
				$scope.generarAlicuotas = function() {
					$scope.mes = $scope.meses.indexOf($scope.model.mes) + 1;
			        if ($scope.mes.toString().length == 1) {
			        	$scope.mes = "0" + $scope.mes;
			        }
					
					$scope.url = 'services/Facturacion/CompraVenta/Alicuotas/' + $scope.model.anio + '/' + $scope.mes;

					$window.location.href = $scope.url;
				}
				
			},
			backdrop : 'static'
		});
	};	
}]);

