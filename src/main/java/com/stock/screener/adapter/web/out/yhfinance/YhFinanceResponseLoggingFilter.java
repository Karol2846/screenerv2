package com.stock.screener.adapter.web.out.yhfinance;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Provider
@ApplicationScoped
class YhFinanceResponseLoggingFilter implements ClientResponseFilter {

    private static final Logger LOG = Logger.getLogger(YhFinanceResponseLoggingFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        String rawJson = readInputStream(responseContext.getEntityStream());
        responseContext.setEntityStream(new ByteArrayInputStream(rawJson.getBytes(StandardCharsets.UTF_8)));

        String ticker = extractTicker(requestContext.getUri());
        persistLog(ticker, rawJson);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void persistLog(String ticker, String rawJson) {
        try {
            new YhFinanceResponseLog(ticker, rawJson).persist();
        } catch (Exception e) {
            LOG.errorf(e, "Failed to persist YH Finance response log for ticker: %s", ticker);
        }
    }

    static String extractTicker(URI uri) {
        String path = uri.getPath();
        String[] segments = path.split("/");
        return segments[segments.length - 1];
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
