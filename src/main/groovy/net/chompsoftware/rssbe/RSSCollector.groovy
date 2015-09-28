package net.chompsoftware.rssbe

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HTTPBuilder
import org.apache.http.client.HttpClient

import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.Method.GET

class RSSCollector {

    private HttpClient client

    RSSCollector(HttpClient client) {
        this.client = client
    }

    def readUrl(String feed) {
        def builder = new HTTPBuilder(feed)
        builder.client = client
        builder.request(GET, XML) { req ->

            response.success = { resp, rss ->
                def items = rss.channel.item
                List<Story> stories = items.collect { new Story(it) }
                return stories
            }

            response.'404' = { resp ->
                println resp
            }

        }
    }

    static String toJson(Story story) {
        ObjectMapper mapper = new ObjectMapper()
        mapper.writeValueAsString(story)
    }
}
