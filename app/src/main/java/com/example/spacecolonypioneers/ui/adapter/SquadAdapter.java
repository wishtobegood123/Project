package com.example.spacecolonypioneers.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolonypioneers.R;
import com.example.spacecolonypioneers.model.CrewMember;

import java.util.ArrayList;
import java.util.List;

public class SquadAdapter extends RecyclerView.Adapter<SquadAdapter.SquadViewHolder> {
    private List<CrewMember> squadList;
    private OnSquadMemberClickListener listener;

    public interface OnSquadMemberClickListener {
        void onSquadMemberClick(CrewMember crew);
    }

    public SquadAdapter(List<CrewMember> squadList, OnSquadMemberClickListener listener) {
        this.squadList = squadList != null ? squadList : new ArrayList<CrewMember>();
        this.listener = listener;
    }

    public void updateList(List<CrewMember> newList) {
        squadList = newList != null ? newList : new ArrayList<CrewMember>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SquadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_squad, parent, false);
        return new SquadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SquadViewHolder holder, int position) {
        if (position < 0 || position >= squadList.size()) return;
        final CrewMember crew = squadList.get(position);
        if (crew == null) return;
        holder.tvSquadName.setText(crew.getName() + (crew.isInjured() ? " (Injured)" : ""));
        holder.tvSquadProfession.setText(crew.getProfession().getDisplayName() + " Lv." + crew.getLevel());
        holder.tvSquadHp.setText("HP: " + crew.getHp() + "/" + crew.getMaxHp());
        holder.itemView.setAlpha(crew.isInjured() ? 0.5f : 1.0f);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && !crew.isInjured()) listener.onSquadMemberClick(crew);
            }
        });
    }

    @Override
    public int getItemCount() {
        return squadList != null ? squadList.size() : 0;
    }

    static class SquadViewHolder extends RecyclerView.ViewHolder {
        TextView tvSquadName, tvSquadProfession, tvSquadHp;

        SquadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSquadName = itemView.findViewById(R.id.tvSquadName);
            tvSquadProfession = itemView.findViewById(R.id.tvSquadProfession);
            tvSquadHp = itemView.findViewById(R.id.tvSquadHp);
        }
    }
}
