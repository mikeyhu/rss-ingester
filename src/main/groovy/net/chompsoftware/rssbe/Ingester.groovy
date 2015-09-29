package net.chompsoftware.rssbe

import groovy.util.logging.Slf4j
import org.apache.http.impl.client.DefaultHttpClient

@Slf4j
class Ingester {

    static final long FOR_10_MINS = 1000 * 60 * 10

    public static void main(String[] args) {

        def urls = [
                'http://feeds.bbci.co.uk/news/rss.xml',
                'http://www.theguardian.com/uk/rss',
                'http://uk.engadget.com/rss.xml'
        ]

        while(true) {
            urls.collect {
                new RSSCollector(new DefaultHttpClient()).readUrl(it)
            }.flatten {
                log.info it.title
            }
            sleep FOR_10_MINS
        }

    }
}
