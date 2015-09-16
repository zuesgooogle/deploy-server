package com.simplegame.deploy.network.http;

import static io.netty.handler.codec.http.HttpHeaderNames.CACHE_CONTROL;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.DATE;
import static io.netty.handler.codec.http.HttpHeaderNames.EXPIRES;
import static io.netty.handler.codec.http.HttpHeaderNames.LAST_MODIFIED;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.simplegame.core.SpringApplicationContext;
import com.simplegame.deploy.bus.file.config.FileServerConfig;
import com.simplegame.deploy.bus.file.service.IFileProgressiveService;

/**
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年9月14日 下午3:29:54
 * 
 */
public class FileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger LOG = LogManager.getLogger(getClass());
    
    private FileServerConfig fileServerConfig;
    
    private IFileProgressiveService fileProgressiveService;
    
    public FileServerHandler() {
        fileServerConfig = SpringApplicationContext.getApplicationContext().getBean(FileServerConfig.class);
        fileProgressiveService = SpringApplicationContext.getApplicationContext().getBean(IFileProgressiveService.class);
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // bad request
        if (!request.decoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        // only support get method
        if (request.method() != GET) {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }
        
        final String agentId = "1";
        
        StringBuffer buf = new StringBuffer();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        if (!params.isEmpty()) {
            for (Entry<String, List<String>> p: params.entrySet()) {
                String key = p.getKey();
                List<String> vals = p.getValue();
                for (String val : vals) {
                    buf.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
                }
            }
            buf.append("\r\n");
        }
        LOG.info("uri: {}, params: {}", queryStringDecoder.path(), buf.toString());

        final String path = checkUri(queryStringDecoder.path());
        if (null == path) {
            sendError(ctx, FORBIDDEN);
            return;
        }

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            sendError(ctx, NOT_FOUND);
            return;
        }

        // not modified check

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException ignore) {
            sendError(ctx, NOT_FOUND);
            return;
        }
        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

        /**
         * -----------------------------------
         * set header
         * -----------------------------------
         */
        setContentLength(response, fileLength);
        setContentType(response, file);
        setDateAndCache(response, file);
        // keep alive
        if( isKeepAlive(request) ) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        
        // send header
        ctx.write(response);
        
        fileProgressiveService.initProgress(agentId, file.getName(), fileLength);
        
        // send data
        ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
        
        // write the end marker.
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        
        //传输状态
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) { // total unknown
                    //System.err.println(future.channel() + " Transfer progress: " + progress);
                } else {
                    //System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
                    
                    
                    //String percent = df.format( ((double)progress / (double)total * 100) );
                    //LOG.info("channel: {}, transfer percent: {}", future.channel(), percent );
                    
                    fileProgressiveService.updateProgress(agentId, progress);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) {
                LOG.info("channel: {}, transfer complete.", future.channel());
            }
        });

        if (!isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        
    }
    
    private void setDateAndCache(HttpResponse response, File file) {
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));

        // Cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        
        time.setTimeInMillis(file.lastModified());
        response.headers().set(LAST_MODIFIED, dateFormatter.format(time.getTime()));
    }
    
    private void setContentLength(HttpResponse response, long length) {
        response.headers().setLong(HttpHeaderNames.CONTENT_LENGTH, length);
    }

    private void setContentType(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }

    private boolean isKeepAlive(HttpMessage message) {
        CharSequence connection = message.headers().get(HttpHeaderNames.CONNECTION);
        if (connection != null && HttpHeaderValues.CLOSE.equalsIgnoreCase(connection)) {
            return false;
        }

        if (message.protocolVersion().isKeepAliveDefault()) {
            return !HttpHeaderValues.CLOSE.equalsIgnoreCase(connection);
        } else {
            return HttpHeaderValues.KEEP_ALIVE.equalsIgnoreCase(connection);
        }
    }
    
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private String checkUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return null;
        }

        uri = uri.replace('/', File.separatorChar);

        // 非法URI 地址
        if (URI_REGIX.matcher(uri).matches()) {
            return null;
        }

        return fileServerConfig.getDirectory() + File.separator + uri;
    }

    private static final Pattern URI_REGIX = Pattern.compile(".*[<>&\"].*");
    
    public static final String HTTP_DATE_FORMAT = "yyy-MM-dd HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    
    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.CHINA);
    
    public static final DecimalFormat df = new DecimalFormat("#.00");  

}
