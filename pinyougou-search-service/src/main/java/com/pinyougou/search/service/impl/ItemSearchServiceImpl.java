package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        if("".equals(searchMap.get("keywords"))){
            return null;
        }
      //处理关键词的空格
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        Map<String, Object> map = new HashMap<>();
        String category = (String) searchMap.get("category");
        List<String> list = searchCategoryList(searchMap);//分组查询获取,商品分类
        if(!"".equals(category)){
            //有分类名称
            map.putAll(searchBrandAndSpecList(category));
        }
            else{
           //没有分类名称,按照第一个查询;
            if(list.size()>0){
                map.putAll(searchBrandAndSpecList(list.get(0)));
            }

        }
        Map searchList = searchList(searchMap);
        map.putAll(searchList);

        map.put("categoryList", list);
        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list );
        solrTemplate.commit();


    }

    //高亮查询
    public Map searchList(Map searchMap) {
        Map map = new HashMap();
        HighlightQuery highlightQuery = new SimpleHighlightQuery();

        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//设置前缀
        highlightOptions.setSimplePostfix("</em>");//设置后缀
        highlightQuery.setHighlightOptions(highlightOptions); //设置高亮选项

        //设置查询条件,按关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        highlightQuery.addCriteria(criteria);

        //设置分类过滤查询
        if(!"".equals(searchMap.get("category"))){
                Criteria filtCriteria1 =new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filtCriteria1);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //品牌过滤查询
        if(!"".equals(searchMap.get("brand"))){
            Criteria filtCriteria1 =new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filtCriteria1);
            highlightQuery.addFilterQuery(filterQuery);
        }

        //规格过滤
        if(searchMap.get("spec")!=null){
            Map<String,String> spec = (Map) searchMap.get("spec");
            for (String key : spec.keySet()) {
                Criteria filtCriteria1 =new Criteria("item_spec_"+key).is(spec.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filtCriteria1);
                highlightQuery.addFilterQuery(filterQuery);
            }


        }
        //价格过滤
        if(!"".equals(searchMap.get("price"))){
            String price = (String) searchMap.get("price");
            String[] split = price.split("-");
            if(!"0".equals(split[0])){
                Criteria criteria1=new Criteria("item_price").greaterThanEqual(split[0]);//split[0]是字符串,会自动解析,不需要转成int
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1) ;
                highlightQuery.addFilterQuery(filterQuery);
            }
            if(!"*".equals(split[1])){
                Criteria criteria1=new Criteria("item_price").lessThanEqual(split[1]);//split[0]是字符串,会自动解析,不需要转成int
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1) ;
                highlightQuery.addFilterQuery(filterQuery);
            }

        }
        //排序

        if(!"".equals(searchMap.get("sort"))&&!"".equals(searchMap.get("sortField"))){
            if("ASC".equals(searchMap.get("sort"))){
                Sort orders = new Sort(Sort.Direction.ASC,"item_"+searchMap.get("sortField"));
                highlightQuery.addSort(orders);
            }
            if("DESC".equals(searchMap.get("sort"))){
                Sort orders = new Sort(Sort.Direction.DESC,"item_"+searchMap.get("sortField"));
                highlightQuery.addSort(orders);
            }
        }

        //设置分页
       Integer  pageNo = (Integer) searchMap.get("pageNo");
        if(pageNo==null){
            pageNo=1;//没有接受到当前页码,则设置为默认1
        }
        Integer  pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=20;//没有接受到当前页码,则设置为默认1
        }
        highlightQuery.setOffset((pageNo-1)*pageSize);//设置搜索起始下标
        highlightQuery.setRows(pageSize);


        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);

        List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();

        for (HighlightEntry<TbItem> entry : highlighted) {
            TbItem entity = entry.getEntity();//获得没有高亮的实体

            List<HighlightEntry.Highlight> highlights = entry.getHighlights();//获得高亮
            if (highlights.size() > 0) {
                for (HighlightEntry.Highlight highlight : highlights) {//每条记录可能对应多个域的高亮
                    List<String> snipplets = highlight.getSnipplets();//每个域可能存在多值
                    if (snipplets.size() > 0) {
                        for (String snipplet : snipplets) {
                            entity.setTitle(snipplet);
                        }
                    }


                }
            }


        }
        map.put("rows", highlightPage.getContent());
        map.put("totalPages", highlightPage.getTotalPages());//返回总页数
        map.put("total", highlightPage.getTotalElements());//返回总记录数
        return map;
    }

    //分组查询,查询分类名称
    public List<String> searchCategoryList(Map searchMap) {
        List list = new ArrayList();

        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);//设置查询条件
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");//按照category域,分组查询
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();

        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }


        return list;
    }

    public Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();

        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
            if(typeId!=null){
                List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
               map.put("brandList", brandList);
                List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
                map.put("specList", specList);
            }
        return map;

    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {

        Query query =new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);

        solrTemplate.delete(query);
        solrTemplate.commit();

    }
}
