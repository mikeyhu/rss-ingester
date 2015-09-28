package net.chompsoftware.rssbe

import co.freeside.betamax.Betamax
import co.freeside.betamax.Recorder
import co.freeside.betamax.TapeMode
import co.freeside.betamax.httpclient.BetamaxRoutePlanner
import co.freeside.betamax.tape.yaml.OrderedPropertyComparator
import co.freeside.betamax.tape.yaml.TapePropertyUtils
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.junit.Rule
import org.yaml.snakeyaml.introspector.Property
import spock.lang.Specification

import java.time.Instant

class RssCollectorTests extends Specification {

    def setup() {
        TapePropertyUtils.metaClass.sort = { Set<Property> properties, List<String> names ->
            new LinkedHashSet(properties.sort(true, new OrderedPropertyComparator(names)))
        }
    }

    @Rule
    public Recorder recorder = new Recorder();

    @Betamax(tape = "it_should_read_a_BBC_RSS_feed", mode = TapeMode.READ_ONLY)
    def "It should read a BBC RSS feed"() {
        given:
        HttpClient client = new DefaultHttpClient()
        BetamaxRoutePlanner.configure(client)

        def collector = new RSSCollector(client)

        when:
        def stories = collector.readUrl 'http://feeds.bbci.co.uk/news/rss.xml'

        then:
        stories.size() == 52
        stories[0].title == 'Divisions on Syria laid bare at UN'
        stories[0].datePublished == Date.from(Instant.parse("2015-09-28T18:14:21.00Z"))
        stories[0].id == "http://www.bbc.co.uk/news/world-middle-east-34378889"
        stories[0].link == "http://www.bbc.co.uk/news/world-middle-east-34378889#sa-ns_mchannel=rss&ns_source=PublicRSS20-sa"
    }


}
