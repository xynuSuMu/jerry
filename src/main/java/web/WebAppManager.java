package web;

import com.alibaba.fastjson.JSONObject;
import context.JerryContext;
import handler.JerryHandlerMethod;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.modal.HttpResponseModal;
import server.modal.ParamModal;

import java.util.Map;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-23 10:38
 */
public class WebAppManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public FullHttpResponse response(ParamModal modal) {
        //寻找Controller对象
        logger.info("URL:{}", modal.getUrl());
        logger.info("Method:{}", modal.getHttpMethod());
        logger.info("Param:{}", modal.getParam());
        //TODO：开发拦截器链
//        for (Map.Entry<String, String> m: modal.getHttpJerryRequest().getHttpHeaders().entries()){
//            System.out.println(m.getKey());
//            System.out.println(m.getValue());
//        }
        //寻找RequestMapping对应的控制层方法
        HttpResponseModal httpResponseModal = JerryHandlerMethod.handlerRequestMethod(modal);
        //响应
        FullHttpResponse response;
        if (httpResponseModal.getResponseStatus() != HttpResponseStatus.OK || httpResponseModal.getO() == null) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseModal.getResponseStatus(),
                    Unpooled.copiedBuffer("Fail:" + httpResponseModal.getResponseStatus().code(), CharsetUtil.UTF_8));
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseModal.getResponseStatus(),
                    Unpooled.copiedBuffer(httpResponseModal.getO().toString(), CharsetUtil.UTF_8));
        }
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/json; charset=UTF-8");
        return response;
    }

}
