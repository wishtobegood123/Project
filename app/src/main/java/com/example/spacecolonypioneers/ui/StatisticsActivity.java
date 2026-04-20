package com.example.spacecolonypioneers.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spacecolonypioneers.R;
import com.example.spacecolonypioneers.model.CrewStatistics;
import com.example.spacecolonypioneers.model.GameState;
import com.example.spacecolonypioneers.model.GameStatistics;
import com.example.spacecolonypioneers.ui.view.BarChartView;
import com.example.spacecolonypioneers.ui.view.PieChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {
    private GameStatistics statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statistics = GameState.getInstance().getStatistics();
        if (statistics == null) statistics = new GameStatistics();

        TextView tvTotalMissions = findViewById(R.id.tvTotalMissions);
        TextView tvSuccessfulMissions = findViewById(R.id.tvSuccessfulMissions);
        TextView tvSuccessRate = findViewById(R.id.tvSuccessRate);
        TextView tvTotalTraining = findViewById(R.id.tvTotalTraining);
        TextView tvTotalXp = findViewById(R.id.tvTotalXp);
        TextView tvTotalFragments = findViewById(R.id.tvTotalFragments);
        PieChartView pieChartMissions = findViewById(R.id.pieChartMissions);
        BarChartView barChartDamage = findViewById(R.id.barChartDamage);
        BarChartView barChartHealing = findViewById(R.id.barChartHealing);
        BarChartView barChartMissionsPerCrew = findViewById(R.id.barChartMissionsPerCrew);
        Button btnBack = findViewById(R.id.btnBackFromStats);

        tvTotalMissions.setText("Total Missions: " + statistics.getTotalMissions());
        tvSuccessfulMissions.setText("Wins: " + statistics.getSuccessfulMissions());
        tvSuccessRate.setText(String.format(Locale.getDefault(), "Success Rate: %.1f%%", statistics.getSuccessRate()));
        tvTotalTraining.setText("Training Sessions: " + statistics.getTotalTrainingSessions());
        tvTotalXp.setText("Total XP Gained: " + statistics.getTotalXpEarned());
        tvTotalFragments.setText("Total Fragments: " + statistics.getTotalFragmentsCollected());

        List<PieChartView.PieData> pieData = new ArrayList<PieChartView.PieData>();
        int failed = statistics.getTotalMissions() - statistics.getSuccessfulMissions();
        if (statistics.getSuccessfulMissions() > 0) {
            pieData.add(new PieChartView.PieData("Wins", statistics.getSuccessfulMissions(), Color.parseColor("#4caf50")));
        }
        if (failed > 0) {
            pieData.add(new PieChartView.PieData("Losses", failed, Color.parseColor("#f44336")));
        }
        if (pieData.isEmpty()) {
            pieData.add(new PieChartView.PieData("No Data", 1, Color.parseColor("#9e9e9e")));
        }
        pieChartMissions.setData(pieData, "Mission Success Rate");

        int[] colors = { Color.parseColor("#f44336"), Color.parseColor("#2196f3"), Color.parseColor("#4caf50"), Color.parseColor("#ff9800"), Color.parseColor("#9c27b0") };
        List<BarChartView.BarData> damageData = new ArrayList<BarChartView.BarData>();
        List<BarChartView.BarData> healingData = new ArrayList<BarChartView.BarData>();
        List<BarChartView.BarData> missionsData = new ArrayList<BarChartView.BarData>();
        List<CrewStatistics> crewStats = statistics.getAllCrewStats();
        if (crewStats != null) {
            for (int i = 0; i < crewStats.size(); i++) {
                CrewStatistics item = crewStats.get(i);
                if (item == null) continue;
                int color = colors[i % colors.length];
                String name = item.getCrewName() != null ? item.getCrewName() : String.valueOf(i + 1);
                damageData.add(new BarChartView.BarData(name, item.getTotalDamageDealt(), color));
                healingData.add(new BarChartView.BarData(name, item.getTotalHealingDone(), color));
                missionsData.add(new BarChartView.BarData(name, item.getMissionsCompleted(), color));
            }
        }
        barChartDamage.setData(damageData, "Total Damage Output");
        barChartHealing.setData(healingData, "Total Healing");
        barChartMissionsPerCrew.setData(missionsData, "Combat Deployments");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
