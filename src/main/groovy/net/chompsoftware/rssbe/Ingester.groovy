package net.chompsoftware.rssbe

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.impl.client.DefaultHttpClient

import static groovyx.net.http.ContentType.JSON

@Slf4j
class Ingester {
    static final API_PORT = 3000
    static final API_BASE = baseUrl()

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
                def builder = new HTTPBuilder("$API_BASE:$API_PORT")
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
            log.info "Inserted $inserted stories out of ${stories.size()} found."
            sleep FOR_10_MINS
        }

    }

    public static String baseUrl() {
        def dockerHost = System.getenv("DOCKER_HOST") ?: "tcp://localhost:2376"
        def host = System.getenv()['RSSBE_API_SERVICE_HOST'] ?: (dockerHost =~ /\/\/(.*):/)[0][1]
        "http://$host"
    }
}
