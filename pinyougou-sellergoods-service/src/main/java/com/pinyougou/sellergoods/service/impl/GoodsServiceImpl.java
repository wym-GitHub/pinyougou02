package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Transactional
@Service

public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    @Override
    public void add(Goods goods) {
        goods.getTbGoods().setAuditStatus("0");
        TbGoods tbGoods = goods.getTbGoods();
        goodsMapper.insert(tbGoods);

        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(tbGoods.getId());
        goodsDescMapper.insert(goodsDesc);

        saveItemList(goods);


    }

    private void setItemValus(Goods goods, TbItem tbItem) {
        tbItem.setGoodsId(goods.getTbGoods().getId());//设置商品id
        tbItem.setSellerId(goods.getTbGoods().getSellerId());//设置商家id
        tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());//商品分类id
        tbItem.setCreateTime(new Date());//设置商品录入时间
        tbItem.setUpdateTime(new Date());//设置修改日期
        //设置品牌名称
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
        tbItem.setBrand(tbBrand.getName());
        //分类名称
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
        tbItem.setCategory(tbItemCat.getName());
        //商家名称
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
        tbItem.setSeller(tbSeller.getNickName());
        //图片
        List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);

        if (maps.size() > 0) {
            tbItem.setImage((String) maps.get(0).get("url"));
        }
    }

    /**
     * 修改
     */

    @Override
    public void update(Goods goods) {

        goods.getTbGoods().setAuditStatus("0");

        goodsMapper.updateByPrimaryKey(goods.getTbGoods());
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //先删除原来的sku
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
        itemMapper.deleteByExample(tbItemExample);
        //然后插入修改后的sku
        saveItemList(goods);

    }

    public void saveItemList(Goods goods){
        if ("1".equals(goods.getTbGoods().getIsEnableSpec())) {
            for (TbItem tbItem : goods.getItemList()) {
                String title = goods.getTbGoods().getGoodsName();
                Map<String, Object> map = JSON.parseObject(tbItem.getSpec());
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                tbItem.setTitle(title);//设置标题
                setItemValus(goods, tbItem);
                itemMapper.insert(tbItem);
            }

        } else {
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods.getTbGoods().getGoodsName());
            tbItem.setSeller(goods.getTbGoods().getSellerId());
            tbItem.setStatus("1");//状态
            tbItem.setPrice(goods.getTbGoods().getPrice());
            tbItem.setIsDefault("1");
            tbItem.setNum(9999);
            tbItem.setSpec("{}");
            setItemValus(goods, tbItem);

            itemMapper.insert(tbItem);
        }

    }


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {

        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);

        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
        Goods goods = new Goods();
        goods.setGoodsDesc(tbGoodsDesc);
        goods.setItemList(tbItems);
        goods.setTbGoods(tbGoods);

        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");

            goodsMapper.updateByPrimaryKey(tbGoods);

//            TbGoods tbGoods1 = new TbGoods();
//            tbGoods1.setId(id);
//            tbGoods1.setIsDelete("1");
//            goodsMapper.updateByPrimaryKeySelective(tbGoods1);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }


        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public Result updateStatus(Long[] ids,String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
            TbItemExample tbItemExample=new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andGoodsIdIn(Arrays.asList(ids));
            List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
            for (TbItem tbItem : tbItems) {
                tbItem.setStatus(status);
                itemMapper.updateByPrimaryKey(tbItem);
            }


        }

        return null;
    }

    @Override
    public void updateIsMarketable(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = new TbGoods();
            tbGoods.setId(id);

            String status2= null;
            try {
                status2 = new String(status.getBytes("ISO-8859-1"),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            tbGoods.setIsMarketable(status2);
            goodsMapper.updateByPrimaryKeySelective(tbGoods);
        }

    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {
        if(ids.length==0){
            return null;
        }

        TbItemExample tbItemExample=new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(ids));
        TbItemExample.Criteria criteria1 = criteria.andStatusEqualTo(status);
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);



        return tbItems;
    }
}
