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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bigdipper.android.polaris.R;
import com.bigdipper.android.polaris.ui.membership.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import static android.app.Activity.RESULT_OK;

public class MyPageFragment extends Fragment {
    Button btnLogOut, btnSetVibrationPattern;
    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_mypage, container, false);

        btnLogOut = root.findViewById(R.id.btn_logout);
        imageview = root.findViewById(R.id.userImage);
        btnSetVibrationPattern = root.findViewById(R.id.btn_setVibrationPattern);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(container.getContext(), "로그아웃에 성공했습니다.", Toast.LENGTH_SHORT).show();
                startLogInActivity();
            }
        });

        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        btnSetVibrationPattern.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ModifyVibrationActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){

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