app.controller('indexController',function ($scope,indexService) {

    $scope.getSellerName=function () {

        indexService.getSellerName().success(
            function (response) {
                if(response.success){
                    $scope.sellerName=response.message;

                }
            }
        )
    }

})