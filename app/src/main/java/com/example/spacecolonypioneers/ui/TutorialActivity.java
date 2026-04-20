package com.example.spacecolonypioneers.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.spacecolonypioneers.R;

public class TutorialActivity extends AppCompatActivity {
    
    private static final String PREF_TUTORIAL_SEEN = "tutorial_seen";
    private static final String PREFS_NAME = "GamePrefs";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        setupTutorialContent();
        Button btnClose = findViewById(R.id.btnCloseTutorial);
        btnClose.setOnClickListener(v -> {
            saveTutorialSeen();
            finish();
        });
    }
    
    private void setupTutorialContent() {
        TextView tvTutorial = findViewById(R.id.tvTutorialContent);
        
        String tutorialText = "🎮 Space Colony: Pioneers - Game Guide\n\n" +
            "📖 Overview\n" +
            "━━━━━━━━━━━━━━━━━━━━\n" +
            "You are the commander of a space colony. Manage crew, run missions, and survive alien threats.\n\n" +
            "🎯 Goals\n" +
            "━━━━━━━━━━━━━━━━━━━━\n" +
            "• Keep the colony growing as long as possible.\n" +
            "• Improve crew power through missions and training.\n" +
            "• Collect resources, fragments, and XP.\n" +
            "• Reach 100 fragments to win the game!\n\n" +
            "⚙️ Core Loop\n" +
            "━━━━━━━━━━━━━━━━━━━━\n" +
            "• Scheduling: assign crew to different areas.\n" +
            "• Progression: process daily effects and recover energy.\n" +
            "• Mission Selection: choose a mission and build squad.\n" +
            "• Combat: defeat enemies with attacks and skills.\n\n" +
            "🏠 Crew Management\n" +
            "━━━━━━━━━━━━━━━━━━━━\n" +
            "• Quarters: crew recovers full energy when returning here.\n" +
            "• Medic in quarters boosts recovery for all crew there.\n" +
            "• Simulator: crew gains XP and levels up faster.\n" +
            "• Mission Control: prepares crew for missions.\n" +
            "• Unassigned: idle crew ready for assignment.\n\n" +
            "💾 Persistence\n" +
            "━━━━━━━━━━━━━━━━━━━━\n" +
            "• XP is retained after missions.\n" +
            "• Energy fully restores in quarters.\n" +
            "• Injured crew needs rest before combat.\n\n" +
            "🎮 Controls\n" +
            "━━━━━━━━━━━━━━━━━━━━\n" +
            "• Tap crew to select and assign.\n" +
            "• Build a squad before starting a mission.\n" +
            "• In combat, select a unit and use Attack/Skill/End Turn.\n" +
            "• Use Save/Load/Next Phase/Stats from the bottom bar.\n\n" +
            "💡 Tips\n" +
            "━━━━━━━━━━━━━━━━━━━━\n" +
            "• Keep a balanced squad.\n" +
            "• Watch enemy intent before acting.\n" +
            "• Heal early and manage resources carefully.\n" +
            "• Place medic in quarters to boost team recovery.\n";
            
        tvTutorial.setText(tutorialText);
    }
    
    private void saveTutorialSeen() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(PREF_TUTORIAL_SEEN, true).apply();
    }
    public static boolean hasSeenTutorial(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_TUTORIAL_SEEN, false);
    }
}
