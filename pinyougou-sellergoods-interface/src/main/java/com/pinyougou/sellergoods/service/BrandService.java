package com.pinyougou.sellergoods.service;


import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAll();

    //按条件,分页,查询
    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

    //品牌新增
    public Result insert(TbBrand tbBrand);

    //品牌修改
    public Result update(TbBrand tbBrand);

    //查询byid
    public TbBrand findById(Long id);

    //删除
    public void delete(Long[] ids);

    public List<Map> selectOptionList();

}
