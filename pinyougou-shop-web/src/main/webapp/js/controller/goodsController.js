 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location,goodsService,itemCatService,typeTemplateService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id=$location.search()['id'];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html($scope.entity.goodsDesc.introduction);
				//显示图片
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//显示商品的规格属性
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);

				//显示sku
				for(var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse( $scope.entity.itemList[i].spec);
				}
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
        $scope.entity.goodsDesc.introduction=editor.html();
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){

					alert(response.message)
		        	$scope.entity={};
					editor.html("");
					location.href='goods.html';

				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//新增
	$scope.add=function () {

        $scope.entity.goodsDesc.introduction=editor.html();

        goodsService.add($scope.entity).success(
        	function (response) {
				if(response.success){
					alert("保存成功")
					$scope.entity={};
					editor.html('')//清空富文本
				}else{

					alert(response.message);
				}
            }
		)
    }


    //图片上传
	$scope.upload=function () {
        uploadService.upload().success(
        	function (response) {
        		if (response.success){
        			$scope.image_entity.url=response.message;
                }else
				{
					alert(response.message);
				}
            }
		)
    }

   $scope.add_image_entity=function () {
			$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
   }

    $scope.dele_image_entity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }


	//分类一级列表
		$scope.entity={tbGoods:{},goodsDesc:{customAttributeItems:[],specificationItems:[],itemImages:[]}};
	$scope.selectItemCat1List=function () {

        itemCatService.findByParentId(0).success(
        	function (response) {
				$scope.itemCat1List=response;
                $scope.itemCat2List=[];
                $scope.itemCat3List=[];
            }
		)
    }
    //分类二级列表
	$scope.$watch('entity.tbGoods.category1Id',function (newvalue,oldvalue) {

		if(newvalue==undefined){
			return;
		}
        itemCatService.findByParentId(newvalue).success(
        	function (response) {
				$scope.itemCat2List=response;
                $scope.itemCat3List=[];
            }
		)

    })
	//分类三级列表
	$scope.$watch('entity.tbGoods.category2Id',function (newvalue,oldvalue) {
        if(newvalue==undefined){
            return;
        }
        itemCatService.findByParentId(newvalue).success(
        	function (response) {
                $scope.itemCat3List=response;
            }
		)
    })

    //查询模板id
    $scope.$watch('entity.tbGoods.category3Id',function (newvalue,oldvalue) {
        if(newvalue==undefined){
            return;
        }
        itemCatService.findOne(newvalue).success(
            function (response) {
                $scope.entity.tbGoods.typeTemplateId=response.typeId;
            }
        )
    })
    //查询模板表获得对应的品牌
    $scope.$watch('entity.tbGoods.typeTemplateId',function (newvalue,oldvalue) {
        if(newvalue==undefined){
            return;
        }
        typeTemplateService.findOne(newvalue).success(
            function (response) {
                $scope.typeTemplateBrandList=JSON.parse(response.brandIds);

                //从模板表中查出,扩展属性名称,添加扩展属性值后,存到goodsDesc表中的customAttributeItems
				if($location.search()['id']==null){
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems);

                }
            }
        )

        typeTemplateService.findSpecList(newvalue).success(

        	function (response) {
				$scope.specList=response;
            }

		)

    })

	//保存选中的规格选项

	//首先定义一个规格集合

	$scope.updateSpecAttribute=function ($event,name,value) {
		var specItems=$scope.entity.goodsDesc.specificationItems;

        var object=$scope.searchObjectByKey(specItems,'attributeName',name);

        if(object!=null){

        	//不为null,代表specificationItems已经有了规格名称这一栏
        	if($event.target.checked){
                alert(object);
        		//选中状态,往规格里面添加,属性值
                object.attributeValue.push(value);
			}
			else{
        		//取消选中,从属性值集合移除,当前取消选中属性值

                object.attributeValue.splice(object.attributeValue.indexOf(value),1);

                if(object.attributeValue.length==0){
                	//只有规格么有属性值,则把这一规格对象从specificationItems移除
                    specItems.splice(specItems.indexOf(object),1);
				}
			}


		}
		else{
            specItems.push({'attributeName':name,'attributeValue':[value]});

		}
    }

		//商品录入之sku商品信息

	$scope.createItemList=function () {
        var specItems=$scope.entity.goodsDesc.specificationItems;

		$scope.entity.itemList=[{price:0,status:0,isDefault:0,num:9999,spec:{}}]

		for(var i=0;i<specItems.length;i++){

            $scope.entity.itemList=addColumn($scope.entity.itemList,specItems[i].attributeName,specItems[i].attributeValue);
		}

    }

    addColumn=function (list,attributeName,attributeValues) {
		var newList=[];

		for(var i=0;i<list.length;i++){
			var oldRow=list[i];
			for(var j=0;j<attributeValues.length;j++){
				var newRow=JSON.parse(JSON.stringify(oldRow));
				newRow.spec[attributeName]=attributeValues[j];
                newList.push(newRow);

			}

		}
		return newList

    }

    $scope.status=['未审核','已审核','审核通过','关闭'];

	//显示商品分类
	$scope.itemCatList=[];

	$scope.findItemCatList=function () {

        itemCatService.findAll().success(
        	function (response) {
				for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;


				}

            }
		)
    }

    //根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function (specName,optionName) {
		var specItems=$scope.entity.goodsDesc.specificationItems;

        var object=$scope.searchObjectByKey(specItems,'attributeName',specName);
        if(object==null){
        	return false;
		}
		else{
        	if(object.attributeValue.indexOf(optionName)>=0){
        		return true;
			}
			else{
        		return false;
			}

		}
    }

    //上下架

	$scope.updateIsMarketable=function (isMarketable) {

        goodsService.updateIsMarketable($scope.ids,isMarketable).success(
        	function (response) {
				if(response.success){
					$scope.reloadList();
				}
				else{
					alert(response.message);
				}
            }
		)
    }
    
});	
