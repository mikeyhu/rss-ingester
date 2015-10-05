package net.chompsoftware.rssbe

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.text.SimpleDateFormat

@EqualsAndHashCode
@ToString
class Story {
    String channelTitle
    String title
    String _id
    String link
    private Date datePublished

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    Date getDatePublished() {
        return datePublished
    }

    String toJson() {
        ObjectMapper mapper = new ObjectMapper()
        mapper.writeValueAsString(this)
    }
}
