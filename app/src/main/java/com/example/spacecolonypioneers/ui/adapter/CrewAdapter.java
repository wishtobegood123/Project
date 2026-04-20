package com.example.spacecolonypioneers.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolonypioneers.R;
import com.example.spacecolonypioneers.model.CrewMember;

import java.util.ArrayList;
import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {
    private List<CrewMember> crewList;
    private OnCrewClickListener listener;

    public interface OnCrewClickListener {
        void onCrewClick(CrewMember crew);
    }

    public CrewAdapter(List<CrewMember> crewList, OnCrewClickListener listener) {
        this.crewList = crewList != null ? crewList : new ArrayList<CrewMember>();
        this.listener = listener;
    }

    public void updateList(List<CrewMember> newList) {
        crewList = newList != null ? newList : new ArrayList<CrewMember>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crew, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        if (position < 0 || position >= crewList.size()) return;
        final CrewMember crew = crewList.get(position);
        if (crew == null) return;
        String nameText = crew.getName();
        if (crew.isInjured()) {
            nameText += " (Injured)";
        }
        holder.tvCrewName.setText(nameText);
        holder.tvCrewProfession.setText(crew.getProfession().getDisplayName() + " Lv." + crew.getLevel());
        int daysUntilReady = crew.getDaysUntilCombatReady();
        if (daysUntilReady > 0) {
            holder.tvCombatCooldown.setVisibility(View.VISIBLE);
            holder.tvCombatCooldown.setText("⏰ Rest for " + daysUntilReady + " more day(s) before combat");
        } else {
            holder.tvCombatCooldown.setVisibility(View.GONE);
        }
        
        holder.pbCrewHp.setMax(crew.getMaxHp());
        holder.pbCrewHp.setProgress(crew.getHp());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onCrewClick(crew);
            }
        });
    }

    @Override
    public int getItemCount() {
        return crewList != null ? crewList.size() : 0;
    }

    static class CrewViewHolder extends RecyclerView.ViewHolder {
        TextView tvCrewName;
        TextView tvCrewProfession;
        TextView tvCombatCooldown;
        ProgressBar pbCrewHp;

        CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCrewName = itemView.findViewById(R.id.tvCrewName);
            tvCrewProfession = itemView.findViewById(R.id.tvCrewProfession);
            tvCombatCooldown = itemView.findViewById(R.id.tvCombatCooldown);
            pbCrewHp = itemView.findViewById(R.id.pbCrewHp);
        }
    }
}
