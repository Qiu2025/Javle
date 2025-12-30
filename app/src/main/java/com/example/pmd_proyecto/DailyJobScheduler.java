package com.example.pmd_proyecto;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import java.util.concurrent.TimeUnit;

public class DailyJobScheduler {

    private static final int JOB_ID = 1001;

    public static void schedule(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (scheduler.getPendingJob(JOB_ID) != null) return;

        ComponentName cn = new ComponentName(context, DailyProblemJobService.class);

        long interval = TimeUnit.DAYS.toMillis(1);
        long flex = TimeUnit.HOURS.toMillis(3);

        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, cn)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // necesita red para llamar API
                .setPersisted(false)
                .setPeriodic(interval, flex)
                .build();

        scheduler.schedule(jobInfo);
    }
}
