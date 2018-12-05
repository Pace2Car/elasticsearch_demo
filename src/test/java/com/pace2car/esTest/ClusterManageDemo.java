package com.pace2car.esTest;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;

/**
 * es集群管理测试
 * @author Pace2Car
 * @date 2018/12/5 11:14
 */
public class ClusterManageDemo {

    // ES集群
    private Settings settings = null;
    // ES服务器的客户端
    private TransportClient client = null;

    @Before
    public void setUp() throws Exception {
        //初始化、创建实例
        settings = Settings.builder().put("cluster.name", "my-application").build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.44.128"), 9300));
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    /**
     * 集群管理
     */
    @Test
    public void testClusterManage() {
        ClusterHealthResponse healths = client.admin().cluster().prepareHealth().get();
        //获取集群名称
        String clusterName = healths.getClusterName();
        System.out.println("clusterName" + ": " + clusterName);
        //获取数据节点数
        int numberOfDataNodes = healths.getNumberOfDataNodes();
        System.out.println("numberOfDataNodes" + ": " + numberOfDataNodes);
        //获取节点数
        int numberOfNodes = healths.getNumberOfNodes();
        System.out.println("numberOfNodes" + ": " + numberOfNodes);

        for (ClusterIndexHealth health : healths.getIndices().values()) {
            //获取索引
            String index = health.getIndex();
            //获取分片数
            int numberOfShards = health.getNumberOfShards();
            //获取副本数
            int numberOfReplicas = health.getNumberOfReplicas();
            //获取健康状况
            ClusterHealthStatus healthStatus = health.getStatus();
            System.out.println("index" + ": " + index + "\n"
                    + "\tnumberOfShards" + ": " + numberOfShards + ",\n"
                    + "\tnumberOfReplicas" + ": " + numberOfReplicas + ",\n"
                    + "\thealthStatus" + ": " + healthStatus);
        }
    }
}
