package com.example.exchangelibrary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    public FirebaseAuth mAuth;
    public ProgressBar probar;

    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_activity,null);
        EditText email = (EditText) root.findViewById(R.id.et_email);
        EditText password = (EditText) root.findViewById(R.id.et_password);
        Button loginBtn = (Button) root.findViewById(R.id.btn_login);
        probar = (ProgressBar) root.findViewById(R.id.progressbar);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
        } else {
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String emailid = email.getText().toString();
                    String passd = password.getText().toString();

                    if (TextUtils.isEmpty(emailid) && TextUtils.isEmpty(passd)) {
                        Toast.makeText(getActivity(),"Field can't be empty",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        probar.setVisibility(View.VISIBLE);
                        loginAccount(emailid,passd);
                    }
                }
            });
        }
        return root;
    }

    public void loginAccount(String email, String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    probar.setVisibility(View.GONE);
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(),"User:"+user.getEmail()+"logged in successfully",Toast.LENGTH_SHORT).show();
                } else {
                    probar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"" +task.getException(),Toast.LENGTH_SHORT).show();
                    Log.e("Error", "signInWithEmail:failure", task.getException());
                }
            }
        });
    }
}