package webapp.app4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-24 11:14
 */

public class HttpResponse {

    public final String CRLF = "\r\n";
    public final String BLANK = " ";


    //返回正文的长度
    private int len;
    //返回状态行和请求头信息
    private StringBuilder head;

    //返回正文内容
    private StringBuilder content;

    //用于写到输出流中
    private BufferedWriter bw;


    private HttpResponse() {
        len = 0;
        content = new StringBuilder();
        head = new StringBuilder();

    }

    public HttpResponse(Socket s) {
        this();
        try {
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            head = null;
            e.printStackTrace();
        }


    }

    //构建正文
    public void print(String s) {
        content.append(s);
        len = content.toString().getBytes().length;


    }

    public void println(String s) {
        content.append(s).append(CRLF);
        len = content.toString().length();

    }

    /*
    private void createHeadInfo(int code){
        //1)  HTTP协议版本、状态代码、描述
        headInfo.append("HTTP/1.1").append(BLANK).append(code).append(BLANK);
        switch(code){
            case 200:
                headInfo.append("OK");
                break;
            case 404:
                headInfo.append("NOT FOUND");
                break;
            case 505:
                headInfo.append("SEVER ERROR");
                break;
        }
        headInfo.append(CRLF);
        //2)  响应头(Response Head)
        headInfo.append("Server:bjsxt Server/0.0.1").append(CRLF);
        headInfo.append("Date:").append(new Date()).append(CRLF);
        headInfo.append("Content-type:text/html;charset=GBK").append(CRLF);
        //正文长度 ：字节长度
        headInfo.append("Content-Length:").append(len).append(CRLF);
        headInfo.append(CRLF); //分隔符
    }
    //推送到客户端
    */
    private void createHeader(int code) {
        head.append("HTTP/1.1").append(BLANK).append(code).append(BLANK);
        switch (code) {
            case 200:
                head.append("OK");
                break;
            case 404:
                head.append("NOT FOUND");

        }
        head.append(CRLF);
        head.append("Server:tomcat").append(CRLF);
        head.append("Date:").append(new Date()).append(CRLF);
        head.append("Content-type:text/html;charset=GBK").append(CRLF);
        head.append("Content-Length:").append(len).append(CRLF);
        head.append(CRLF);


    }

    public void flush(int code) throws IOException {

        createHeader(code);
        bw.write(head.toString());
        bw.write(content.toString());
        bw.flush();


    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}


