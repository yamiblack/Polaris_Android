package com.bigdipper.android.polaris.ui.mypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.ui.membership.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MyPageFragment extends Fragment {
    Button btnLogOut;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mypage, container, false);

        btnLogOut = root.findViewById(R.id.btn_logout);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(container.getContext(), "로그아웃에 성공했습니다.", Toast.LENGTH_SHORT).show();
                startLogInActivity();
            }
        });
        return root;
    }


    private void startLogInActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}