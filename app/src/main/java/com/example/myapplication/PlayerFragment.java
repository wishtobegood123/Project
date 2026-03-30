package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlayerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PlayerRepository repository = new PlayerRepository();
        List<Player> players = DataProvider.createSamplePlayers();
        for (Player player : players) {
            repository.add(player);
        }

        recyclerView.setAdapter(new PlayerAdapter(repository.getAll()));
        return recyclerView;
    }

    static class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.Holder> {
        private final List<Player> list;

        public PlayerAdapter(List<Player> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(50, 40, 50, 40);
            textView.setTextSize(18);
            return new Holder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Player player = list.get(position);
            holder.textView.setText(player.getName() + " | " + player.getPosition() + " | " + player.getTeamName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Player: " + player.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class Holder extends RecyclerView.ViewHolder {
            TextView textView;
            public Holder(@NonNull TextView itemView) {
                super(itemView);
                textView = itemView;
            }
        }
    }
}
