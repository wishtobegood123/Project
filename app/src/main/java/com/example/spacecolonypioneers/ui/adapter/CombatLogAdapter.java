package com.example.spacecolonypioneers.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolonypioneers.R;
import com.example.spacecolonypioneers.model.CombatLogEntry;

import java.util.ArrayList;
import java.util.List;

public class CombatLogAdapter extends RecyclerView.Adapter<CombatLogAdapter.LogViewHolder> {
    private List<CombatLogEntry> logList;

    public CombatLogAdapter(List<CombatLogEntry> logList) {
        this.logList = logList != null ? logList : new ArrayList<CombatLogEntry>();
    }

    public void updateList(List<CombatLogEntry> newList) {
        this.logList = newList != null ? newList : new ArrayList<CombatLogEntry>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_combat_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        if (position < 0 || position >= logList.size()) return;
        CombatLogEntry entry = logList.get(position);
        if (entry == null) return;
        String message = entry.getMessage() != null ? entry.getMessage() : "";
        holder.tvLog.setText(message);
        int color = Color.WHITE;
        float size = 16f;
        if (entry.getType() != null) {
            switch (entry.getType()) {
                case DAMAGE:
                    color = Color.parseColor("#ff9800");
                    break;
                case HEAL:
                    color = Color.parseColor("#4caf50");
                    break;
                case SKILL:
                    color = Color.parseColor("#2196f3");
                    break;
                case VICTORY:
                    color = Color.parseColor("#ffeb3b");
                    size = 20f;
                    break;
                case DEFEAT:
                    color = Color.parseColor("#f44336");
                    size = 20f;
                    break;
                default:
                    break;
            }
        }
        holder.tvLog.setTextColor(color);
        holder.tvLog.setTextSize(size);
    }

    @Override
    public int getItemCount() {
        return logList != null ? logList.size() : 0;
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvLog;

        LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLog = itemView.findViewById(R.id.tvLog);
        }
    }
}
