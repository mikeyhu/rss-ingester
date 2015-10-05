package net.chompsoftware.rssbe

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.impl.client.DefaultHttpClient

import static groovyx.net.http.ContentType.JSON

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
            def inserted = 0
            def stories = urls.collect {
                new RSSCollector(new DefaultHttpClient()).readUrl(it)
            }.flatten { story ->
                def builder = new HTTPBuilder('http://localhost:3000')
                builder.request(Method.POST, JSON) {
                    uri.path = '/stories'
                    body = story.toJson()
                    response.success = { resp, rss ->
                        if(resp.status == 201) {
                            inserted ++
                        }
                    }

                }
            }
            println "Inserted $inserted stories out of ${stories.size()} found."
            sleep FOR_10_MINS
        }

    }
}
