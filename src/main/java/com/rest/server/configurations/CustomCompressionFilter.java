package com.rest.server.configurations;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

public class CustomCompressionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String acceptEncoding = httpRequest.getHeader("Accept-Encoding");
        if (acceptEncoding == null || !acceptEncoding.contains("gzip")) {
            chain.doFilter(request, response);
            return;
        }

        GZipServletResponseWrapper wrappedResponse = new GZipServletResponseWrapper(httpResponse);
        try {
            chain.doFilter(request, wrappedResponse);
        } finally {
            wrappedResponse.finishResponse();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}

    private static class GZipServletResponseWrapper extends HttpServletResponseWrapper {
        private GZIPOutputStream gzipOutputStream;
        private ServletOutputStream servletOutputStream;
        private boolean isClosed = false;

        public GZipServletResponseWrapper(HttpServletResponse response) throws IOException {
            super(response);
            response.addHeader("Content-Encoding", "gzip");
            response.addHeader("Vary", "Accept-Encoding");
            this.servletOutputStream = response.getOutputStream();
            this.gzipOutputStream = new GZIPOutputStream(servletOutputStream);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {}

                @Override
                public void write(int b) throws IOException {
                    if (!isClosed) {
                        gzipOutputStream.write(b);
                    }
                }

                @Override
                public void flush() throws IOException {
                    if (!isClosed) {
                        gzipOutputStream.flush();
                    }
                }

                @Override
                public void close() throws IOException {
                    finishResponse();
                }
            };
        }

        public void finishResponse() throws IOException {
            if (!isClosed) {
                isClosed = true;
                gzipOutputStream.finish();
                gzipOutputStream.close();
            }
        }
    }
}
