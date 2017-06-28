package com.stubbornjava.common.seo;

import static com.stubbornjava.common.undertow.handlers.CustomHandlers.timed;

import com.stubbornjava.common.undertow.Exchange;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class InMemorySitemapRoutes {
    private final InMemorySitemap sitemap;
    private InMemorySitemapRoutes(InMemorySitemap sitemap) {
        this.sitemap = sitemap;
    }

    public void getSitemap(HttpServerExchange exchange) {
        String sitemapName = Exchange.pathParams().pathParam(exchange, "sitemap").orElse(null);
        String content = sitemap.getIndex(sitemapName);
        if (null == content) {
            exchange.setStatusCode(404);
            Exchange.body().sendText(exchange, "Sitemap doesn't exist");
            return;
        }
        Exchange.body().sendXml(exchange, content);
    }

    public static RoutingHandler router(InMemorySitemap sitemap) {
        InMemorySitemapRoutes routes = new InMemorySitemapRoutes(sitemap);
        RoutingHandler router = new RoutingHandler()
            .get("/sitemaps/{sitemap}", timed("getSitemap", routes::getSitemap))
        ;
        return router;
    }
}
