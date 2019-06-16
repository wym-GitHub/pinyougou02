package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;
    //查询所有品牌
    @Override
    public List<TbBrand> findAll() {
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);
        return tbBrands;
    }

    //按条件分页查询

    @Override
    public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize );

        TbBrandExample tbBrandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
        //判断前端提交的数据,是否为空
        if(tbBrand!=null){
            if(tbBrand.getName()!=null&&tbBrand.getName().length()>0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }

        Page<TbBrand> tbBrands = (Page<TbBrand>) tbBrandMapper.selectByExample(tbBrandExample);

        return new PageResult(tbBrands.getTotal(),tbBrands.getResult());
    }

    //品牌新增
    @Override
    public Result insert(TbBrand tbBrand) {

        try {
            tbBrandMapper.insert(tbBrand);
            return new Result(true,"新增成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"新增失败");
        }


    }

    @Override
    public Result update(TbBrand tbBrand) {

        try {
            tbBrandMapper.updateByPrimaryKey(tbBrand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @Override
    public TbBrand findById(Long id) {
        TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(id);

        return tbBrand;
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);

        }
    }

    @Override
    public List<Map> selectOptionList() {

        return tbBrandMapper.selectOptionList();
    }
}
