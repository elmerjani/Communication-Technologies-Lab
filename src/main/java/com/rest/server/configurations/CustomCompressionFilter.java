package com.rest.server.configurations;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class CustomCompressionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();

        // Désactiver la compression pour les requêtes GraphQL
        if (uri.startsWith("/graphql")) {
            chain.doFilter(request, response);
            return;
        }

        // Appliquer la compression pour les autres endpoints
        GZipServletResponseWrapper wrappedResponse = new GZipServletResponseWrapper(httpResponse);
        chain.doFilter(request, wrappedResponse);
        wrappedResponse.finishResponse();
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}

    private static class GZipServletResponseWrapper extends HttpServletResponseWrapper {
        private GZIPOutputStream gzipOutputStream;
        private ServletOutputStream servletOutputStream;

        public GZipServletResponseWrapper(HttpServletResponse response) throws IOException {
            super(response);
            response.addHeader("Content-Encoding", "gzip");
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
                    gzipOutputStream.write(b);
                }

                @Override
                public void flush() throws IOException {
                    gzipOutputStream.flush();
                }

                @Override
                public void close() throws IOException {
                    gzipOutputStream.close();
                }
            };
        }

        public void finishResponse() throws IOException {
            gzipOutputStream.finish();
            gzipOutputStream.close();
        }
    }
}
