package com.ezreal.rpc.core.common;

import java.io.Serializable;

import static com.ezreal.rpc.core.common.constants.RpcConstants.MAGIC_NUMBER;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class RpcProtocol implements Serializable {

    private static final long serialVersionUID = 1198176706484490294L;

    private final short magicNumber = MAGIC_NUMBER;

    private int contentLength;

    private byte[] content;

    public RpcProtocol() {
    }

    public RpcProtocol(byte[] content) {
        this.content = content;
        this.contentLength = content.length;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
