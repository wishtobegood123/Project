package com.example.spacecolonypioneers.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolonypioneers.R;
import com.example.spacecolonypioneers.model.Mission;
import com.example.spacecolonypioneers.model.enums.MissionModifier;

import java.util.ArrayList;
import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.MissionViewHolder> {
    private List<Mission> missionList;
    private OnMissionClickListener listener;

    public interface OnMissionClickListener {
        void onMissionClick(Mission mission);
    }

    public MissionAdapter(List<Mission> missionList, OnMissionClickListener listener) {
        this.missionList = missionList != null ? missionList : new ArrayList<Mission>();
        this.listener = listener;
    }

    public void updateList(List<Mission> newList) {
        missionList = newList != null ? newList : new ArrayList<Mission>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mission, parent, false);
        return new MissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        if (position < 0 || position >= missionList.size()) return;
        final Mission mission = missionList.get(position);
        if (mission == null) return;
        holder.tvMissionName.setText(mission.getName());
        holder.tvMissionType.setText(mission.getType().getDisplayName());
        String difficultyDesc = getDifficultyDescription(mission.getDifficulty());
        holder.tvDifficulty.setText("Difficulty: " + mission.getDifficulty() + "/5 (" + difficultyDesc + ")");
        String threatDesc = getThreatDescription(mission.getThreatLevel());
        holder.tvThreat.setText("Threat: " + mission.getThreatLevel() + " (" + threatDesc + ")");
        
        holder.tvRewards.setText("XP: " + mission.getRewardXp() + " | Fragments: " + mission.getRewardFragments() + " | Progress: " + mission.getRewardProgress());
        if (mission.getModifier() != null && mission.getModifier() != MissionModifier.NONE) {
            holder.tvModifier.setVisibility(View.VISIBLE);
            String modifierDesc = getModifierDescription(mission.getModifier());
            holder.tvModifier.setText("✨ " + mission.getModifier().getDisplayName() + "\n" + modifierDesc);
        } else {
            holder.tvModifier.setVisibility(View.GONE);
        }
        StringBuilder enemies = new StringBuilder("Enemies: ");
        for (int i = 0; i < mission.getEnemyTypes().size(); i++) {
            if (i > 0) enemies.append(", ");
            enemies.append(mission.getEnemyTypes().get(i));
        }
        holder.tvEnemies.setText(enemies.toString());
        int threatColor = mission.getThreatLevel() <= 3 ? Color.parseColor("#4caf50") : mission.getThreatLevel() <= 6 ? Color.parseColor("#ff9800") : Color.parseColor("#f44336");
        holder.tvThreat.setTextColor(threatColor);
        holder.itemView.setBackgroundColor(mission.isSelected() ? Color.parseColor("#3949ab") : Color.parseColor("#16213e"));
        holder.itemView.setAlpha(mission.isCompleted() ? 0.5f : 1.0f);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && !mission.isCompleted()) listener.onMissionClick(mission);
            }
        });
    }

    @Override
    public int getItemCount() {
        return missionList != null ? missionList.size() : 0;
    }
    private String getDifficultyDescription(int difficulty) {
        switch (difficulty) {
            case 1: return "Easy - Beginner Friendly";
            case 2: return "Normal - Moderate Challenge";
            case 3: return "Hard - Strong Squad Required";
            case 4: return "Expert - Very Challenging";
            case 5: return "Hell - Near Impossible";
            default: return "Unknown";
        }
    }
    private String getThreatDescription(int threatLevel) {
        if (threatLevel <= 3) return "Low Threat - Safe";
        if (threatLevel <= 6) return "Medium Threat - Caution";
        if (threatLevel <= 9) return "High Threat - Dangerous";
        return "Extreme Threat - Deadly";
    }
    private String getModifierDescription(MissionModifier modifier) {
        switch (modifier) {
            case DOUBLE_XP:
                return "XP Reward x2.0 | Difficulty x1.0";
            case DOUBLE_FRAGMENTS:
                return "XP Reward x1.0 | Fragment Reward x2.0";
            case TOUGH_ENEMIES:
                return "Stronger enemies | XP x1.2 | Difficulty x1.5";
            case FAST_MISSION:
                return "Quick strike | XP x0.8 | Difficulty x0.8";
            case ELITE_TEAM:
                return "Elite squad | XP x1.5 | Difficulty x1.5";
            default:
                return "No special effect";
        }
    }

    static class MissionViewHolder extends RecyclerView.ViewHolder {
        TextView tvMissionName, tvMissionType, tvDifficulty, tvThreat, tvRewards, tvModifier, tvEnemies;

        MissionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMissionName = itemView.findViewById(R.id.tvMissionName);
            tvMissionType = itemView.findViewById(R.id.tvMissionType);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvThreat = itemView.findViewById(R.id.tvThreat);
            tvRewards = itemView.findViewById(R.id.tvRewards);
            tvModifier = itemView.findViewById(R.id.tvModifier);
            tvEnemies = itemView.findViewById(R.id.tvEnemies);
        }
    }
}
