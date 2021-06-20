package com.bigdipper.android.polaris.ui.favorite;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.adapter.FavoriteRecyclerViewAdapter;
import com.bigdipper.android.polaris.adapter.ItemClickSupport;
import com.bigdipper.android.polaris.entity.FavoriteData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    private ArrayList<FavoriteData> arrayList;
    private RecyclerView rvFavorite;
    private FavoriteRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String email;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);

        rvFavorite = (RecyclerView) root.findViewById(R.id.rv_favorite);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        email = auth.getCurrentUser().getEmail();

        getFavorite();

        return root;
    }

    public void getFavorite() {
        arrayList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());
        rvFavorite.setLayoutManager(layoutManager);
        adapter = new FavoriteRecyclerViewAdapter(getActivity(), arrayList);
        rvFavorite.setAdapter(adapter);

        db.collection("FAVORITE").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.get("email").toString().equals(email)) {
                            FavoriteData favorite = documentSnapshot.toObject(FavoriteData.class);
                            arrayList.add(favorite);
                        }
                    }

                    for (int i = 0; i < arrayList.size(); i++) {
                        arrayList.get(i).setNumber(String.valueOf(i + 1));
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


}