package com.bigdipper.android.polaris.ui.mypage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.ui.membership.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import static android.app.Activity.RESULT_OK;

public class MyPageFragment extends Fragment {
    private final int GET_GALLERY_IMAGE = 200;

    private ImageView imageview;
    private TextView tvEmail;
    private Button btnSetVibrationPattern;
    private Button btnSetSTTWords;
    private Button btnLogOut;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_mypage, container, false);

        imageview = root.findViewById(R.id.userImage);
        tvEmail = root.findViewById(R.id.tv_email);
        btnSetVibrationPattern = root.findViewById(R.id.btn_setVibrationPattern);
        btnSetSTTWords = root.findViewById(R.id.btn_setSTTWords);
        btnLogOut = root.findViewById(R.id.btn_logout);

        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        tvEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        btnSetSTTWords.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), STTwordsActivity.class);
                startActivity(intent);
            }
        });

        btnSetVibrationPattern.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ModifyVibrationActivity.class);
                startActivity(intent);
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
        }
    }

    private void startLogInActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}