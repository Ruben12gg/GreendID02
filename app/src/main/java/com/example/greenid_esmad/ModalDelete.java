package com.example.greenid_esmad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ModalDelete extends DialogFragment {

    FirebaseStorage storage;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // "Build" dialog with Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure that you want to delete your account? All your profile data will be deleted!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("DELETE", "Deleting account!!");

                        //Access user Id from GLOBALS
                        GLOBALS globalUserId = (GLOBALS) getContext().getApplicationContext();
                        String userId = globalUserId.getUserIdGlobal();

                        Log.d("GLOBALID", userId);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        //Delete profile Data
                        db.collection("users").document(userId).delete();

                        //Delete posts
                        db.collection("users").document(userId).collection("posts")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                String postId = document.getId();
                                                db.collection("users").document(userId).collection("posts").document(postId).delete();

                                            }
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                        }

                                    }
                                });

                        //Delete following
                        db.collection("users").document(userId).collection("following")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                String followingId = document.getId();
                                                Log.d("FOLLOWING", followingId);
                                                db.collection("users").document(userId).collection("following").document(followingId).delete();

                                            }
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                        }

                                    }
                                });

                        //Delete followers
                        db.collection("users").document(userId).collection("followers")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                String followerId = document.getId();
                                                db.collection("users").document(userId).collection("followers").document(followerId).delete();

                                            }
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                        }

                                    }
                                });

                        //Delete favorites
                        db.collection("users").document(userId).collection("favorites")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                String favoriteId = document.getId();
                                                db.collection("users").document(userId).collection("posts").document(favoriteId).delete();

                                            }
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                        }

                                    }
                                });

                        //Delete notifications
                        db.collection("users").document(userId).collection("notifications")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                String notificationId = document.getId();
                                                db.collection("users").document(userId).collection("notifications").document(notificationId).delete();

                                            }
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                        }

                                    }
                                });


                        // Create a storage reference from our app
                        storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();

                        // Create a reference to the file to delete
                        StorageReference userImagesRef = storageRef.child("images/" + userId);

                        // Delete user image stored files
                        userImagesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                                Log.d("ERROR STORAGE", exception.toString());

                            }
                        });


                        String message = "Account deleted successfully!";
                        Context context = getContext().getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, message, duration);
                        toast.show();

                        Logout();


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void Logout() {

        //Logout user from his google account to prevent login cycle into the deleted account on next sign in
        GoogleSignInClient mGoogleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext().getApplicationContext(), gso);
        mGoogleSignInClient.signOut();

        //Go back to login screen
        Intent intent = new Intent(getContext(), Login.class);
        intent.putExtra("deletedAcc", true);
        startActivity(intent);
    }
}
