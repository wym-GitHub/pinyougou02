app.controller('baseController',function ($scope) {

    $scope.reloadList=function () {
        //刷新
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

        //复选框选中的id数组
        $scope.ids = [];
        $scope.check = function ($event, id) {

            if ($event.target.checked) {
                $scope.ids.push(id);
            } else {
                var index = $scope.ids.indexOf(id);
                $scope.ids.splice(index, 1);
            }
        }

        //分页控件
        $scope.paginationConf = {
            currentPage: 1,//当前页数
            totalItems: 10,//总记录数
            itemsPerPage: 10,

            perPageOptions: [5, 10, 20, 30],
            onChange: function () {

                //页面发生改变执行onchange,重新把当前页和显示条数发送给后端,进行分页查询
                $scope.reloadList();
            }
        }
    //全选

    $scope.selectAll=function ($event) {
        $scope.ids=[];
        var isAllChecked=$event.target.checked;
        if(isAllChecked){
            for(var i=0;i<$scope.list.length;i++){
                $scope.ids.push($scope.list[i].id);
            }
        }

    }

    //根据属性和值查询集合中是否有对应的记录
    $scope.searchObjectByKey=function (list,key,name) {

            for(var i=0;i<list.length;i++){
                if(list[i][key]==name){
                    return list[i];
                }

            }
            return null;
    }

})