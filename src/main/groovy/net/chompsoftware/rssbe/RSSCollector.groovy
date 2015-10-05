package net.chompsoftware.rssbe

import groovyx.net.http.HTTPBuilder
import org.apache.http.client.HttpClient

import java.text.SimpleDateFormat

import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.Method.GET

class RSSCollector {

    static DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
    private HttpClient client

    RSSCollector(HttpClient client) {
        this.client = client
    }

    def readUrl(String feed) {
        def builder = new HTTPBuilder(feed)
        builder.client = client
        builder.request(GET, XML) { req ->

            response.success = { resp, rss ->
                def channelTitle = rss.channel.title
                def items = rss.channel.item
                List<Story> stories = items.collect {
                    new Story(
                            channelTitle: channelTitle,
                            title: it.title.toString(),
                            _id: it.guid.toString(),
                            link: it.link.toString(),
                            datePublished: DATE_FORMAT.parse(it.pubDate.toString())
                    )
                }
                return stories
            }

            response.'404' = { resp ->
                println resp
            }

        }
    }
}
