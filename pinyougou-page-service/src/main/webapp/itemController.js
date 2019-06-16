app.controller('itemController',function ($scope) {

        //数量操作
    $scope.addNum=function (x) {
        $scope.num=$scope.num+x;
        if( $scope.num<1){
            $scope.num=1;
        }
    }

    $scope.specificationItems={};
    //规格选择,点击规格时,把规格名称和具体的规格值,传入;
    $scope.selectSpecification=function (name,value) {
        $scope.specificationItems[name]=value;
        searchSku();
    }

    //判断规格是否被选中
   $scope.isSelected=function (name,value) {
       if( $scope.specificationItems[name]==value){
           return true;
       }else{
           return false;
       }
   }

   //加载默认的sku列表
    $scope.loadSku=function () {
        $scope.sku=skuList[0];
        $scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
    }

    //匹配两个对象

    matchObject=function (map1,map2) {

        for(var k in map1){

            if(map1[k]!=map2[k]){
                    return false;
            }
        }
        for(var k in map2){

            if(map1[k]!=map2[k]){
                return false;
            }
        }
        return true;
    }

    //查询sku

    searchSku=function (){
        for(var i=0;i<skuList.length;i++){
            if(matchObject(skuList[i].spec,$scope.specificationItems)){
                $scope.sku=skuList[i];
                return;
            }

        }
        $scope.sku={id:0,title:'--------',price:0};//没有匹配
    }

})