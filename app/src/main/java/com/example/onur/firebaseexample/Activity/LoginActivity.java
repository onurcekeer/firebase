package com.example.onur.firebaseexample.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;


import com.example.onur.firebaseexample.Gps.GPSTracker;
import com.example.onur.firebaseexample.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText E_email;
    EditText E_password;
    RelativeLayout container;
    AnimationDrawable anim;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    Snackbar hata;
    Snackbar basarili;
    ProgressDialog progress;
    CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        stopService(new Intent(LoginActivity.this,GPSTracker.class));

        mAuth = FirebaseAuth.getInstance();
        container = (RelativeLayout) findViewById(R.id.activity_main);

        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);

        Button buton_login = (Button)findViewById(R.id.button_login);
        Button button_register = (Button)findViewById(R.id.button_register);
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        E_email = (EditText) findViewById(R.id.editText);
        E_password = (EditText) findViewById(R.id.editText2);

        hata = Snackbar.make(container, "Lütfen internet bağlantınızı kontrol ediniz", Snackbar.LENGTH_LONG);
        basarili = Snackbar.make(container,"Kayıt işlemi başarılı",Snackbar.LENGTH_LONG);

        progress = new ProgressDialog(LoginActivity.this);
        progress.setMessage("Giriş Yapılıyor...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
             //   .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                startActivity(new Intent(LoginActivity.this, SimpleTabsActivity.class));
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                hata.show();
            }
        });

// ...


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(LoginActivity.this,SimpleTabsActivity.class));
                    Log.d("true","onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("false", "onAuthStateChanged:signed_out");
                }
                // ...
            }

        };


        buton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = E_email.getText().toString();
                String password = E_password.getText().toString();

                if (!E_email.getText().toString().isEmpty() && !E_password.getText().toString().isEmpty()) {

                    progress.show();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful())
                                hata.show();
                            else {
                                startActivity(new Intent(LoginActivity.this, SimpleTabsActivity.class));

                            }
                            progress.dismiss();
                        }
                    });
                } else {
                    if (E_email.getText().toString().isEmpty()) {
                        E_email.setError("Lütfen email giriniz");
                    } else
                        E_password.setError("Lütfen şifre giriniz");
                }

            }
        });



        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = E_email.getText().toString();
                String password = E_password.getText().toString();

                if (!E_email.getText().toString().isEmpty() && !E_password.getText().toString().isEmpty()) {

                    final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
                    progress.setMessage("Kullanıcı Oluşturuluyor...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        hata.show();
                                    } else {
                                        basarili.show();

                                    }
                                    progress.dismiss();
                                }
                            });
                }

            else{
                if (E_email.getText().toString().isEmpty()){
                    E_email.setError("Lütfen email giriniz");
                }
                else
                    E_password.setError("Lütfen şifre giriniz");
            }}
        });


        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        findViewById(R.id.facebook_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else {
                hata.show();
            }
        }

        if(requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()){
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }



    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        progress.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            hata.show();

                        }
                        else{
                            startActivity(new Intent(LoginActivity.this, SimpleTabsActivity.class));
                        }
                        progress.dismiss();
                    }
                });
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                        }
                        else{
                            startActivity(new Intent(LoginActivity.this,SimpleTabsActivity.class));
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onDestroy(){
        super.onDestroy();
        stopService(new Intent(LoginActivity.this,GPSTracker.class));
    }
}
