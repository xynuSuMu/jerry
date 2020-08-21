package server.http;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.StringUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-21 08:41
 */
public class JerryHttpMessage extends JerryHttpObject  implements HttpMessage {
    private HttpVersion version;
    private final HttpHeaders headers;

    protected JerryHttpMessage(HttpVersion version) {
        this(version, true);
    }

    protected JerryHttpMessage(HttpVersion version, boolean validateHeaders) {
        if (version == null) {
            throw new NullPointerException("version");
        } else {
            this.version = version;
            this.headers = new DefaultHttpHeaders(validateHeaders);
        }
    }

    public HttpHeaders headers() {
        return this.headers;
    }

    public HttpVersion getProtocolVersion() {
        return this.version;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(StringUtil.simpleClassName(this));
        buf.append("(version: ");
        buf.append(this.getProtocolVersion().text());
        buf.append(", keepAlive: ");
        buf.append(HttpHeaders.isKeepAlive(this));
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }

    public HttpMessage setProtocolVersion(HttpVersion version) {
        if (version == null) {
            throw new NullPointerException("version");
        } else {
            this.version = version;
            return this;
        }
    }

    void appendHeaders(StringBuilder buf) {
        Iterator i$ = this.headers().iterator();

        while(i$.hasNext()) {
            Map.Entry<String, String> e = (Map.Entry)i$.next();
            buf.append((String)e.getKey());
            buf.append(": ");
            buf.append((String)e.getValue());
            buf.append(StringUtil.NEWLINE);
        }

    }
}
