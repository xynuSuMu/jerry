package web;

import com.alibaba.fastjson.JSONObject;
import context.JerryContext;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.modal.ParamModal;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-23 10:38
 */
public class WebApp1 {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public FullHttpResponse response(ParamModal modal) {
        //寻找Controller对象
        logger.info("URL:{}", modal.getUrl());
        logger.info("Method:{}", modal.getHttpMethod());
        logger.info("Param:{}", modal.getParam());
        //
        Object obj = JerryContext.getInstance().getBean(modal.getUrl());
        HttpResponseStatus status;
        if (obj == null) {
            status = HttpResponseStatus.NOT_FOUND;
        } else {
            status = HttpResponseStatus.OK;
        }
        //返回结果
        JSONObject responseJson = new JSONObject();
        responseJson.put("code", "400");
        Object o = responseJson;
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(o.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/json; charset=UTF-8");
        return response;
    }

}
