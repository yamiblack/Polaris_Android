package com.bigdipper.android.polaris.ui.recent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.adapter.FavoriteRecyclerViewAdapter;
import com.bigdipper.android.polaris.adapter.RecentRecyclerViewAdapter;
import com.bigdipper.android.polaris.entity.FavoriteData;
import com.bigdipper.android.polaris.entity.RecentData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecentFragment extends Fragment {

    private ArrayList<RecentData> arrayList;
    private RecyclerView rvRecent;
    private RecentRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String email;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recent, container, false);

        rvRecent = (RecyclerView) root.findViewById(R.id.rv_recent);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        email = auth.getCurrentUser().getEmail();

        getRecent();

        return root;
    }

    public void getRecent() {
        arrayList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        rvRecent.setLayoutManager(layoutManager);
        adapter = new RecentRecyclerViewAdapter(getActivity(), arrayList);
        rvRecent.setAdapter(adapter);

        db.collection("RECENT").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.get("email").toString().equals(email)) {
                            RecentData recentData = documentSnapshot.toObject(RecentData.class);
                            arrayList.add(recentData);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

}