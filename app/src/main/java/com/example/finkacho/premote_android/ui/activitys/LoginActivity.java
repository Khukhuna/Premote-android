package com.example.finkacho.premote_android.ui.activitys;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.finkacho.premote_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.log_mail) TextInputEditText mailInput;
    @BindView(R.id.log_password) TextInputEditText passInput;
    @BindView(R.id.login_layout) RelativeLayout layout;
    Animation shakeAnimation;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // bind views to their variables
        ButterKnife.bind(this);
        // initialize firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
    }


    @OnClick(R.id.login)
    public void onLoginClicked(View view){
        // animate button, so user will understand that it's clicked
        AlphaAnimation animation = new AlphaAnimation(1F, 0.8F);
        view.startAnimation(animation);

        // validate input
        String mail = mailInput.getText().toString();
        String password = passInput.getText().toString();

        boolean error = false;


        if(mail.isEmpty()){
            mailInput.setError(getResources().getString(R.string.enterMail));
            mailInput.startAnimation(shakeAnimation);
            error = true;
        }else if(!mail.contains("@") || mail.length() < 5){
            mailInput.setError(getResources().getString(R.string.wrongMailFormat));
            mailInput.startAnimation(shakeAnimation);
            error = true;
        }

        if(password.isEmpty()){
            passInput.setError(getResources().getString(R.string.enterPassword));
            passInput.startAnimation(shakeAnimation);
            error = true;
        }else if(password.length() < 5){
            passInput.setError(getResources().getString(R.string.wrongPasswordFormat));
            passInput.startAnimation(shakeAnimation);
            error = true;
        }

        if(error){
            return;
        }

        login(mail, password);

    }

    private void login(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // catch error & display
                        String error = task.getException()==null? getString(R.string.unknownError): task.getException().getLocalizedMessage();
                        Snackbar.make(layout, error, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            finish();
        }
    }

    @Override
    protected void onStart() {
        // check user status on start
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        updateUI(cUser);
    }


    @OnClick({ R.id.passRecover, R.id.signUp })
    public void onButtonClicked(Button btn){
        Intent intent;

        if(btn.getId() == R.id.passRecover){
            intent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
        }else{
            intent = new Intent(LoginActivity.this, SignUpActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
