package com.example.weatherapp.ui.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.weatherapp.data.model.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class FirebaseUtils {

    private static FirebaseAuth auth;
    private static FirebaseFirestore db;

    public static FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static FirebaseFirestore getFirestore() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }

    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public static String getCurrentUserId(Context context) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            showToast(context, "You must be logged in.");
            return null;
        }
        return user.getUid();
    }

    public static void signUpUser(Context context, String fname, String lname, String email, String password, Runnable onSuccess, Runnable onFailure) {
        getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = getAuth().getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("firstName", fname);
                            userData.put("lastName", lname);
                            userData.put("email", email);

                            getFirestore().collection("users").document(userId).set(userData)
                                    .addOnSuccessListener(unused -> {
                                        showToast(context, "Registration Successful!");
                                        onSuccess.run();
                                    })
                                    .addOnFailureListener(e -> {
                                        showToast(context, "Failed to save user details: " + e.getMessage());
                                        onFailure.run();
                                    });
                        }
                    } else {
                        showToast(context, "Registration Failed: " + task.getException().getMessage());
                        onFailure.run();
                    }
                });
    }

    public static void loginUser(Context context, String email, String password, Runnable onSuccess, Runnable onFailure) {
        getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast(context, "Login Successful!");
                        onSuccess.run();
                    } else {
                        showToast(context, "Login Failed: " + task.getException().getMessage());
                        onFailure.run();
                    }
                });
    }

    public static void recoverPassword(Context context, String email) {
        getAuth().sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> showToast(context, "Password reset email sent!"))
                .addOnFailureListener(e -> showToast(context, "Error: " + e.getMessage()));
    }

    public static void saveCityToFavorites(Context context, String cityName) {
        String userId = getCurrentUserId(context);
        if (userId == null) return;

        Map<String, Object> cityData = new HashMap<>();
        cityData.put("name", cityName);

        getFirestore().collection("users").document(userId)
                .collection("favoriteCities").document(cityName)
                .set(cityData)
                .addOnSuccessListener(aVoid -> showToast(context, "City added to favorites"))
                .addOnFailureListener(e -> showToast(context, "Failed to add city: " + e.getMessage()));
    }

    public static void removeCityFromFavorites(Context context, String cityName) {
        String userId = getCurrentUserId(context);
        if (userId == null) return;

        getFirestore().collection("users").document(userId)
                .collection("favoriteCities").document(cityName)
                .delete()
                .addOnSuccessListener(aVoid -> showToast(context, "City removed from favorites"))
                .addOnFailureListener(e -> showToast(context, "Failed to remove city: " + e.getMessage()));
    }

    public interface FavoriteCitiesCallback {
        void onSuccess(List<String> favoriteCities);
    }

    public static void loadFavoriteCities(Context context, FavoriteCitiesCallback callback) {
        String userId = getCurrentUserId(context);
        if (userId == null) return;

        getFirestore().collection("users").document(userId)
                .collection("favoriteCities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> favoriteCities = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        favoriteCities.add(document.getString("name"));
                    }
                    callback.onSuccess(favoriteCities);
                })
                .addOnFailureListener(e -> showToast(context, "Failed to load favorites: " + e.getMessage()));
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }













    public static void submitWeatherPost(Context context, String text, String locationName, String weather, Runnable onSuccess, Runnable onFailure) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            showToast(context, "You must be logged in.");
            onFailure.run();
            return;
        }

        String userId = user.getUid();

        getFirestore().collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    String userName = document.getString("firstName");
                    if (userName == null) userName = "Unknown";

                    Map<String, Object> postData = new HashMap<>();
                    postData.put("userId", userId);
                    postData.put("userName", userName);
                    postData.put("locationName", locationName);
                    postData.put("weather", weather);
                    postData.put("text", text);
                    postData.put("timestamp", com.google.firebase.Timestamp.now());

                    getFirestore().collection("weather_posts")
                            .add(postData)
                            .addOnSuccessListener(unused -> {
                                showToast(context, "Post submitted!");
                                onSuccess.run();
                            })
                            .addOnFailureListener(e -> {
                                showToast(context, "Failed to submit post: " + e.getMessage());
                                onFailure.run();
                            });
                })
                .addOnFailureListener(e -> {
                    showToast(context, "Failed to fetch user info: " + e.getMessage());
                    onFailure.run();
                });
    }

    public interface WeatherPostsCallback {
        void onSuccess(List<PostModel> posts);
    }

    public static void loadWeatherPosts(Context context, WeatherPostsCallback callback) {
        if (context == null) return;
        getFirestore().collection("weather_posts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<PostModel> posts = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        PostModel post = doc.toObject(PostModel.class);
                        posts.add(post);
                    }
                    callback.onSuccess(posts);
                })
                .addOnFailureListener(e -> {
                    showToast(context, "Failed to load posts: " + e.getMessage());
                });
    }



}
