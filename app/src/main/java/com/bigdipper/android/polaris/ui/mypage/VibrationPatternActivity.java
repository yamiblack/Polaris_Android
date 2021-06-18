package com.bigdipper.android.polaris.ui.mypage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bigdipper.android.polaris.MessageConsumer;
import com.bigdipper.android.polaris.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.samsung.android.sdk.accessory.SAAgentV2;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VibrationPatternActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity(C)";
    private Context context = this;

    private EditText etStraight;
    private EditText etTen;
    private EditText etTwo;
    private EditText etLeft;
    private EditText etRight;
    private EditText etEight;
    private EditText etFour;

    private Button btnSetPattern;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private MessageConsumer messageConsumer = null;

    private String email;
    private Boolean isSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrationpattern);
        getSupportActionBar().setTitle("진동 패턴 설정");

        etStraight = (EditText) findViewById(R.id.et_vibration_straight);
        etTen = (EditText) findViewById(R.id.et_vibration_ten);
        etTwo = (EditText) findViewById(R.id.et_vibration_two);
        etLeft = (EditText) findViewById(R.id.et_vibration_left);
        etRight = (EditText) findViewById(R.id.et_vibration_right);
        etEight = (EditText) findViewById(R.id.et_vibration_eight);
        etFour = (EditText) findViewById(R.id.et_vibration_four);

        btnSetPattern = (Button) findViewById(R.id.btn_setVibrationPattern);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        connectGalaxyWatch();
        getPattern();

        if (!isSet) {
            btnSetPattern.setText("설정 완료");
        }

        btnSetPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPattern();
            }
        });

    }

    public void setPattern() {
        if (etStraight.getText().toString().length() == 0 || etTen.getText().toString().length() == 0 ||
                etTwo.getText().toString().length() == 0 || etLeft.getText().toString().length() == 0 ||
                etRight.getText().toString().length() == 0 || etEight.getText().toString().length() == 0 ||
                etFour.getText().toString().length() == 0) {
            Toast.makeText(context, "빈칸없이 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if (isSet) {
                deletePattern();
            }

            Map<String, Object> data = new HashMap<>();

            data.put("email", email);
            data.put("straight", etStraight.getText().toString());
            data.put("left", etLeft.getText().toString());
            data.put("right", etRight.getText().toString());
            data.put("two", etTwo.getText().toString());
            data.put("four", etFour.getText().toString());
            data.put("eight", etEight.getText().toString());
            data.put("ten", etTen.getText().toString());

            db.collection("VIBRATIONPATTERN").document(email)
                    .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    etStraight.setText(null);
                    etLeft.setText(null);
                    etRight.setText(null);
                    etTwo.setText(null);
                    etFour.setText(null);
                    etEight.setText(null);
                    etTen.setText(null);

                    getPattern();
                    if (messageConsumer != null) {
                        messageConsumer.sendData("guide/" + etStraight.getText().toString() + "/straight");
                        messageConsumer.sendData("guide/" + etLeft.getText().toString() + "/left");
                        messageConsumer.sendData("guide/" + etRight.getText().toString() + "/right");
                        messageConsumer.sendData("guide/" + etTwo.getText().toString() + "/two");
                        messageConsumer.sendData("guide/" + etFour.getText().toString() + "/four");
                        messageConsumer.sendData("guide/" + etEight.getText().toString() + "/eight");
                        messageConsumer.sendData("guide/" + etTen.getText().toString() + "/ten");

                        Toast.makeText(context, "성공적으로 설정됐습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "갤럭시워치 연결을 확안해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void getPattern() {
        db.collection("VIBRATIONPATTERN").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.get("email").toString().equals(email)) {
                            isSet = true;

                            etStraight.setText(documentSnapshot.get("straight").toString());
                            etLeft.setText(documentSnapshot.get("left").toString());
                            etRight.setText(documentSnapshot.get("right").toString());
                            etTwo.setText(documentSnapshot.get("two").toString());
                            etFour.setText(documentSnapshot.get("four").toString());
                            etEight.setText(documentSnapshot.get("eight").toString());
                            etTen.setText(documentSnapshot.get("ten").toString());

                            btnSetPattern.setText("설정 수정");
                        }
                    }
                }
            }
        });

    }

    public void deletePattern() {
        db.collection("VIBRATIONPATTERN").document(email)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private SAAgentV2.RequestAgentCallback agentCallback = new SAAgentV2.RequestAgentCallback() {
        @Override
        public void onAgentAvailable(SAAgentV2 agent) {
            messageConsumer = (MessageConsumer) agent;
        }

        @Override
        public void onError(int errorCode, String message) {
            Log.e(TAG, "Agent initialization error: " + errorCode + ". ErrorMsg: " + message);
        }
    };

    private void connectGalaxyWatch() {
        Log.e("connection1", "success");
        SAAgentV2.requestAgent(getApplicationContext(), MessageConsumer.class.getName(), agentCallback);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (messageConsumer != null) {
                        messageConsumer.findPeers();
                        Log.e("connection2", "success");
                        break;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    super.run();
                }
            }
        }.start();
    }


}
