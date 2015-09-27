package net.chompsoftware.rssbe

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.Method.GET

class RSSCollector {

    def readUrl(String feed) {
        def builder = new HTTPBuilder(feed)
        builder.request(GET, XML) { req ->

            response.success = { resp, rss ->
                def items = rss.channel.item
                List<Story> stories = items.collect { new Story(it) }
                stories.each { println toJson(it) }
            }

            response.'404' = { resp ->
                println resp
            }

        }
    }

    String toJson(Story story) {
        ObjectMapper mapper = new ObjectMapper()
        mapper.writeValueAsString(story)
    }
}
