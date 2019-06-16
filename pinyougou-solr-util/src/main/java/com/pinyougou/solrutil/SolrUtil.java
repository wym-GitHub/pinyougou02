package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void import2Solr() {
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo("1");//已审核

        int total = itemMapper.countByExample(tbItemExample);
        int ceil = (int) Math.ceil(total * 1.0 / 100);
        System.out.println("总记录数:" + total);
        System.out.println("总页数:" + ceil);

        for (int i = 1; i <= ceil; i++) {
            PageHelper.startPage(i, 100);
            List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
            for (TbItem item : tbItems) {

                Map map = JSON.parseObject(item.getSpec(), Map.class);
                item.setSpecMap(map);

            }
            solrTemplate.saveBeans(tbItems);

        }
        System.out.println("结束");
    }

    public static void main(String[] args) {
        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) app.getBean("solrUtil");
        solrUtil.import2Solr();
    }

}
