package com.rlc.test;

import akka.actor.Address;
import com.rlc.test.config.LogDeal;
import com.typesafe.config.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by renlc on 2016/7/6.
 */
public class AkkaConfigTest {


    public static void main(String[] args) {
        Config config = ConfigFactory.load("testConfig");
//        List<Config> list = (List<Config>) config.getConfigList("kafkajobs");
//        System.out.println(list.get(0).getString("address"));
        ConfigObject object = config.getObject("kafkajobs");
        for (String str : object.keySet()) {
            System.out.println(str);
            System.out.println("kafkajobs." + str + ".kafka.host" + "=" + config.getString("kafkajobs." + str + ".kafka.host"));
            ConfigList configlist = config.getList("kafkajobs." + str + ".client");
            for(ConfigValue value : configlist){
                System.out.println(value.atKey("client").getString("client.name"));
                System.out.println(value.atKey("client").getString("client.logtype"));
                System.out.println(value.atKey("client").getString("client.domains"));
                try {
                    LogDeal deal = LogDeal.class.cast(Class.forName(value.atKey("client").getString("client.class")).newInstance());
                    deal.dealLog();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            System.out.println();
        }
    }
}
