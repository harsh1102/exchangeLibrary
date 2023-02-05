package com.example.exchangelibrary;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;

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

import java.util.concurrent.Executor;

public class RegisterFragment extends Fragment {

    public String username;
    public ProgressBar probar;
    public FirebaseAuth mAuth;
//    public int duration = Toast.LENGTH_SHORT;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.register_activity,null);
        EditText name = (EditText) root.findViewById(R.id.et_name);
        EditText email = (EditText) root.findViewById(R.id.et_email);
        EditText password = (EditText) root.findViewById(R.id.et_password);
        EditText confirmpassword = (EditText) root.findViewById(R.id.et_repassword);
        Button registerBtn = (Button) root.findViewById(R.id.btn_register);
        probar = (ProgressBar) root.findViewById(R.id.progressbar);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailid = email.getText().toString();
                String passd = password.getText().toString();
                String username = name.getText().toString();
                String confPasswd = confirmpassword.getText().toString();
                String emailRegx = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (TextUtils.isEmpty(emailid) && TextUtils.isEmpty(passd) && TextUtils.isEmpty(username) && TextUtils.isEmpty(confPasswd)) {
                    Toast.makeText(getActivity(),"Field can't be empty",Toast.LENGTH_SHORT).show();
                }
                else{
                    if (emailid.matches(emailRegx)){
                        if (passd.length()>=6){
                            if (passd.equals(confPasswd)){
                                probar.setVisibility(View.VISIBLE);
                                createAccount(emailid,passd, username);
                            }
                            else {
                                Toast.makeText(getActivity(),"Password is not matching",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), " Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getActivity(),"Invalid email input",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return root;
    }


    private void createAccount(String email, String password, String username) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getActivity(),"User:"+user.getEmail()+"registered  in successfully",Toast.LENGTH_SHORT).show();
                            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                            user.updateProfile(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        probar.setVisibility(View.GONE);
                                        Log.d("Success", "User profile is updated");
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(getActivity(),"" +task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}