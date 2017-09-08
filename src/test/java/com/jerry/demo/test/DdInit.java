package com.jerry.demo.test;

import com.xuanwu.flowengine.factory.ProcessEngineFactory;
import org.activiti.engine.ProcessEngine;
import org.junit.Assert;
import org.junit.Test;

import java.sql.*;

/**
 * File created on 2017/5/4.
 *
 * @author jkun
 */
public class DdInit {
    public static void main(String[] args) {
        /*int index = 1009200;

        while(index < 1009300) {
            Long tenant = Long.valueOf(index);
            Long productCode = Long.valueOf(index * 1000);

            try {
                ProcessEngine engine = ProcessEngineFactory.getProcessEngine(tenant, productCode, 1l, 1l);
                System.out.println(engine);
            } catch (Exception e) {
                e.printStackTrace();
            }

            index++;
        }*/

        //checkData();

        try {
            ProcessEngine engine = ProcessEngineFactory.getProcessEngine(1008200l, 834683221240184837l, 1l, 1l);
            System.out.println(engine);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void checkData() {
        String urltemplate = "jdbc:postgresql://172.16.0.143/tenant_%d?user=postgres&password=123456&allowMultiQueries=true&stringtype=unspecified";
        String username = "postgres";
        String password = "123456";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String sql = "select resource_name_ from act_re_procdef order by version_ desc limit 1 ";

        for (int i = 1009202; i < 1009300; i++) {
            try {
                String url = String.format(urltemplate, i);
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection(url, username, password);
                statement = conn.prepareStatement(sql);
                resultSet = statement.executeQuery();
                if(null != resultSet) {
                    while(resultSet.next()) {
                        System.out.println("tenantCode:" + i);
                        System.out.println(resultSet.getString(1));
                        Assert.assertEquals("expense" + i + ".bpmn20.xml", resultSet.getString(1));
                    }
                }
            } catch (Exception ex) {

            } finally {
                if(null != conn) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (null != resultSet) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
