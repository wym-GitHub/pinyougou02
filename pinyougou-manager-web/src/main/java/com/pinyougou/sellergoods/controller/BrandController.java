package com.pinyougou.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    @RequestMapping("findAll")
    public List<TbBrand> findAll(){

        List<TbBrand> all = brandService.findAll();
        return all;

    }

    //按条件分页查询
    @RequestMapping("findPage")
    public PageResult findPage(@RequestBody TbBrand tbBrand, int pageNum, int pageSize){
        PageResult page = brandService.findPage(tbBrand, pageNum, pageSize);
        return page;
    }

    //品牌新增
    @RequestMapping("insert")
    public Result insert(@RequestBody TbBrand tbBrand){
        Result insert = brandService.insert(tbBrand);
        return insert;


    }
    //查询单个
    @RequestMapping("findById")
    public TbBrand findById(Long id){
        TbBrand byId = brandService.findById(id);
        return byId;

    }
    //修改操作
    @RequestMapping("update")
    public Result update(@RequestBody TbBrand tbBrand){
        Result update = brandService.update(tbBrand);
        return update;

    }

    //删除操作

    @RequestMapping("delete")

    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"删除失败");
        }

    }
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
