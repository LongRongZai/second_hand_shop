package test;

import com.shop.SecondHandShopApplication;
import com.shop.dao.mapperDao.CollectMapper;
import com.shop.dao.mapperDao.CommodityMapper;
import com.shop.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {SecondHandShopApplication.class})
public class UnitTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;
    @Resource
    CollectMapper collectMapper;
    @Resource
    CommodityMapper commodityMapper;


    @Test
    public void t_login() {


    }

}
