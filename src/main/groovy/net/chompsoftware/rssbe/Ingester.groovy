package net.chompsoftware.rssbe


class Ingester {

    public static void main(String[] args) {
        RSSCollector collector = new RSSCollector()
        collector.readUrl('http://feeds.bbci.co.uk/news/rss.xml')
    }
}
    