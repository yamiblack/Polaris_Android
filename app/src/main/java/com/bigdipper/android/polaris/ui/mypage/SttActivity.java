package com.bigdipper.android.polaris.ui.mypage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class SttActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity(C)";
    private Context context = this;

    private TextView tvShowWord;
    private EditText etWriteWord;
    private Button btnSetWord;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String email, word;
    private Boolean isSet = false;

    private MessageConsumer messageConsumer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stt);
        getSupportActionBar().setTitle("음성인식 단어 관리");

        tvShowWord = (TextView) findViewById(R.id.tv_stt_showWord);
        etWriteWord = (EditText) findViewById(R.id.et_stt_writeWord);
        btnSetWord = (Button) findViewById(R.id.btn_stt_setWord);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        connectGalaxyWatch();
        getWord();

        if (!isSet) {
            tvShowWord.setText("설정된 단어가 없습니다.");
            btnSetWord.setText("음성인식 단어 설정");
        }

        btnSetWord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setWord();
            }
        });

    }

    public void setWord() {
        if (etWriteWord.getText().toString().length() == 0) {
            Toast.makeText(context, "음성인식 단어를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {

            if (isSet) {
                deleteWord();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("word", etWriteWord.getText().toString());

            db.collection("STT").document(email)
                    .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    etWriteWord.setText(null);
                    getWord();
                    if (messageConsumer != null) {
                        messageConsumer.sendData("stt/" + word);

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

    public void getWord() {
        db.collection("STT").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.get("email").toString().equals(email)) {
                            word = documentSnapshot.get("word").toString();
                            isSet = true;
                            tvShowWord.setText("음성인식 단어 : " + word);
                            btnSetWord.setText("음성인식 단어 수정");
                        }
                    }
                }
            }
        });
    }

    public void deleteWord() {
        db.collection("STT").document(email)
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
