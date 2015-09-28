package net.chompsoftware.rssbe

import org.apache.http.impl.client.DefaultHttpClient


class Ingester {

    public static void main(String[] args) {

        def urls = [
                'http://feeds.bbci.co.uk/news/rss.xml',
                'http://www.theguardian.com/uk/rss',
                'http://uk.engadget.com/rss.xml'
        ]

        urls.collect {
            new RSSCollector(new DefaultHttpClient()).readUrl(it)
        }.flatten {
            println it.title
        }
    }
}
