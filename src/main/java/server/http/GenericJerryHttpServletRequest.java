package server.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import server.enume.ContentType;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-20 14:47
 */
public class GenericJerryHttpServletRequest implements JerryHttpServletRequest {

    private static final HttpDataFactory factory =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private HttpPostRequestDecoder postRequestDecoder;

    private HttpObject httpObject;
    private HttpRequest request;
    private FullHttpRequest fullHttpRequest;
    //参数
    private Map<String, String> params = new HashMap<>();
    //参数名
    private List<String> list = new ArrayList<>();
    //content
    private ByteBuf byteBuf;
    //contentType
    private String contentType;
    //是否开启https
    private Boolean isSSl;
    //文件
    private List<FileUpload> fileUploads = new CopyOnWriteArrayList<>();

    public GenericJerryHttpServletRequest(HttpObject httpObject, boolean isSSl) throws IOException {
        this.httpObject = httpObject;
        this.request = (HttpRequest) httpObject;
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        decoder.parameters().entrySet().forEach(entry -> {
            list.add(entry.getKey());
            params.put(entry.getKey(), entry.getValue().get(0));
        });
        this.isSSl = isSSl;
        contentType = request.headers().get(HttpHeaders.Names.CONTENT_TYPE);
        if (request.getMethod() != HttpMethod.GET) {
            handlerContentType();
        }
    }


    @Override
    public HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public HttpHeaders headers() {
        return request.headers();
    }

    @Override
    public Boolean isSSL() {
        return isSSl;
    }

    @Override
    public List<FileUpload> getFileUpload() {
        return fileUploads;
    }

    @Override
    public String getProtocol() {
        return request.getProtocolVersion().protocolName();
    }

    @Override
    public String getHost() {
        return request.getUri();
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public String getUri() {
        String url = request.getUri();
        if (url.contains("?")) {
            url = url.split("\\?")[0];
        }
        return url;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {

    }

    @Override
    public String getParameter(String paramName) {
        return params.get(paramName);
    }

    @Override
    public List<String> getParameterNames() {
        return list;
    }

    @Override
    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    private void handlerContentType() throws IOException {
        if (contentType.contains(ContentType.Type.FORM_DATA.getTypeName())) {//上传文件
            postRequestDecoder = new HttpPostRequestDecoder(factory, request);
            InterfaceHttpData data;
            while (postRequestDecoder.hasNext() && (data = postRequestDecoder.next()) != null) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) { // 获得文件数据
                    FileUpload fileUpload = (FileUpload) data;
                    if (fileUpload.isCompleted()) {
                        fileUploads.add(fileUpload);
                    }
                } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {//文本数据
                    Attribute attribute = (Attribute) data;
                    params.put(attribute.getName(), attribute.getValue());
                }
            }
        } else if (contentType.contains(ContentType.Type.JSON.getTypeName())) {//json格式
            this.fullHttpRequest = (FullHttpRequest) httpObject;
            byteBuf = fullHttpRequest.content();
        } else if (contentType.contains(ContentType.Type.FORM.getTypeName())) {
            List<InterfaceHttpData> datas = postRequestDecoder.getBodyHttpDatas();
            for (InterfaceHttpData data : datas) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    Attribute attribute = (Attribute) data;

                    params.put(attribute.getName(), attribute.getValue());

                }
            }
        }
    }
}
