package com.example.pmd_proyecto;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.example.pmd_proyecto.model.EnunciadoProblema;

public class DailyProblemJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        new Thread(() -> {
            try {
                EnunciadoProblema daily = NetUtils.ConsultarDailyProblem();
                if (daily != null) {
                    DBHelper db = DBHelper.getInstance(getApplicationContext());
                    db.guardarProblemaDia(daily);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                jobFinished(params, false);
            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Pedir reintento
        return true;
    }
}
