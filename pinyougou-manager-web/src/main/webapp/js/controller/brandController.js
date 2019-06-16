app.controller('brandController',function ($scope,$controller,brandService) {

        //继承
    $controller('baseController',{$scope:$scope});




    //分页查询
    //首先定义一个对象封装,提交的查询条件
    $scope.conditions={};
    $scope.search=function (pageNum,pageSize) {

        brandService.findPage(pageNum,pageSize,$scope.conditions).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        )

    }

    //品牌新增
    $scope.save=function () {
        var serviceObject;
        if($scope.entity.id!=null){
            serviceObject=brandService.update($scope.entity);
        }
        else{
            serviceObject=brandService.insert($scope.entity);
        }
        serviceObject.success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }

            }
        )

    }

    //修改操作,点击修改,从数据库查出该id对应得信息,填到表格中
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;

            }
        )
    }

    //删除操作

    $scope.delete=function () {
        brandService.delete($scope.ids).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.ids=[];
                }
                else{
                    alert(response.message);
                }
            }
        )
    }
})