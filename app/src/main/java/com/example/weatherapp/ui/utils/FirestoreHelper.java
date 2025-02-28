package com.example.weatherapp.ui.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Context context;

    public FirestoreHelper(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void saveCityToFavorites(String cityName) {
        String userId = getUserId();
        if (userId == null) return;

        Map<String, Object> cityData = new HashMap<>();
        cityData.put("name", cityName);

        db.collection("users").document(userId)
                .collection("favoriteCities")
                .document(cityName)
                .set(cityData)
                .addOnSuccessListener(aVoid -> showToast("City added to favorites"))
                .addOnFailureListener(e -> showToast("Failed to add city: " + e.getMessage()));
    }

    public void removeCityFromFavorites(String cityName) {
        String userId = getUserId();
        if (userId == null) return;

        db.collection("users").document(userId)
                .collection("favoriteCities")
                .document(cityName)
                .delete()
                .addOnSuccessListener(aVoid -> showToast("City removed from favorites"))
                .addOnFailureListener(e -> showToast("Failed to remove city: " + e.getMessage()));
    }

    public void loadFavoriteCities(FavoriteCitiesCallback callback) {
        String userId = getUserId();
        if (userId == null) return;

        db.collection("users").document(userId)
                .collection("favoriteCities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> favoriteCities = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        favoriteCities.add(document.getString("name"));
                    }
                    callback.onSuccess(favoriteCities);
                })
                .addOnFailureListener(e -> showToast("Failed to load favorites: " + e.getMessage()));
    }

    private String getUserId() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            showToast("You must be logged in.");
        }
        return userId;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public interface FavoriteCitiesCallback {
        void onSuccess(List<String> favoriteCities);
    }
}
