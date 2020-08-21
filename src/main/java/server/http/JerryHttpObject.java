package server.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpObject;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-21 08:40
 */
public class JerryHttpObject implements HttpObject {
    private DecoderResult decoderResult;

    protected JerryHttpObject() {
        this.decoderResult = DecoderResult.SUCCESS;
    }

    public DecoderResult getDecoderResult() {
        return this.decoderResult;
    }

    public void setDecoderResult(DecoderResult decoderResult) {
        if (decoderResult == null) {
            throw new NullPointerException("decoderResult");
        } else {
            this.decoderResult = decoderResult;
        }
    }
}

