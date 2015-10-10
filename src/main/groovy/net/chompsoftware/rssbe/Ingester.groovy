package net.chompsoftware.rssbe

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.impl.client.DefaultHttpClient
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

import static groovyx.net.http.ContentType.JSON
import static org.quartz.CronScheduleBuilder.cronSchedule
import static org.quartz.JobBuilder.newJob
import static org.quartz.TriggerBuilder.newTrigger

@Slf4j
class Ingester implements Job {
    static final API_PORT = 3000
    static final API_BASE = baseUrl()

    static final URLS = [
            'http://feeds.bbci.co.uk/news/rss.xml',
            'http://www.theguardian.com/uk/rss',
            'http://uk.engadget.com/rss.xml'
    ]

    static final String EVERY_10_MINS = '0 0/10 * * * ?'

    public void execute(JobExecutionContext context) {
        log.info 'Starting job'
        def inserted = 0
        def failed = 0
        def stories = URLS.collect {
            new RSSCollector(new DefaultHttpClient()).readUrl(it)
        }.flatten { story ->

            try {
                def builder = new HTTPBuilder("$API_BASE:$API_PORT")
                builder.request(Method.POST, JSON) {
                    uri.path = '/stories'
                    body = story.toJson()
                    response.success = { resp, rss ->
                        if (resp.status == 201) inserted++
                    }
                }
            } catch (Exception e) {
                log.error "failed to ingest article: ${e.message}"
                failed ++
            }
        }
        log.info "Inserted $inserted stories out of ${stories.size()} found ($failed failed)"
    }

    public static void main(String[] args) {

        Scheduler scheduler = new StdSchedulerFactory().getScheduler()

        JobDetail job = newJob(Ingester.class)
                .withIdentity("ingester", "ingestGroup")
                .build()

        Trigger trigger = newTrigger()
                .withIdentity("ingestTrigger", "ingestGroup")
                .startNow()
                .withSchedule(cronSchedule(EVERY_10_MINS))
                .build()

        scheduler.scheduleJob job, trigger
        scheduler.start()
        log.info "Scheduler started with following cron schedule '$EVERY_10_MINS'"
    }

    public static String baseUrl() {
        def dockerHost = System.getenv("DOCKER_HOST") ?: "tcp://localhost:2376"
        def host = System.getenv()['RSSBE_API_SERVICE_HOST'] ?: (dockerHost =~ /\/\/(.*):/)[0][1]
        "http://$host"
    }
}
