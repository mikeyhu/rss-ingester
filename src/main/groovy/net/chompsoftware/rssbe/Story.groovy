package net.chompsoftware.rssbe

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.text.SimpleDateFormat

@EqualsAndHashCode
@ToString
class Story {
    private String title
    private String id
    private String link
    private Date datePublished

    static format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")

    Story(item) {
        title = item.title.toString()
        id = item.guid.toString()
        link = item.link.toString()
        datePublished = format.parse item.pubDate.toString()
    }

    String getTitle() {
        return title
    }

    String getId() {
        return id
    }

    String getLink() {
        return link
    }

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
    Date getDatePublished() {
        return datePublished
    }
}
