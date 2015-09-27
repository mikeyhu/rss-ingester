package net.chompsoftware.rssbe

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.text.SimpleDateFormat

@EqualsAndHashCode
@ToString
class Story {
    String title
    String id
    String link
    Date datePublished

    static format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")

    Story(item) {
        title = item.title.toString()
        id = item.guid.toString()
        link = item.link.toString()
        datePublished = format.parse item.pubDate.toString()
    }
}
