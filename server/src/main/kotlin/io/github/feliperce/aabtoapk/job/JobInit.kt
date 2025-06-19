package io.github.feliperce.aabtoapk.job

import io.github.feliperce.aabtoapk.config.ServerConfig
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.ktor.server.application.*
import org.quartz.JobBuilder
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory

fun Application.initJobs() {
    val scheduler = StdSchedulerFactory.getDefaultScheduler()
    scheduler.start()

    val job = JobBuilder.newJob(RemoveCacheJob::class.java)
        .withIdentity("cacheRemoval", "cache")
        .build()

    val trigger = TriggerBuilder.newTrigger()
        .withIdentity("cacheRemoval", "cache")
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(ServerConfig.REMOVE_UPLOAD_HOUR_TIME)
                .repeatForever())
        .build()

    scheduler.scheduleJob(job, trigger)
}
