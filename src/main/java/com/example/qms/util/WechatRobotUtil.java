package com.example.qms.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WechatRobotUtil {
    private static final Logger log = LoggerFactory.getLogger(WechatRobotUtil.class);

    @Value("${app.robot-webhook}")
    private String defaultWebhook;

    public void sendMessage(String webhook, String content) throws IOException {
        String targetWebhook = webhook != null ? webhook : defaultWebhook;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(targetWebhook);
            String body = String.format("{\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}", content);
            httpPost.setEntity(new StringEntity(body, "UTF-8"));
            httpPost.setHeader("Content-Type", "application/json");
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                log.info("机器人消息发送完成");
            }
        }
    }
}